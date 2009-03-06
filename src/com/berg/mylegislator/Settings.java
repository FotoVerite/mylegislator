package com.berg.mylegislator;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity { 
	//Option Values
	private static final String OPT_IS_FIRST_TIME_OPENED= "firstTimeOpened"; 
	private static final boolean OPT_IS_FIRST_TIME_OPENED_DEF = true; 
	private static final String OPT_USER_ZIP= "user_zip"; 
	private static final String OPT_USER_ZIP_DEF = "00000"; 

	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.settings); 
	}
	
	/** Get the boolean if the app has been opened before */ 
	public static boolean getHasBeenOpened(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_IS_FIRST_TIME_OPENED, OPT_IS_FIRST_TIME_OPENED_DEF); 
	}
	
	public static String getUserZip(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getString(OPT_USER_ZIP, OPT_USER_ZIP_DEF); 
	}
	
	public static void setAppHasBeenOpened(Context context) {  // Retrieve an editor to modify the shared preferences. 
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putBoolean(OPT_IS_FIRST_TIME_OPENED, false).commit();
	}
	
	public static void setUserZip(Context context, String value) {  // Retrieve an editor to modify the shared preferences. 
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putString(OPT_USER_ZIP, value).commit();
	}
	
	
	
	
}