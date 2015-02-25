package com.droidcare;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This adapter class is responsible for retrieving data from an ArrayList object
 * and transforming the data into items inside the ListFragment
 *
 */

public class AppointmentListAdapter extends ArrayAdapter<Appointment> {
	private Context context;
	
	public AppointmentListAdapter (Context context, List<Appointment> items) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
	}
	
	private class ViewHolder {
		TextView titleText;
	}
	
	public View getView (int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View viewToUse = null;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null) {
			viewToUse = mInflater.inflate(R.layout.appointment_item, parent, false);
			holder = new ViewHolder();
			holder.titleText = (TextView) viewToUse.findViewById(R.id.appointment_item_textview);
		}
		
		holder.titleText.setText("TESTING");
		return viewToUse;
	}
}