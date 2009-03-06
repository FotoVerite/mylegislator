package com.berg.mylegislator;
import javax.xml.parsers.ParserConfigurationException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.berg.mylegislator.apis.GovTrackPerson;
import com.berg.mylegislator.apis.Sunlight;

public class Welcome extends Activity {
	private EditText enteredZipCode;
	private int mSuccessful;
	private TableRow indicatorRow;
	private ProgressBar progressBar;
	private TextView indicatorText;
	private Button continueButton;
	private boolean hasPressButton = false;
	String zipcode;
	Resources myResources;
	Context mContext;
	MyLegislatorDatabaseAdapter dBAdapter;
	
	private String title;
	private String message;
	
	// Initialize a handler on the main thread. 
	private Handler handler = new Handler(); 
	private Boolean inProcess=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//For some reason it needs these drivers to be set Manually to parse XML correctly. 
		System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		this.mContext = getBaseContext();
		super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        dBAdapter = new MyLegislatorDatabaseAdapter(mContext);
    	dBAdapter.open();
        findViews();
        title = getIntent().getStringExtra(MyLegislator.INPUT_SCREEN_TITLE);
        message = getIntent().getStringExtra(MyLegislator.INPUT_SCREEN_MESSAGE);
        TextView messageView = (TextView)findViewById(R.id.input_screen_message);
        messageView.setText(message);
        continueButton.setOnClickListener(new View.OnClickListener() { 
          public void onClick(View view) { 
        	if (!(hasPressButton)) {
	        	zipcode = enteredZipCode.getText().toString();
	        	indicatorRow.setVisibility(TableRow.INVISIBLE);
	        	if (zipcode.length() < 5)  
	        		invalidZip();
	        	else {
		        	indicatorRow.setVisibility(TableRow.VISIBLE);
		        	progressBar.setVisibility(TableRow.VISIBLE);
					indicatorText.setTextColor(Color.WHITE);
					indicatorText.setPadding(5, 0, 0, 0);
					indicatorText.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
	        		indicatorText.setText("Contacting Sunlight Api");
	        		hasPressButton= true;
	        		inProcess=true;
	        		sunlightProcessing();
	        	}
        	}
          } 
        });
    }
	
	public void findViews() {
		indicatorRow = (TableRow)findViewById(R.id.indicator_row);
        progressBar = (ProgressBar)findViewById(R.id.indicator_progress_bar);
        indicatorText = (TextView)findViewById(R.id.indicator_text);
        continueButton = (Button) findViewById(R.id.enter_zip_button); 
        enteredZipCode = (EditText) findViewById(R.id.enter_zip_edit_text);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub

	switch(KeyEvent.KEYCODE_BACK) {
		case KeyEvent.KEYCODE_BACK: 
			if (inProcess)
				return true;
			else
				finish();
	}
	return super.onKeyDown(keyCode, event);
	}
	

	@Override 
	public void onDestroy() {
		super.onDestroy();
		dBAdapter.close();
	}
	
// =========================================================== 
// GUI Methods
// =========================================================== 
	 
	public void invalidZip() {
		int duration = Toast.LENGTH_LONG;
		String output = "Invalid Zip, ZipCodes are five characters long";
		Toast toast = Toast.makeText(mContext, output, duration); 
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}
	 
	 private void outputResult(int outputCode) {
		 switch (outputCode) {
		 	case 1: {
				indicatorText.setText("Contacting GovTrack");
		 		govTrackProcessing(); 
		 		break;
		 	}
		 	case 2: {
				 progressBar.setVisibility(TextView.GONE);
				 indicatorText.setTextColor(Color.RED);
				 indicatorText.setPadding(15, 0, 0, 0);
				 indicatorText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				 indicatorText.setText("ERROR");
				 hasPressButton= false;
				 dBAdapter.truncateDatabase();
				 break;
			 }
		 	case 3: {
				 progressBar.setVisibility(TextView.GONE);
				 indicatorText.setTextColor(Color.RED);
				 indicatorText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				 indicatorText.setPadding(15, 0, 0, 0);
				 indicatorText.setText("ZipCode is not valid");
				 hasPressButton= false;
				 break;
			 }
		 	case 4: {
		 		Settings.setUserZip(mContext, zipcode);
		 		finish();
		 		break;
		 	}
		 }
		 inProcess=false;
	} 
	 
// =========================================================== 
// Api Call Methods
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
					mSuccessful = GovTrackPerson.digestFromPersonFromGovTrack(context, govTrackId, legislatorId);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mSuccessful == 2)
					break;
	    	}
	    } while(LegislatorCursor.moveToNext());
		LegislatorCursor.deactivate();
		LegislatorCursor.close();
	 	handler.post(doOutputResult);
	}
	
	public void contactSunlightApi(String zipcode) {
	 	mSuccessful= Sunlight.findCongressLegislators(zipcode, getBaseContext());
	 	handler.post(doOutputResult);
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
	 };
	 
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
		   outputResult(mSuccessful); 
		 } 
	 };

}
