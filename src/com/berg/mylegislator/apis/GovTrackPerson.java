package com.berg.mylegislator.apis;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.berg.mylegislator.MyLegislatorDatabaseAdapter;
import com.berg.mylegislator.parser.GovTrackPersonParserHelper;

public class GovTrackPerson {
	
	protected static final String LEGISLATOR_ID = "com.berg.mylegislator.legislator_id";
	static String TAG = "MyLegislator";
	static final String BASE ="http://www.govtrack.us/congress/person_api.xpd?id=";
	static final int millisecondsTimeOut = 5000;
	 long LegislatorID;
	 long GovTrackID;
	
	static MyLegislatorDatabaseAdapter dBAdapter;
	Context mContext;
	Cursor LegislatorCursor;
	
	public static int digestFromPersonFromGovTrack(Context context, long govTrackId, long legislatorId) throws ParserConfigurationException {	
		String Url = BASE + govTrackId;
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, millisecondsTimeOut);
		httpclient.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, millisecondsTimeOut);
	        // Prepare a request object
	        HttpGet httpget = new HttpGet(Url); 
	        	
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
	                InputStream instream = entity.getContent();
	 
	    			//XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
	    			//Refer to the comments below for the explanation for commenting above line and putting a new one below.

	                SAXParserFactory spf = SAXParserFactory.newInstance(); 
	                SAXParser sp = spf.newSAXParser(); 

	                /* Get the XMLReader of the SAXParser we created. */ 
	                XMLReader xr = sp.getXMLReader();
	                xr = XMLReaderFactory.createXMLReader();
	                xr.setContentHandler(new GovTrackPersonParserHelper(context, legislatorId));
	                xr.parse(new InputSource(instream));
	    			instream.close();

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
	        } catch (SAXException e) {
				// TODO Auto-generated catch block
	            Log.d(TAG,"SAXException" + e);
				e.printStackTrace();
	            return 2;
			}
			return 4; 
	 }
}