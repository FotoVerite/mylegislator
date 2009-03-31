package com.berg.mylegislator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple database access helper class. Defines the basic CRUD operations,
 * and gives the ability to list all rows as well as retrieve or modify a specific row.
 */
public class MyLegislatorDatabaseAdapter {
	private static final String TAG = "MyCongressPerson";

    private final DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    public static final String KEY_ID = "_id";    

    /** App-specific parameters
     * 
     */
    private static final String DATABASE_TABLE_REPRESENTATIVES = "representatives";
    private static final String DATABASE_TABLE_COMMITTEES = "committees";
    private static final String DATABASE_TABLE_TERMS = "terms";

    private static final String DATABASE_NAME = "reds.db";
    private static final int DATABASE_VERSION = 1;
    
    /** DELAYED CALLS TABLE */
    public static final String KEY_TITLE= "title";
    public static final int TITLE_COLUMN = 1;
    public static final String KEY_NAME_SUFFIX= "name_suffix";
    public static final int  NAME_SUFFIX_COLUMN = 2;
    public static final String KEY_FIRST_NAME= "first_name";
    public static final int  FIRST_NAME_COLUMN = 3;
    public static final String KEY_MIDDLE_NAME= "middle_name";
    public static final int  MIDDLE_NAME_COLUMN = 4;
    public static final String KEY_LAST_NAME= "last_name";
    public static final int  LAST_NAME_COLUMN = 5;
    public static final String KEY_GENDER= "gender";
    public static final int  GENDER_COLUMN = 6;
    public static final String KEY_PARTY= "party";
    public static final int  PARTY_COLUMN = 7;
    public static final String KEY_DISTRICT= "district";
    public static final int  DISTRICT_COLUMN = 8;
    public static final String KEY_STATE= "state";
    public static final int  STATE_COLUMN = 9;
    public static final String KEY_EMAIL= "email";
    public static final int  EMAIL_COLUMN = 10;
    public static final String KEY_PHONE= "phone";
    public static final int  PHONE_COLUMN = 11;
    public static final String KEY_FAX= "fax";
    public static final int  FAX_COLUMN= 12;
    public static final String KEY_WEBFORM= "webform";
    public static final int  WEBFORM_COLUMN = 13;
    public static final String KEY_WEBSITE= "website";
    public static final int  WEBSITE_COLUMN= 14;
    public static final String KEY_OFFICE_ADDRESS= "office_address";
    public static final int  OFFICE_ADDRESS_COLUMN= 15;
    public static final String KEY_CRP_ID= "crp_id";
    public static final int  CPR_ID_COULMN=16;
    public static final String KEY_GOVTRACK_ID= "govtrack_id";
    public static final int  GOVTRACK_ID_COLUMN= 17;
    public static final String KEY_FEC_ID= "fec_id";
    public static final int  FEC_ID_COLUMN = 18;
    public static final String KEY_EVENTFUL_ID= "eventful_id";
    public static final int  EVENTFUL_ID_COLUMN = 19;
    public static final String KEY_TWITTER_ID= "twitter_id";
    public static final int  TWITTER_ID_COLUMN = 20;
    public static final String KEY_VOTESMART_ID= "votesmart_id";
    public static final int  VOTESMART_ID_COLUMN = 21;
    public static final String KEY_BIOGUIDE_ID= "bioguide_id";
    public static final int  BIOGUIDE_ID_COLUMN = 22;
    public static final String KEY_YOUTUBE= "youtube_url";
    public static final int  YOUTUBE_COLUMN = 23;
    public static final String KEY_CONGRESSPEDIA= "congresspedia_url";
    public static final int  CONGRESSPEDIA_COLUMN = 24;
    public static final String KEY_RSS= "official_rss";
    public static final int  RSS_COLUMN = 25;
    public static final String KEY_FOLLOWS_ON_TWITTER= "followed_on_twitter";
    public static final int  FOLLOWS_ON_TWITTER_COLUMN = 26;

    /** COMMITTEES TABLE */
    public static final String KEY_LEGISLATOR_ID= "legislator_id";
    public static final int LEGISLATOR_ID_COLUMN = 1;
    public static final String KEY_ROLE= "role";
    public static final int ROLE_COLUMN = 2;
    public static final String KEY_COMMITTEE_NAME= "name";
    public static final int  COMMITTEE_NAME_COLUMN = 3;
    public static final String KEY_GOVERNMENT_COMMITTEE_ID= "government_committee_id";
    public static final int  GOVERNMENT_COMMITTEE_ID_COLUMN = 4;
    public static final String KEY_COMMITTEE_ID= "committee_id";
    public static final int  COMMITTEE_ID_COLUMN = 5;    
    
