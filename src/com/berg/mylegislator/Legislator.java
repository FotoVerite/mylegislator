package com.berg.mylegislator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berg.mylegislator.view.GradientedTableRow;
import com.berg.mylegislator.view.InnerPanel;
import com.berg.mylegislator.view.Panel;


public class Legislator extends Activity {
	
	 protected static final String LEGISLATOR_ID = "com.berg.mylegislator.legislator_id";
	 protected static final String PANEL_BORDER= "com.berg.mylegislator.panel_border";
	 protected static final String GRADIENT_COLOR = "com.berg.mylegislator.gradient_color";
	 protected static final String DIVIDER_RESOURCE = "com.berg.mylegislator.divider_resource";
	 protected static final String FULLNAME = "com.berg.mylegislator.fullname";

	 static final private int CALL = R.id.call_legislator;
	 static final private int EMAIL  = R.id.email_legislator;
	 static final private int WEBSITE  = R.id.visit_website;
	 static final private int TWITTER  = R.id.visit_twitter;

	 static final private int WEBFORM_MAIN  = R.id.visit_webform_main;
	 static final private int WEBFORM_ADDITIONAL  = R.id.visit_webform_additional;

	 static final private int ADD_TO_CONTACTS  = R.id.add_legislator_to_contacts;
	 static final private int YOUTUBE  = R.id.visit_youtube_channel;
	 public static final int COMMITTEES = R.id.mini_bio;
	 public static final int TERMS = R.id.terms;


	 public static final int REMOVE_FROMT_CONTACTS = Menu.FIRST;
	 public static final int NO = Menu.FIRST + 4;

	 static private int primaryPanelColor;
	 static private int graidentColor;
	 static private int backgroundColors;
	 static private int dividerResource;
	 private long legislatorId;
	 private Context mContext;
	 
