package com.berg.mylegislator.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.berg.mylegislator.MyLegislatorDatabaseAdapter;
import com.berg.mylegislator.R;

public class TermAdapter extends SimpleCursorAdapter
{
    private Context context;
    private int layout;
	private StringBuffer termText= new StringBuffer("");

    public TermAdapter(Context context, int layout, Cursor
c,String[] from, int[] to)
    {
        super(context, layout, c, from, to);
        this.context = context;
        this.layout = layout;
    }

    /**
     * Custom view translates columns into appropriate text, images
etc.
     *
     * (non-Javadoc)
     * @see android.widget.CursorAdapter#getView(int,
android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup
parent)
    {
        // Cursor to current item
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        LayoutInflater inflate = LayoutInflater.from(context);
        View v = inflate.inflate(layout, parent, false);

        TextView nameControl = (TextView) v.findViewById(R.id.display_term);
        if (nameControl != null)
        {
        	int typeIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_TYPE);
            int startIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_START_OF_TERM);
            int endsIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_END_OF_TERM);
			int stateIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_TERM_STATE);
			int districtIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_TERM_DISTRICT);

                
            String type = cursor.getString(typeIndex);
			String started = cursor.getString(startIndex);
			String ends = cursor.getString(endsIndex);
			String state = cursor.getString(stateIndex);	
			String district = cursor.getString(districtIndex);	
			
			termText.setLength(0);
			termText.append("Term for ");
			termText.append(type);
			termText.append(" of ");
			if (!(district.equals(""))) {
				termText.append("district ");
				termText.append(district);
				termText.append(" of ");
			}
			termText.append(state);
			termText.append(" began ");
			termText.append(started);
			if (position == 0)
				termText.append(" and will end in ");
			else
				termText.append(" and ended in ");
			termText.append(ends);

            nameControl.setText(termText);
        }
        return v;
    }
}