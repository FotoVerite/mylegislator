package com.berg.mylegislator.apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.berg.mylegislator.MyLegislatorDatabaseAdapter;
import com.berg.mylegislator.Settings;

public class Sunlight {
	
	static String TAG = "MyCongressPerson";
	static String URL="http://services.sunlightlabs.com/api/";
	static String FORMAT=".json?";
	static String SUNLIGHT_API_CODE = "apikey=aefcb2c5e879bbdd976039d1a8b0dc8b";
	static String METHOD="legislators.allForZip";
	static String ZIP;
	static final int millisecondsTimeOut = 5000;
	
	static MyLegislatorDatabaseAdapter dBAdapter;

	
	public static int findCongressLegislators(String zipcode, Context mContext) {
		 String URI= URL + METHOD + FORMAT + SUNLIGHT_API_CODE  + "&zip=" + zipcode;
         Log.d(TAG,URI);
         dBAdapter = new MyLegislatorDatabaseAdapter(mContext);
		 dBAdapter.open();

		 HttpClient httpclient = new DefaultHttpClient();
		 httpclient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, millisecondsTimeOut);
		 httpclient.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, millisecondsTimeOut);
	        // Prepare a request object
	        HttpGet httpget = new HttpGet(URI); 
	        	
	        // Execute the request
	        HttpResponse response;
	        try {
	            response = httpclient.execute(httpget);
	            // Examine the response status
	 
	            // Get hold of the response entity
	            HttpEntity entity = response.getEntity();
	            // If the response does not enclose an entity, there is no need
	            // to worry about connection release

	            if (entity != null) {
	 
	                // A Simple JSON Response Read
	                InputStream instream = entity.getContent();
	                String result= convertStreamToString(instream);
	 
	                // A Simple JSONObject Creation
	                JSONObject json=new JSONObject(result);
	                json = json.getJSONObject("response");  
	                JSONArray legislators = json.getJSONArray("legislators");
	                if (legislators.length() == 0){
	                	 instream.close();
	                     Log.d(TAG,"Unkown zipcode");
	                	 return 3;
	                }
	                Log.d(TAG,"didn't break");

	                for(int index=0;index<legislators.length();index++) {

	                	JSONObject legislator=new JSONObject(legislators.get(index).toString());
	                	legislator = legislator.getJSONObject("legislator");  

		                ContentValues newLegislator = new ContentValues();
		               
	                	newLegislator.put(MyLegislatorDatabaseAdapter.KEY_TITLE, legislator.getString("title"));
		            	newLegislator.put(MyLegislatorDatabaseAdapter.KEY_NAME_SUFFIX, legislator.getString("name_suffix"));
		            	newLegislator.put("first_name", legislator.getString("firstname"));
		            	newLegislator.put("middle_name", legislator.getString("middlename"));
		            	newLegislator.put("last_name", legislator.getString("lastname"));
		            	newLegislator.put("gender", legislator.getString("gender"));
		            	newLegislator.put("party", legislator.getString("party"));
		            	newLegislator.put("state", legislator.getString("state"));
		            	newLegislator.put("district", legislator.getString("district"));
		            	newLegislator.put("email", legislator.getString("email"));
		            	newLegislator.put("phone", legislator.getString("phone"));
		            	newLegislator.put("fax", legislator.getString("fax"));
		            	newLegislator.put("webform", legislator.getString("webform"));
		            	newLegislator.put("website", legislator.getString("website"));
		            	newLegislator.put("office_address", legislator.getString("congress_office"));
		            	newLegislator.put("crp_id", legislator.getString("crp_id"));
		            	newLegislator.put("govtrack_id", legislator.getString("govtrack_id"));
		            	newLegislator.put("fec_id", legislator.getString("fec_id"));
		            	newLegislator.put("eventful_id", legislator.getString("eventful_id"));
		            	newLegislator.put("twitter_id", legislator.getString("twitter_id"));
		            	newLegislator.put("votesmart_id", legislator.getString("votesmart_id"));
		            	newLegislator.put("bioguide_id", legislator.getString("bioguide_id"));
		            	newLegislator.put("youtube_url",  legislator.getString("youtube_url"));
		            	newLegislator.put("congresspedia_url", legislator.getString("congresspedia_url"));
		            	newLegislator.put("official_rss", legislator.getString("official_rss"));
		            	newLegislator.put(MyLegislatorDatabaseAdapter.KEY_FOLLOWS_ON_TWITTER, false);
		                long r =dBAdapter.saveRepresenative(newLegislator);
			             Log.d(TAG, "" + r);
		                // Closing the input stream will trigger connection release
		            }
	                instream.close();
	                Settings.setAppHasBeenOpened(mContext);
	       			dBAdapter.close();
	            }
		 
	        } 
	        
	        catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            Log.d(TAG,"ClientProtocol" + e);
	            return 2;
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            Log.d(TAG,"IOException" + e);
	            e.printStackTrace();
	            return 2;
	        } catch (JSONException e) {
	            // TODO Auto-generated catch block
	            Log.d(TAG,"JSONException" + e);
	            e.printStackTrace();
	            return 2;
	        }
			return 1;
	 }
	 
	 private static String convertStreamToString(InputStream is) {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }
}