	 private MyLegislatorDatabaseAdapter dBAdapter;
	 private Cursor legislatorCursor;
	 private Cursor contactCursor;
	 private String partyDesignation, endOfTerm, email, phone, fax, address, website, webform,  twitter, youtube, mContactID;
	 private Boolean hasTerm=false,hasPhone=false, hasEmail=false, hasAddress=false, hasTwitter=false, hasYoutube=false, hasFax=false, 
	 hasBefriended=false, hasWebSite=false,hasWebForm=false, hasWebFormMainOption=false, hasWebFormAdditionOption=false, hasAddedToContacts= false;
	 private StringBuffer fullName= new StringBuffer("");
	 private StringBuffer memberOf= new StringBuffer("");
	 private TextView fullNameView, districtView, endOfTermView, phoneView, emailView, faxView, 
	 addressView, webformView, websiteView, twitterView, youtubeView, rssView;
	 private Panel thisPanel;
	 private InnerPanel thisInnerPanel;
	 private GradientedTableRow termEndsRow, emailRow, phoneRow, faxRow, addressRow, websiteRow, webformRow, twitterRow, youtubeRow;
	 private GradientedTableRow[] panelRows;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getBaseContext();
        setContentView(R.layout.legislator);
        setupViews();
        legislatorId = getIntent().getLongExtra(LEGISLATOR_ID, 1);
        dBAdapter = new MyLegislatorDatabaseAdapter(this);
	    dBAdapter.open();
	    legislatorCursor = dBAdapter.fetchRepresentative(legislatorId);
	    startManagingCursor(legislatorCursor);
	    populatePanelVariables();
	    findTextViews();
	    findPanelRows();
	    fullNameView.setText(fullName.toString());
	    districtView.setText(memberOf.toString());
	    phoneView.setText(phone);
	    getPrimaryPanelColor();
	    setPanelColor();
	    setRowGradient();
	    setPanelRowsVisibility();

	}
	
	public void setupViews() {
		thisPanel = (Panel)findViewById(R.id.legislator_panel);
        thisInnerPanel = (InnerPanel)findViewById(R.id.legislator_inner_panel);
	}
	
	public void populatePanelVariables() {
		endOfTerm = "" + dBAdapter.fetchEndOfTerm(legislatorId);
		legislatorCursor.moveToFirst();
		setFullName();
		setMemberOf();
		setContactInfo();
		phone = legislatorCursor.getString(MyLegislatorDatabaseAdapter.PHONE_COLUMN);
		email = legislatorCursor.getString(MyLegislatorDatabaseAdapter.EMAIL_COLUMN);
		fax = legislatorCursor.getString(MyLegislatorDatabaseAdapter.FAX_COLUMN);
		twitter = legislatorCursor.getString(MyLegislatorDatabaseAdapter.TWITTER_ID_COLUMN);
		youtube = legislatorCursor.getString(MyLegislatorDatabaseAdapter.YOUTUBE_COLUMN);
		address = legislatorCursor.getString(MyLegislatorDatabaseAdapter.OFFICE_ADDRESS_COLUMN);
		website = legislatorCursor.getString(MyLegislatorDatabaseAdapter.WEBSITE_COLUMN);
		webform = legislatorCursor.getString(MyLegislatorDatabaseAdapter.WEBFORM_COLUMN);

		setBooleans();
		isAddedAsContact();


	}
	
	public void findTextViews() {
		fullNameView = (TextView)findViewById(R.id.fullname);
	    districtView = (TextView)findViewById(R.id.district);
	    endOfTermView = (TextView)findViewById(R.id.term_ends);
	    emailView = (TextView)findViewById(R.id.email);
	    phoneView = (TextView)findViewById(R.id.phone);
	    faxView = (TextView)findViewById(R.id.fax);
	    addressView = (TextView)findViewById(R.id.address);
	    websiteView = (TextView)findViewById(R.id.website_url);
	    webformView = (TextView)findViewById(R.id.webform_url);
	    twitterView = (TextView)findViewById(R.id.twitter);
	    youtubeView = (TextView)findViewById(R.id.youtube);
	}
	
	public void findPanelRows() {
		termEndsRow = (GradientedTableRow)findViewById(R.id.term_ends_row);
		emailRow = (GradientedTableRow)findViewById(R.id.email_row);
	    phoneRow = (GradientedTableRow)findViewById(R.id.phone_row);
	    faxRow = (GradientedTableRow)findViewById(R.id.fax_row);
	    addressRow = (GradientedTableRow)findViewById(R.id.address_row);
	    websiteRow = (GradientedTableRow)findViewById(R.id.website_row);
	    webformRow = (GradientedTableRow)findViewById(R.id.webform_row);
	    twitterRow = (GradientedTableRow)findViewById(R.id.twitter_row);
	    youtubeRow = (GradientedTableRow)findViewById(R.id.youtube_row);
	    panelRows = new GradientedTableRow[]{termEndsRow, emailRow, phoneRow, faxRow, addressRow, websiteRow, webformRow,twitterRow, youtubeRow};
	}
	
	public void setPanelRowsVisibility() {
		displayRow(endOfTerm, endOfTermView, hasTerm);
		displayRow(phone, phoneView, hasPhone);
		displayRow(email, emailView, hasEmail);
		displayRow(fax, faxView, hasFax);
		displayRow(address, addressView, hasAddress);
		displayRow(website, websiteView, hasWebSite);
		displayRow(webform, webformView, hasWebForm);
		displayRow(twitter, twitterView, hasTwitter);
		displayRow(youtube, youtubeView, hasYoutube);
	}
	
	public void displayRow(String _text, TextView _view, Boolean _boolean) {
		if (_boolean) {
			_view.setText(_text);
		}
		else {
			GradientedTableRow _row = (GradientedTableRow) _view.getParent();
			_row.setVisibility(View.GONE);
		}
	}
		
	
	public void setFullName() {
		fullName.append(legislatorCursor.getString(MyLegislatorDatabaseAdapter.FIRST_NAME_COLUMN));
		fullName.append(" ");
		fullName.append(legislatorCursor.getString(MyLegislatorDatabaseAdapter.MIDDLE_NAME_COLUMN));
		fullName.append(" ");
		fullName.append(legislatorCursor.getString(MyLegislatorDatabaseAdapter.LAST_NAME_COLUMN));
		fullName.append(" ");
		fullName.append(legislatorCursor.getString(MyLegislatorDatabaseAdapter.NAME_SUFFIX_COLUMN));
	}
	
	public void setMemberOf() {
		partyDesignation = legislatorCursor.getString(MyLegislatorDatabaseAdapter.PARTY_COLUMN);
		if (partyDesignation.equals("R"))
			memberOf.append("Republican ");
		if (partyDesignation.equals("D"))
			memberOf.append("Democratic ");
		if (partyDesignation.equals("I"))
			memberOf.append("Independent ");
		String district = legislatorCursor.getString(MyLegislatorDatabaseAdapter.DISTRICT_COLUMN);
		if (district.length() < 5) {
			StringBuffer bufferedString = new StringBuffer("district "); 
			bufferedString.append(district);
			bufferedString.append(" representative");
			memberOf.append(bufferedString.toString());
		}
		else {
			memberOf.append(district);
			memberOf.append(" senator");
		}
	}
	
	public void setContactInfo() {
	}
	
	public void getPrimaryPanelColor() {
		Resources myResources = getResources(); {
		if (partyDesignation.equals("R")) 
			primaryPanelColor = myResources.getInteger(R.color.republican_panel);
			graidentColor = myResources.getInteger(R.color.republican_gradient);
			backgroundColors =  R.drawable.republican_list_selector_background;
			dividerResource = R.drawable.republican_divider;
		}
		if (partyDesignation.equals("D")) {
			primaryPanelColor = myResources.getInteger(R.color.democratic_panel);
			graidentColor = myResources.getInteger(R.color.democratic_gradient);
			backgroundColors = R.drawable.democratic_list_selector_background;
			dividerResource = R.drawable.democrat_divider;

		}
		if (partyDesignation.equals("I")) {
			primaryPanelColor = myResources.getInteger(R.color.independent_panel);
			graidentColor = myResources.getInteger(R.color.independent_gradient);
			backgroundColors =  R.drawable.republican_list_selector_background;
			dividerResource = android.R.drawable.divider_horizontal_dark;
		}
	}
	
	public void setBooleans() {
		hasTerm = endOfTerm.equals("") ? false : true;
		hasPhone = phone.equals("") ? false : true;
		hasEmail = email.equals("") ? false : true;
		hasAddress = address.equals("") ? false : true;
		hasFax = fax.equals("") ? false : true;
		hasWebSite = website.equals("") ? false : true;
		hasYoutube = youtube.equals("") ? false : true;
		hasTwitter = twitter.equals("") ? false : true;
		// Sets the webform option, can be a main menu setting or in the submenu depending on email. 
		if (!(webform.equals(""))) {
			hasWebForm=true;
			if (hasEmail==true)
				hasWebFormAdditionOption = true;
			else
				hasWebFormMainOption = true;
		}
		// Sets if user has already followed Legislator on Twitter. TODO allow way for user to say that he is following Legislator. 
		hasBefriended = getBoolean(legislatorCursor, MyLegislatorDatabaseAdapter.FOLLOWS_ON_TWITTER_COLUMN);
	}
	
	private boolean getBoolean(Cursor _cursor, int ColumnId) {
		return (_cursor.getInt(ColumnId) == 0) ?  false : true;
	}
	
	public void isAddedAsContact() {
		contactCursor = getContentResolver().query(People.CONTENT_URI, null, "name='" + fullName +"'", null, null);
		if (contactCursor.getCount() == 1) {
			hasAddedToContacts = true;
			contactCursor.moveToFirst();
			int row = contactCursor.getColumnIndex(People._ID);
			mContactID = contactCursor.getString(row);
		}
		contactCursor.deactivate();
		contactCursor.close();
	}
