package com.berg.mylegislator;

import javax.xml.parsers.ParserConfigurationException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.berg.mylegislator.adapters.LegislatorAdapter;
import com.berg.mylegislator.apis.GovTrackPerson;
import com.berg.mylegislator.apis.Sunlight;
import com.berg.mylegislator.view.InnerPanel;
import com.berg.mylegislator.view.Panel;

public class MyLegislator extends Activity {
	
	//Menu Options
	static final private int RESET = R.id.reset;
	static final private int REQUERY = R.id.requery_legislators;
	private Handler mHandler = new Handler();
	private Resources myResources;
	private String welcomeMessage;
	private String regularMessage;
	//Basic Variables
	private Context mContext;
	private int mSuccess;
	private String zipcode;
	private int mCursorCount;
	//DataBase Variables
	private MyLegislatorDatabaseAdapter dBAdapter;
	private Cursor myCursor;
	//ListView Variables
	private SimpleCursorAdapter myAdapter;
	private static final int[] toLayoutIDs = new int[] {R.id.party_icon, R.id.first_name, R.id.party};
	private static final String[] fromColumns = new String[] {MyLegislatorDatabaseAdapter.KEY_FIRST_NAME, 
			MyLegislatorDatabaseAdapter.KEY_MIDDLE_NAME, MyLegislatorDatabaseAdapter.KEY_LAST_NAME, 
			MyLegislatorDatabaseAdapter.KEY_PARTY, MyLegislatorDatabaseAdapter.KEY_DISTRICT};
	//Views
	private ListView legislatorsList;
	private TextView title;
	private Panel panel, noLegislatorPanel;
	private Button continueButton;
	//Dialog Variables
	private ProgressDialog requeryDialog = null; 
	private Builder areYouSureDialog = null;
	
	private static final String INPUT_ZIP = "com.berg.mylegislator.INPUT_ZIP";
	public static final String INPUT_SCREEN_TITLE = "com.berg.mylegislator.INPUT_SCREEN_TITLE";
	public static final String INPUT_SCREEN_MESSAGE = "com.berg.mylegislator.INPUT_SCREEN_MESSAGE";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		setContentView(R.layout.main);
        this.mContext = this;
        this.zipcode = Settings.getUserZip(this);
        dBAdapter = new MyLegislatorDatabaseAdapter(this);
	    dBAdapter.open();
	    myResources = getResources();
	    welcomeMessage = myResources.getString(R.string.welcome_message);
	    regularMessage = myResources.getString(R.string.regular_message);
		myCursor = dBAdapter.fetchAllRepresenatives();
		startManagingCursor(myCursor);
		mCursorCount = myCursor.getCount();
		setListView();
		title = (TextView)findViewById(R.id.main_title);
		panel = (Panel)findViewById(R.id.main_panel);
		noLegislatorPanel =  (Panel)findViewById(R.id.no_legislators_panel);
		InnerPanel innerPanel = (InnerPanel)findViewById(R.id.legislator_inner_panel);
		innerPanel.setBackgroundColor(0XFF777777);
		title.setText("Legislators for zipcode " + zipcode); 
		if (Settings.getHasBeenOpened(this))  {
			startInputZipActivity("Welcome to My Legislators", welcomeMessage);
		}
		else 
			showCorrectPanel();    	
    }
    
    private void startInputZipActivity(String title, String message) {
    	Intent i = new Intent(INPUT_ZIP);
		Bundle inputZipExtras = new Bundle();
		inputZipExtras.putString(MyLegislator.INPUT_SCREEN_TITLE, title);
		inputZipExtras.putString(MyLegislator.INPUT_SCREEN_MESSAGE, message);
		i.putExtras(inputZipExtras);
		startActivity(i);
    }

    @Override
    public void onResume() {
    	super.onResume();
    	myCursor.requery();
        myAdapter.notifyDataSetChanged();
		mCursorCount = myCursor.getCount();
		showCorrectPanel();    	
		title.setText("Legislators for zipcode " + Settings.getUserZip(mContext));
    }
    
    @Override
    public void onDestroy() {
    	myCursor.close();
    	dBAdapter.close();
		super.onDestroy(); 
    }
    