    /** TERMS TABLE */
    //KEY_LEGISLATOR_ID don't want to initalize another variable
    //LEGISLATOR_ID_COLUMN;
    public static final String KEY_TYPE= "type_of_legislator";
    public static final int KEY_TYPE_COLUMN = 2;
    public static final String KEY_START_OF_TERM= "start_of_term";
    public static final int  START_OF_TERM_COLUMN = 3;
    public static final String KEY_END_OF_TERM= "end_of_term";
    public static final int  END_OF_TERM_COLUMN = 4;
    public static final String KEY_TERM_STATE= "term_state";
    public static final int  TERM_STATE_COLUMN = 5;    
    public static final String KEY_TERM_DISTRICT= "term_district";
    public static final int  TERM_DISTRICT_COLUMN = 6;    
  
    private String[] represenativesFields = new String[] {KEY_ID, KEY_TITLE, KEY_NAME_SUFFIX, KEY_FIRST_NAME,
    		KEY_MIDDLE_NAME, KEY_LAST_NAME, KEY_GENDER, KEY_PARTY, KEY_DISTRICT, KEY_STATE, KEY_EMAIL,  KEY_PHONE, 
    		KEY_FAX, KEY_WEBFORM, KEY_WEBSITE, KEY_OFFICE_ADDRESS, KEY_CRP_ID, KEY_GOVTRACK_ID, KEY_FEC_ID, 
    		KEY_EVENTFUL_ID, KEY_TWITTER_ID, KEY_VOTESMART_ID, KEY_BIOGUIDE_ID, KEY_YOUTUBE, KEY_CONGRESSPEDIA, KEY_RSS,
    		KEY_FOLLOWS_ON_TWITTER}; 
    
    private String[] committeeFields = new String[] {KEY_ID, KEY_LEGISLATOR_ID, KEY_ID, KEY_ROLE, KEY_COMMITTEE_NAME, KEY_COMMITTEE_ID}; 
  
    private String[] termFields = new String[] {KEY_ID, KEY_LEGISLATOR_ID, KEY_TYPE, KEY_START_OF_TERM, KEY_END_OF_TERM, KEY_TERM_STATE, KEY_TERM_DISTRICT}; 

    // public static final String KEY_STARTUP = "on_startup";
    
    private static final String CREATE_TABLE_REPRESENTATIVES =
            "create table " + DATABASE_TABLE_REPRESENTATIVES + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                KEY_TITLE + " string not null, " +
                KEY_NAME_SUFFIX + " string not null, " +
                KEY_FIRST_NAME + " string not null, " +
                KEY_MIDDLE_NAME + " string null, " +
                KEY_LAST_NAME + " string not null, " +
                KEY_GENDER + " string not null, " +
                KEY_PARTY + " string not null, " +
                KEY_DISTRICT + " string not null, " +
                KEY_STATE + " string not null, " +
                KEY_EMAIL + " string not null, " +
                KEY_PHONE + " string not null, " +
                KEY_FAX + " string not null, " +
                KEY_WEBFORM + " string not null, " +
                KEY_WEBSITE + " string not null, " +
                KEY_OFFICE_ADDRESS + " string not null, " +
                KEY_CRP_ID + " string not null, " +
                KEY_GOVTRACK_ID + " string not null, " +
                KEY_FEC_ID + " string not null, " +
                KEY_EVENTFUL_ID + " string not null, " +
                KEY_TWITTER_ID + " string not null, " +
                KEY_VOTESMART_ID + " string not null, " +
                KEY_BIOGUIDE_ID + " string not null, " +
                KEY_YOUTUBE + " string not null, " +
                KEY_CONGRESSPEDIA + " string not null," +
                KEY_RSS + " string not null," +
                KEY_FOLLOWS_ON_TWITTER + " boolean not null);";
   
    
    private static final String CREATE_COMMITTEES_TABLE =
        "create table " + DATABASE_TABLE_COMMITTEES + " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_LEGISLATOR_ID  + " integer not null, " +
            KEY_ROLE + " string null, " +
            KEY_COMMITTEE_NAME + " string not null, " +
            KEY_GOVERNMENT_COMMITTEE_ID + " int not null, " +
            KEY_COMMITTEE_ID + " int not null);";
    