// =========================================================== 
// Menu Ovveride Methods 
// =========================================================== 
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) { 
	  super.onCreateOptionsMenu(menu); 
	  // Create and add new menu items. 
	  MenuInflater inflater = getMenuInflater(); 
	  inflater.inflate(R.menu.legislator, menu);
	  SubMenu subScience = menu.addSubMenu(
			  R.string.additional_options);
	  subScience.setIcon(android.R.drawable.ic_menu_more);
	  inflater.inflate(R.menu.additional_options, subScience);
	  return true; 
	}
	
	@Override 
	public boolean onPrepareOptionsMenu(Menu menu) { 
	  String contactTitle = getString(hasAddedToContacts ? 
	                                 R.string.remove_legislator_to_contacts_label : R.string.add_legislator_to_contacts_label);
	  int ContactTitle = hasAddedToContacts ?
			  					android.R.drawable.ic_menu_delete : android.R.drawable.ic_menu_add;	
	  
	  MenuItem addToContactsOption = menu.findItem(ADD_TO_CONTACTS);
	  MenuItem twitterOption = menu.findItem(TWITTER);
	  MenuItem youtubeOption = menu.findItem(YOUTUBE);
	  MenuItem committeesOption = menu.findItem(COMMITTEES);
	  MenuItem TermOption= menu.findItem(TERMS);
	  MenuItem webformMainOption = menu.findItem(WEBFORM_MAIN);
	  MenuItem webformAdditionOption = menu.findItem(WEBFORM_ADDITIONAL);
	  MenuItem phoneOption = menu.findItem(CALL);
	  MenuItem emailOption = menu.findItem(EMAIL);
	  addToContactsOption.setTitle(contactTitle); 
	  addToContactsOption.setIcon(ContactTitle);
	  showOrHide(webformMainOption, hasWebFormMainOption);
	  showOrHide(webformAdditionOption, hasWebFormAdditionOption);
	  showOrHide(phoneOption, hasPhone);
	  showOrHide(addToContactsOption, hasPhone || hasEmail);
	  showOrHide(emailOption, hasEmail);
	  showOrHide(twitterOption, hasTwitter);
	  showOrHide(youtubeOption, hasYoutube);
	  showOrHide(committeesOption, hasTerm);
	  showOrHide(TermOption, hasTerm);
	  return true; 
	}	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item); 
		switch(item.getItemId()) { 
    	case (CALL): {
    		call();
    		return true; 
    		}
    	case (EMAIL):
	    	Intent msg=new Intent(Intent.ACTION_SEND);  
	        String[] recipients={email};  
	  
	        msg.putExtra(Intent.EXTRA_EMAIL, recipients);  
	        msg.setType("image/*");  
	        startActivity(Intent.createChooser(msg, "Choose you email application"));  
	    	return true; 
    	case (ADD_TO_CONTACTS): {
    		if (hasAddedToContacts) {
    			removeFromContacts();
    			sendToast(fullName + " has been removed from your contacts");
    		}
    		else
    			addToContacts();
				sendToast(fullName + " has been added to your contacts");
    		}
    		return true; 
    	case (YOUTUBE): 
    		startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse(youtube)));
    	    return true;
    	case (WEBFORM_MAIN): 
    		startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse(webform)));
    	    return true;
    	case (WEBFORM_ADDITIONAL): 
    		startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse(webform)));
    	    return true;
    	case (WEBSITE): 
    		startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse(website)));
    	    return true;
    	case (TWITTER): 
    		startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/"+ twitter)));
    	    return true;
    	case (COMMITTEES):
    		Intent committeeIntent=new Intent(this, Committees.class);
    		Bundle committeesExtras = new Bundle();
    		committeesExtras.putLong(Legislator.LEGISLATOR_ID, legislatorId); 
    		committeesExtras.putInt(Legislator.PANEL_BORDER, primaryPanelColor); 
    		committeesExtras.putInt(Legislator.GRADIENT_COLOR, graidentColor); 
    		committeesExtras.putInt(Legislator.DIVIDER_RESOURCE, dividerResource); 
    		committeesExtras.putString(Legislator.FULLNAME, fullName.toString()); 
			committeeIntent.putExtras(committeesExtras);
    		startActivity(committeeIntent);
    	    return true;
	    case (TERMS):
			Intent termIntent=new Intent(this, Term.class);
			Bundle termExtras = new Bundle();
			termExtras.putLong(Legislator.LEGISLATOR_ID, legislatorId);
			termExtras.putInt(Legislator.PANEL_BORDER, primaryPanelColor); 
			termExtras.putInt(Legislator.GRADIENT_COLOR, graidentColor); 
			termExtras.putString(Legislator.FULLNAME, fullName.toString());
			termExtras.putInt(Legislator.DIVIDER_RESOURCE, dividerResource); 
			termIntent.putExtras(termExtras);
			startActivity(termIntent);
		    return true;
		}
    	return false;
    }
	