// =========================================================== 
// View Methods
// =========================================================== 
    
    public void showCorrectPanel() {
    	if (mCursorCount > 0) {
			panel.setVisibility(TextView.VISIBLE);
			noLegislatorPanel.setVisibility(TextView.GONE);

		}
		else {
			noLegislatorPanel.setVisibility(TextView.VISIBLE);
			panel.setVisibility(TextView.GONE);
			continueButton =  (Button)findViewById(R.id.continue_to_welcome_intent);
			continueButton.setOnClickListener(new View.OnClickListener() { 
		          public void onClick(View view) { 
		        	noLegislatorPanel.setVisibility(View.GONE);
					startInputZipActivity("Input your zipcode", regularMessage);
		          } 
		        });
		}    
    }
    
    public void setListView() {
    	myAdapter = new LegislatorAdapter(this, 
                R.layout.my_legislator_main_rows, 
                myCursor, 
                fromColumns, 
                toLayoutIDs); 
		legislatorsList = (ListView)findViewById(R.id.legislators_list);
		legislatorsList.setAdapter(myAdapter);
		//Set onClick listener. TODO make this it's own method
		legislatorsList.setOnItemClickListener(new OnItemClickListener() { 
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Move the cursor to the selected item 
				myCursor.moveToPosition(position); 
				// Extract the row id. 
				long rowId = myCursor.getInt(myCursor.getColumnIndexOrThrow("_id")); 
				// Construct the result URI.  
				Intent Legislator_Panel = new Intent(mContext, Legislator.class); 
				Bundle extras = new Bundle();
				extras.putLong(Legislator.LEGISLATOR_ID, rowId); 
				Legislator_Panel.putExtras(extras);
				startActivity(Legislator_Panel);
			} 
		  });
    }
    
 // =========================================================== 
 // Menu Methods
 // =========================================================== 
    
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) { 
	  super.onCreateOptionsMenu(menu); 
	  // Create and add new menu items. 
	  MenuInflater inflater = getMenuInflater(); 
	  inflater.inflate(R.menu.main, menu);
	  return true; 
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item); 
		switch(item.getItemId()) { 
    	case (RESET): {
    		dBAdapter.truncateDatabase();
       	 	myCursor.requery();
    		mCursorCount = 0;
        	panel.setVisibility(View.GONE);
       	 	resetToast();
       	 	mHandler.removeCallbacks(mStartZipInputIntent);
			mHandler.postDelayed(mStartZipInputIntent, 5000);
    		return true; 
    		}
    	case (REQUERY): {
    		dBAdapter.truncateDatabase();
    		showAreYouSureDialog();
    		return true;
    	}
    	}
    	return false;
    }
	
	@Override 
	public boolean onPrepareOptionsMenu(Menu menu) { 
	  MenuItem requeryOption = menu.findItem(REQUERY);
	  MenuItem resetOption = menu.findItem(RESET);
	  Boolean hasQueriedLegislators = mCursorCount > 0;
	  showOrHide(requeryOption, hasQueriedLegislators);
	  showOrHide(resetOption, hasQueriedLegislators);
	  return true; 
	}	
	
	public void showOrHide(MenuItem _menuItem, boolean _trueOrFalse) {
		_menuItem.setVisible(_trueOrFalse);
		_menuItem.setEnabled(_trueOrFalse);
	}
	
	public void resetToast() {
		int duration = Toast.LENGTH_SHORT;
		String output = "My Legislators has been reset to Factory Settings.";
		Toast toast = Toast.makeText(mContext, output, duration); 
		toast.setGravity(Gravity.BOTTOM, 0, 25);
		toast.show();
	}
    
    private void outputResult(int outputCode) {
		 switch (outputCode) {
		 	case 1: {
		 		requeryDialog.setMessage("Contacting GovTrack");
		 		govTrackProcessing(); 
		 		break;
		 	}
		 	case 2: {
				 dBAdapter.truncateDatabase();
				 requeryDialog.setMessage("Error No Connection found");
				 mHandler.removeCallbacks(mShowLostConnectionDialog);
				 mHandler.postDelayed(mShowLostConnectionDialog, 1000);
				 break;
			 }
		 	case 3: {
				 break;
			 }
		 	case 4: {
		 		requeryDialog.dismiss();
				myCursor.requery();
		        myAdapter.notifyDataSetChanged();
		 		break;
		 	}
		 }
	} 

