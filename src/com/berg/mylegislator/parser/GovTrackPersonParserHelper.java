package com.berg.mylegislator.parser;
	
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;

import com.berg.mylegislator.MyLegislatorDatabaseAdapter;

public class GovTrackPersonParserHelper extends DefaultHandler {
	
	Context mContext;
	long govTrackId, legislatorID;
	String tempVal;
	MyLegislatorDatabaseAdapter dBAdapter;

	ContentValues termValues = new ContentValues();
	ArrayList<Committee> committeeMembership;
	Committee committee;
	Subcommittee subcommittee;
	
	public GovTrackPersonParserHelper(Context _context, long _legislatorID) {
		this.legislatorID=_legislatorID;
		this.mContext = _context;
	}

    // =========================================================== 
    // Methods 
    // =========================================================== 
    @Override 
    public void startDocument() throws SAXException {
    	dBAdapter = new MyLegislatorDatabaseAdapter(mContext);
	    dBAdapter.open();
        
    } 

    @Override 
    public void endDocument() throws SAXException { 
	    dBAdapter.close();
    } 

    /** Gets be called on opening tags like: 
     * <tag> 
     * Can provide attribute(s), when xml was like: 
     * <tag attribute="attributeValue">*/ 
	@Override 
	public void startElement(String nsURI, String strippedName,
		String tagName, Attributes attributes)  {
		//reset
		tempVal = "";
		
		if(tagName.equalsIgnoreCase("CommitteeMembership")) {
			//create a new instance of employee
			committeeMembership = new ArrayList<Committee>();
			
		}
		
		else if(tagName.equalsIgnoreCase("Committee")) {
			//create a new instance of employee
			committee = new Committee();
			committee.setID(attributes.getValue("id"));
			committee.setName(attributes.getValue("name"));
			committee.setRole(attributes.getValue("Role"));
		}
		else if(tagName.equalsIgnoreCase("subcommittee")) {
			//create a new instance of employee
			subcommittee = new Subcommittee();
			subcommittee.setID(attributes.getValue("id"));
			subcommittee.setName(attributes.getValue("name"));
			subcommittee.setRole(attributes.getValue("Role"));
			committee.addSubcommittee(subcommittee);
		}
		else if(tagName.equalsIgnoreCase("Term")) {
			termValues.clear();
		}
	}

	@Override 
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	@Override 
	public void endElement(String uri, String localName,
		String qName) throws SAXException {

		if(localName.equalsIgnoreCase("Committee")) {
			//add it to the list
			committeeMembership.add(committee);
		}else if (localName.equalsIgnoreCase("CommitteeMembership")) {
			insertIntoDatabase(committeeMembership);
		}else if (localName.equalsIgnoreCase("Title")) {
			termValues.put(MyLegislatorDatabaseAdapter.KEY_TYPE, tempVal);
		}else if (localName.equalsIgnoreCase("Start")) {
			termValues.put(MyLegislatorDatabaseAdapter.KEY_START_OF_TERM, tempVal);
		}else if (localName.equalsIgnoreCase("End")) {
			termValues.put(MyLegislatorDatabaseAdapter.KEY_END_OF_TERM, tempVal);
		}else if (localName.equalsIgnoreCase("State")) {
			termValues.put(MyLegislatorDatabaseAdapter.KEY_TERM_STATE, tempVal);
		}else if (localName.equalsIgnoreCase("District")) {
			termValues.put(MyLegislatorDatabaseAdapter.KEY_TERM_DISTRICT, tempVal);
		}
		
		else if (localName.equalsIgnoreCase("Term")) {
			termValues.put(MyLegislatorDatabaseAdapter.KEY_LEGISLATOR_ID, legislatorID);
			if (!(termValues.containsKey(MyLegislatorDatabaseAdapter.KEY_TERM_DISTRICT)))
					termValues.put(MyLegislatorDatabaseAdapter.KEY_TERM_DISTRICT, "");
			insertTermIntoDatabase(termValues);
		}
		
	}
	
	public boolean insertIntoDatabase(ArrayList<Committee> committees) {
		int indexSize = committees.size();
		int subcommitteesSize;
		int committeeIndex, subcommitteIndex;
		long committeID;
		Committee committee;
		ArrayList<Subcommittee> subcommittees;
		Subcommittee subcommittee;
		ContentValues values;
		
		for (committeeIndex=0; committeeIndex < indexSize; committeeIndex++) {
			committee = committees.get(committeeIndex);
			values = new ContentValues();
			values.put(MyLegislatorDatabaseAdapter.KEY_LEGISLATOR_ID, legislatorID);
			values.put(MyLegislatorDatabaseAdapter.KEY_ROLE, committee.getRole());
			values.put(MyLegislatorDatabaseAdapter.KEY_COMMITTEE_NAME, committee.getName());
			values.put(MyLegislatorDatabaseAdapter.KEY_GOVERNMENT_COMMITTEE_ID, committee.getID());
			values.put(MyLegislatorDatabaseAdapter.KEY_COMMITTEE_ID, "");
			committeID = dBAdapter.saveCommittee(values);
			subcommittees= committee.getSubcommittees();
			subcommitteesSize = subcommittees.size();
			for (subcommitteIndex=0; subcommitteIndex < subcommitteesSize; subcommitteIndex++)  {
				subcommittee= subcommittees.get(subcommitteIndex);
				values.clear();
				values.put(MyLegislatorDatabaseAdapter.KEY_LEGISLATOR_ID, legislatorID);
				values.put(MyLegislatorDatabaseAdapter.KEY_ROLE, subcommittee.getRole());
				values.put(MyLegislatorDatabaseAdapter.KEY_COMMITTEE_NAME, subcommittee.getName());
				values.put(MyLegislatorDatabaseAdapter.KEY_GOVERNMENT_COMMITTEE_ID, subcommittee.getID());
				values.put(MyLegislatorDatabaseAdapter.KEY_COMMITTEE_ID, committeID);
				dBAdapter.saveCommittee(values);
			}
		}
		return true;
	}
	public long insertTermIntoDatabase(ContentValues termValues) {
		return dBAdapter.saveTerm(termValues);
	}
	
	public class Committee {
		ArrayList<Subcommittee> subcommittees;
		String role;
		String id;
		String name;
		
		Committee(){
			id="";
			role="";
			name="";
			subcommittees = new ArrayList<Subcommittee>();
		}
		
		public boolean addSubcommittee(Subcommittee _subcommittee) {
			return subcommittees.add(_subcommittee);
		}
		
		public ArrayList<Subcommittee> getSubcommittees() {
			return subcommittees;
		}
		
		void setID(String _id){
			id=_id;
		}
		
		void setName(String _name) {
			name=_name;
		}
		
		void setRole(String _role) {
			role=_role;
		}
		
		String getRole() {
			return role;
		}
		
		String getName() {
			return name;
		}
	
		String getID() {
			return id;
		}
	
	
	}
	
	public class Subcommittee {
		String role;
		String id;
		String name;
		
		Subcommittee(){
			id="";
			role="";
			name="";
		}
		
		void setID(String _id){
			id=_id;
		}
		
		void setName(String _name) {
			name=_name;
		}
		
		void setRole(String _role) {
			role=_role;
		}
		
		String getRole() {
			return role;
		}
		
		String getName() {
			return name;
		}
	
		String getID() {
			return id;
		}
		
	}
}