// =========================================================== 
// Menu Action Methods 
// =========================================================== 
	
	public void removeFromContacts() {
		Uri uri = Uri.parse(People.CONTENT_URI + "/" + mContactID);
		getContentResolver().delete(uri, null, null);
		hasAddedToContacts = false;
		
	}

	public void addToContacts() {
		ContentValues legislatorContact = new ContentValues(); 
		// Assign values for each row. 
		legislatorContact.put(People.NAME, fullName.toString());
		Uri myRowUri = getContentResolver().insert(People.CONTENT_URI, legislatorContact);
		mContactID = myRowUri.getLastPathSegment();
		String contactID= mContactID;
		Uri ContactMethodsUri = Uri.withAppendedPath(myRowUri, Contacts.ContactMethods.CONTENT_URI.getPath().substring(1));
		
		// assign the new phone number to the person
		legislatorContact.clear();
		legislatorContact.put(Contacts.Phones.PERSON_ID, contactID);
		legislatorContact.put(Contacts.Phones.NUMBER, phone);
		legislatorContact.put(Contacts.Phones.ISPRIMARY, 1);
		legislatorContact.put(Contacts.Phones.TYPE, Phones.TYPE_WORK);
		// insert the new phone number in the database
		getContentResolver().insert(Contacts.Phones.CONTENT_URI, legislatorContact);
		if (hasFax) {
			legislatorContact.clear();
			legislatorContact.put(Contacts.Phones.PERSON_ID, contactID);
			legislatorContact.put(Contacts.Phones.NUMBER, fax);
			legislatorContact.put(Contacts.Phones.TYPE, Phones.TYPE_FAX_WORK);
			// insert the new fax number in the database
			getContentResolver().insert(Contacts.Phones.CONTENT_URI, legislatorContact);
		}
		if (hasEmail) {
			// assign an email address for this person
			legislatorContact.clear();
			legislatorContact.put(Contacts.ContactMethods.PERSON_ID, contactID);
			legislatorContact.put(Contacts.ContactMethods.KIND, 1);
			legislatorContact.put(Contacts.ContactMethods.TYPE,Contacts.ContactMethods.TYPE_WORK);
			legislatorContact.put(Contacts.ContactMethods.DATA, email);
			// insert the new email address in the database
			getContentResolver().insert(ContactMethodsUri, legislatorContact);
		}
		legislatorContact.clear();
		legislatorContact.put(Contacts.GroupMembership.PERSON_ID, contactID);
		legislatorContact.put(Contacts.GroupMembership.GROUP_ID, 1);
		getContentResolver().insert(Contacts.GroupMembership.CONTENT_URI, legislatorContact);
		hasAddedToContacts = true;
	}
	
	public void showOrHide(MenuItem _menuItem, boolean _trueOrFalse) {
		_menuItem.setVisible(_trueOrFalse);
		_menuItem.setEnabled(_trueOrFalse);
	}

	public void call() {
		String cleanedPhoneNumber = phone.replaceAll("\\W", "");
		Intent i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + cleanedPhoneNumber));
		startActivity(i);
	}
	