    private static final String CREATE_TERMS_TABLE =
        "create table " + DATABASE_TABLE_TERMS + " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_LEGISLATOR_ID  + " integer not null, " +
            KEY_TYPE + " string not null, " +
            KEY_START_OF_TERM + " string not null, " +
            KEY_END_OF_TERM + " string not null, " +
            KEY_TERM_STATE + " string not null, " +
            KEY_TERM_DISTRICT + " string not null);";
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    
    public MyLegislatorDatabaseAdapter(Context mContext) { 
        this.mDbHelper = new DatabaseHelper(mContext); 
      } 


    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public void open() throws SQLException {
    	try { 
    			mDb = mDbHelper.getWritableDatabase(); 
    		} catch (SQLiteException ex) { 
    			mDb = mDbHelper.getReadableDatabase(); 
    		} 
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new row using the information provided. If the row is
     * successfully created, return the new rowId for that row, otherwise, return
     * a -1 to indicate failure.
     * 
     * @param args the column keys and data of the row to be created
     * @return rowId or -1 if failed
     */

    public long saveRepresenative(ContentValues args) {
        return mDb.insert(DATABASE_TABLE_REPRESENTATIVES, null, args);
    }
    
    public long saveCommittee(ContentValues args) {
        return mDb.insert(DATABASE_TABLE_COMMITTEES, null, args);
    }
    
    public long saveTerm(ContentValues args) {
        return mDb.insert(DATABASE_TABLE_TERMS, null, args);
    }
    
    /**
     * Truncate the representatives table. 
     * 
     */
    public boolean truncateDatabase() {
    	return (mDb.delete(DATABASE_TABLE_REPRESENTATIVES, "1", null) > 0 &&
    			mDb.delete(DATABASE_TABLE_COMMITTEES, "1", null) > 0 &&
    			mDb.delete(DATABASE_TABLE_TERMS, "1", null) > 0);
    	
    }

    /**
     * Delete the row with the given rowId
     * 
     * @param rowId id of row to delete
     * @return true if deleted, false otherwise
     */
  
    /**
     * Return a Cursor over the list of all rows in the representative table
     * 
     * @return Cursor over all rows
     */
    public Cursor fetchAllRepresenatives() {
        return mDb.query(DATABASE_TABLE_REPRESENTATIVES, represenativesFields,
        		null, null, null, null, KEY_FIRST_NAME);
    }
    
    public Cursor fetchAllCommittes(long legislatorId) {
        return mDb.query(DATABASE_TABLE_COMMITTEES, committeeFields,
        		KEY_LEGISLATOR_ID + "=" + legislatorId + " AND " + KEY_COMMITTEE_ID + "=''", null, null, null, null);
    }
    
    public Cursor fetchAllSubCommittees(long committeeID) {
        return mDb.query(DATABASE_TABLE_COMMITTEES, committeeFields,
        		KEY_COMMITTEE_ID + "=" + committeeID, null, null, null, null);
        
    }
    
    public Cursor fetchAllTerms(long legislatorId) {
        return mDb.query(DATABASE_TABLE_TERMS, termFields,
        		KEY_LEGISLATOR_ID + "=" + legislatorId, null, null, null, KEY_END_OF_TERM + " DESC");
    }


    /**
     * Return a Cursor positioned at the row that matches the given rowId
     * 
     * @param rowId ID of row to retrieve
     * @return Cursor positioned to matching row, if found
     * @throws SQLException if row could not be found/retrieved
     */
    
    public Cursor fetchRepresentative(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE_REPRESENTATIVES,  represenativesFields, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        return mCursor;
    }
    
    public String fetchEndOfTerm(long legislatorId) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE_TERMS,  new String[] {"end_of_term"}, KEY_LEGISLATOR_ID + "=" + legislatorId, null,
                        null, null, "end_of_term DESC", "1");
        String termEndDate = "";
        if (!(mCursor.getCount() == 0)) {
	        mCursor.moveToFirst();
	        termEndDate= mCursor.getString(0);  
        }
        mCursor.deactivate();
        mCursor.close();
        return termEndDate;
    }

    /**
     * Update the row using the details provided. The row to be updated is
     * specified using the rowId, and it is altered to use the values passed in 
     * 
     * @param rowId id of row to update
     * @param args set of column ids and values
     * @return true if the row was successfully updated, false otherwise
     */
    public boolean updateRepresenative(long rowId, ContentValues args) {
        return mDb.update(DATABASE_TABLE_REPRESENTATIVES, args, KEY_ID + "=" + rowId, null) > 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_REPRESENTATIVES);
            db.execSQL(CREATE_COMMITTEES_TABLE);
            db.execSQL(CREATE_TERMS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + MyLegislatorDatabaseAdapter.DATABASE_TABLE_REPRESENTATIVES);
            onCreate(db);
        }
    }

    
}
