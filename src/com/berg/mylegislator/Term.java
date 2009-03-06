package com.berg.mylegislator;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.berg.mylegislator.adapters.TermAdapter;
import com.berg.mylegislator.view.InnerPanel;
import com.berg.mylegislator.view.Panel;

public class Term extends Activity {
	
	protected static final String LEGISLATOR_ID = "com.berg.mylegislator.legislator_id";
	protected static final String PANEL_BORDER= "com.berg.mylegislator.panel_border";
	protected static final String GRADIENT_COLOR = "com.berg.mylegislator.gradient_color";
	protected static final String FULLNAME = "com.berg.mylegislator.fullname";
	protected static final String DIVIDER_RESOURCE = "com.berg.mylegislator.divider_resource";

	private static long legislatorId;
	private static int graidentColor;
	private static int primaryPanelColor;
	private static int dividerResouce;

	private static String fullName;
	
	Context mContext;
	MyLegislatorDatabaseAdapter dBAdapter;
	Cursor myCursor;
	SimpleCursorAdapter myAdapter;
	int[] toLayoutIDs = new int[] {R.id.display_term};
	String[] fromColumns = new String[] {MyLegislatorDatabaseAdapter.KEY_TYPE,
			MyLegislatorDatabaseAdapter.KEY_START_OF_TERM, 
			MyLegislatorDatabaseAdapter.KEY_END_OF_TERM, 
			MyLegislatorDatabaseAdapter.KEY_TERM_STATE,
			MyLegislatorDatabaseAdapter.KEY_TERM_DISTRICT
			};
	
	ListView legislatorsList;
	TextView termTitle;
	Panel thisPanel;
	InnerPanel thisInnerPannel;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.terms);
        mContext = this;
        dBAdapter = new MyLegislatorDatabaseAdapter(this);
	    dBAdapter.open();
        legislatorId = getIntent().getLongExtra(LEGISLATOR_ID, 1);
        primaryPanelColor = getIntent().getIntExtra(PANEL_BORDER, 1);
        graidentColor = getIntent().getIntExtra(GRADIENT_COLOR, 1);
        dividerResouce = getIntent().getIntExtra(DIVIDER_RESOURCE, 1);
        fullName = getIntent().getStringExtra(FULLNAME);
		myCursor = dBAdapter.fetchAllTerms(legislatorId);
		startManagingCursor(myCursor);
		myAdapter = new TermAdapter(this, 
                R.layout.term_adapter, 
                myCursor, 
                fromColumns, 
                toLayoutIDs); 
		legislatorsList = (ListView)findViewById(R.id.terms_list);
		legislatorsList.setAdapter(myAdapter);
		termTitle = (TextView)findViewById(R.id.term_title);
		termTitle.setText("Term history for " + fullName);
        thisPanel = (Panel)findViewById(R.id.term_panel);
        thisPanel.setBorderPaint(primaryPanelColor);
        thisInnerPannel = (InnerPanel)findViewById(R.id.legislator_inner_panel);
        thisInnerPannel.setGradientColor(graidentColor);
        Resources myResources= getResources();
        Drawable divider = myResources.getDrawable(dividerResouce);
        legislatorsList.setDivider(divider);        	
    }

    @Override
    public void onResume() {
    	super.onResume();
    	 myCursor.requery();
         myAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onDestroy() {
    	dBAdapter.close();
		super.onDestroy(); 
    }
}