// =========================================================== 
// Context Menu Methods 
// ===========================================================
	
	@Override 
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) { 
	  super.onCreateContextMenu(menu, v, menuInfo); 
	  if (v == phoneRow) {
		menu.setHeaderTitle("Call?");
	  	menu.add(0, CALL, Menu.NONE, "Yes"); 
	  	menu.add(0, NO, Menu.NONE, "No"); 
	  }
	  else if (v == emailRow) {
			menu.setHeaderTitle("Email?");
		  	menu.add(0, EMAIL, Menu.NONE, "Yes"); 
		  	menu.add(0, NO, Menu.NONE, "No"); 
		  }
	  else if (v == twitterRow) {
		  	if (!(hasBefriended)) {
				menu.setHeaderTitle("Visit Twitter?");
			  	menu.add(0, TWITTER, Menu.NONE, "Yes"); 
			  	menu.add(0, NO, Menu.NONE, "No"); 
		  	}
	  }
	} 
	
	@Override 
	public boolean onContextItemSelected(MenuItem item) {  
	  super.onContextItemSelected(item); 
	  switch (item.getItemId()) { 
	    case (CALL): { 
	    	call();
	    	return true; 
	    }
	    case (EMAIL): {
	    	Intent msg=new Intent(Intent.ACTION_SEND);  
	        String[] recipients={email};  
	  
	        msg.putExtra(Intent.EXTRA_EMAIL, recipients);  
	        msg.putExtra(Intent.EXTRA_TEXT, "This is the email body");  
	        msg.putExtra(Intent.EXTRA_SUBJECT, "This is the email subject");  
	        msg.setType("image/*");  
	        startActivity(Intent.createChooser(msg, "Choose application to send email"));  
	    	return true; 
	    }
	    case (TWITTER): {
    		startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/"+ twitter)));
	    	return true; 
	    } 
	  } 
	  return false; 
	}
	
// =========================================================== 
// Panel Color Methods 
// ===========================================================
	
	public void setPanelColor() {
		thisPanel.setBorderPaint(primaryPanelColor);
	}
	
	public void setRowGradient() {
		thisInnerPanel.setGradientColor(graidentColor);
		try {
			  for(int i=0; ; i++)
			  {
				  panelRows[i].setGradientColor(graidentColor);
				  panelRows[i].setBackgroundResource(backgroundColors);
				  registerForContextMenu(panelRows[i]);

			  }
			} catch(ArrayIndexOutOfBoundsException aioobe) {
			// ignore; this just means that i has reached the end of the array
			}
	}
	
	public void sendToast(String message) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(mContext, message, duration);
		toast.setGravity(Gravity.BOTTOM, 0, 5);
		toast.show();
	}
	
	@Override
    public void onDestroy() {
    	dBAdapter.close();
		super.onDestroy(); 
    }
}