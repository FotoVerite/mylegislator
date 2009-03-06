package com.berg.mylegislator.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.berg.mylegislator.MyLegislatorDatabaseAdapter;
import com.berg.mylegislator.R;

public class LegislatorAdapter extends SimpleCursorAdapter
{
    private Context context;
    private int layout;
    private StringBuffer district = new StringBuffer("");

    public LegislatorAdapter(Context context, int layout, Cursor
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
        String party = null;
        // Cursor to current item
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        LayoutInflater inflate = LayoutInflater.from(context);
        View v = inflate.inflate(layout, parent, false);

        TextView nameControl = (TextView) v.findViewById(R.id.first_name);
        if (nameControl != null)
        {
                int firstNameIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_FIRST_NAME);
                int middleNameIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_MIDDLE_NAME);
				int lastNameIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_LAST_NAME);
                
                String firstName = cursor.getString(firstNameIndex);
				String middleName = cursor.getString(middleNameIndex);
				String lastName = cursor.getString(lastNameIndex);	
                nameControl.setText(firstName + " " + middleName + " " + lastName);
                Drawable icon = Drawable.createFromPath("com.berg.mylegislator/res/drawable/democrate_icon.png"); 
                nameControl.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }

        TextView partyControl = (TextView)v.findViewById(R.id.party);
        if (nameControl != null)
        {
                int partyIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_PARTY);
                int districtIndex = cursor.getColumnIndex(MyLegislatorDatabaseAdapter.KEY_DISTRICT);

				party = cursor.getString(partyIndex).trim();
				String districtText = cursor.getString(districtIndex);
				district.setLength(0);
				if (districtText.length() < 3) {
					district.append("District ");
				}
				district.append(districtText);
 				partyControl.setText(district);
 				partyControl.setPadding(0, 0, 5, 0);
        }
        ImageView iconControl = (ImageView)v.findViewById(R.id.party_icon);
        if (nameControl != null)
        {	if(party.equals("R"))
        		iconControl.setImageResource(R.drawable.republican_icon);
		    if(party.equals("D"))
				iconControl.setImageResource(R.drawable.democrat_icon);
        }
        Log.d("MyLegislator", party);

        return v;
    }
}