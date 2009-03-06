package com.berg.mylegislator;

import com.berg.mylegislator.view.InnerPanel;
import com.berg.mylegislator.view.Panel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

public class Committees extends Activity {
	
	protected static final String LEGISLATOR_ID = "com.berg.mylegislator.legislator_id";
	protected static final String PANEL_BORDER= "com.berg.mylegislator.panel_border";
	protected static final String GRADIENT_COLOR = "com.berg.mylegislator.gradient_color";
	protected static final String FULLNAME = "com.berg.mylegislator.fullname";
	protected static final String DIVIDER_RESOURCE = "com.berg.mylegislator.divider_resource";

	
	private static long legislatorId;
	private static int paneBorder;
	private static int gradientColor;
	private static int dividerResouce;

	private static String fullName;

	MyLegislatorDatabaseAdapter dBAdapter;
	Cursor committeesCursor;
	Cursor subcommitteesCursor;
	TextView mainTitle;
	Panel thisPanel;
	InnerPanel thisInnerPannel;
	
    private ExpandableListAdapter mAdapter;

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.committees);
        legislatorId = getIntent().getLongExtra(LEGISLATOR_ID, 1);
        paneBorder = getIntent().getIntExtra(PANEL_BORDER, 1);
        gradientColor = getIntent().getIntExtra(GRADIENT_COLOR, 1);
        dividerResouce = getIntent().getIntExtra(DIVIDER_RESOURCE, 1);
        fullName = getIntent().getStringExtra(FULLNAME);
        Resources myResources= getResources();
        Drawable divider = myResources.getDrawable(dividerResouce);
        dBAdapter = new MyLegislatorDatabaseAdapter(getBaseContext());
        dBAdapter.open();
        committeesCursor = dBAdapter.fetchAllCommittes(legislatorId);
        startManagingCursor(committeesCursor);

        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(committeesCursor,
                this,
                R.layout.expandable_list_view,
                R.layout.expandable_list_view,
                new String[] {MyLegislatorDatabaseAdapter.KEY_COMMITTEE_NAME}, // Name for group layouts
                new int[] {R.id.s},
                new String[] {MyLegislatorDatabaseAdapter.KEY_COMMITTEE_NAME, MyLegislatorDatabaseAdapter.KEY_ROLE}, // Number for child layouts
                new int[] {R.id.s, android.R.id.text2});
        ExpandableListView legislatorsList = (ExpandableListView)findViewById(R.id.committees_list);
		legislatorsList.setDivider(divider);
		legislatorsList.setChildDivider(divider);
		legislatorsList.setAdapter(mAdapter);
        TextView mainTitle = (TextView)findViewById(R.id.main_title);
        mainTitle.setText("Committees for " + fullName);
        thisPanel = (Panel)findViewById(R.id.term_panel);
        thisPanel.setBorderPaint(paneBorder);
        thisInnerPannel = (InnerPanel)findViewById(R.id.legislator_inner_panel);
        thisInnerPannel.setGradientColor(gradientColor);

    }

    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        public MyExpandableListAdapter(Cursor cursor, Context context, int groupLayout,
                int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                int[] childrenTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                    childrenTo);
        }

        protected Cursor getChildrenCursor(Cursor committeesCursor) {
            // Given the group, we return a cursor for all the children within that group 
            Log.d("Ext", "getGroupId " + committeesCursor);
        	long subcommitteeForeignKey= committeesCursor.getLong(0);
        	return dBAdapter.fetchAllSubCommittees(subcommitteeForeignKey);
        }

    }
    
    @Override
    public void onDestroy() {
    	dBAdapter.close();
		super.onDestroy(); 
    }

}
