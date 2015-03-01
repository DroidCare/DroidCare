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
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		// Getting the corresponding appointment based on the position on the list
		Appointment appointment = (Appointment) getItem(position);
		
		ViewHolder holder = null;
		View viewToUse = null;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null) {
			// The view layout for each list item
			viewToUse = mInflater.inflate(R.layout.appointment_list_item, parent, false);
			holder = new ViewHolder();
			
			// Take all views needed to be customized here
			holder.titleText = (TextView) viewToUse.findViewById(R.id.appointment_list_item_textview);
		}
		
		// Customize the view based on each appointment details
		holder.titleText.setText("" + appointment.getId());
		return viewToUse;
	}
}