// =========================================================== 
// Dialog Methods
// ===========================================================
    
    public void lostConntectionDialog() {
		areYouSureDialog = new AlertDialog.Builder(this); 
		areYouSureDialog.setTitle("Lost Connection");
		areYouSureDialog.setMessage("Requery legislators for zipcode " + zipcode + " requires that the database be wiped is this Okay?");
		areYouSureDialog.setPositiveButton("OK", new 
				DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int whichButton) {
				mHandler.removeCallbacks(mShowProcessDialog);
				mHandler.postDelayed(mShowProcessDialog, 1000);
            } 
        }) 
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int whichButton) { 
            } 
        }) 
        .create();
		areYouSureDialog.show();
	}
	
	public void showAreYouSureDialog() {
		areYouSureDialog = new AlertDialog.Builder(this); 
		areYouSureDialog.setTitle("Requery Legislators?");
		areYouSureDialog.setMessage("Requery legislators for zipcode " + zipcode + " requires that the database be wiped is this Okay?");
		areYouSureDialog.setPositiveButton("OK", new 
				DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int whichButton) {
				mHandler.removeCallbacks(mShowProcessDialog);
				mHandler.postDelayed(mShowProcessDialog, 2000);
            } 
        }) 
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int whichButton) { 
            } 
        }) 
        .create();
		areYouSureDialog.show();
	}
	
	private void showDialog() {
		requeryDialog = ProgressDialog.show(this,      
                "Please wait...", "Requery for zipcode " + Settings.getUserZip(mContext), true);
		sunlightProcessing();
	}
    
// =========================================================== 
// API Contact Methods
// ===========================================================
	
    
    public void contactGovTrackPersonApi() {
		Context context = mContext;
		Cursor LegislatorCursor = dBAdapter.fetchAllRepresenatives();
		if (LegislatorCursor.moveToFirst())
	    do {
	    	long legislatorId = LegislatorCursor.getLong(0);
	    	long govTrackId = LegislatorCursor.getLong(MyLegislatorDatabaseAdapter.GOVTRACK_ID_COLUMN);
	    	if (!(govTrackId == 0)) {
		 		try {
					mSuccess = GovTrackPerson.digestFromPersonFromGovTrack(context, govTrackId, legislatorId);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mSuccess == 2)
					break;
	    	}
	    } while(LegislatorCursor.moveToNext());
		LegislatorCursor.deactivate();
		LegislatorCursor.close();
	 	mHandler.post(doOutputResult);
	}
    
 	public void contactSunlightApi(String zipcode) {
 	 	mSuccess= Sunlight.findCongressLegislators(zipcode, getBaseContext());
 	 	mHandler.post(doOutputResult);
 	}
    
 // =========================================================== 
 // Runnable Methods for Children Threads
 // =========================================================== 
 	
 	private void sunlightProcessing() { 
 		  Thread thread = new Thread(null, doBackgroundContactSunlightApi, "SunlightBackground");
 		  thread.start(); 
 	} 
 	 
 	private void govTrackProcessing() { 
 		  Thread thread = new Thread(null, doBackgroundContactGovTrack, "GovTrackBackground"); 
 		  thread.start(); 
 	 }
 	 
 	 private Runnable doBackgroundContactSunlightApi = new Runnable() { 
 		 public void run() { 
 			 contactSunlightApi(zipcode);
 		 } 
 	 };
 	 
 	 private Runnable doBackgroundContactGovTrack = new Runnable() { 
 		 public void run() { 
  			contactGovTrackPersonApi();
 		 } 
 	 };
 	 
 	// Runnable that executes the update GUI method. 
 	 private Runnable doOutputResult = new Runnable() { 
 		 public void run() { 
 			 outputResult(mSuccess);
 		 } 
 	 };
 	 
 	private Runnable mStartZipInputIntent = new Runnable() {
		   public void run() {
			 //TODO actually make intent filters for all of these. 
			startInputZipActivity("Input your zipcode", regularMessage);
		   }
		};
		
	private Runnable mShowProcessDialog = new Runnable() {
		   public void run() {
			   showDialog();
		   }
		};
		
	private Runnable mShowLostConnectionDialog = new Runnable() {
		   public void run() {
			   lostConntectionDialog();
		   }
		};

}