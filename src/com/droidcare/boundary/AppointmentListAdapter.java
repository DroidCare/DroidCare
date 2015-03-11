package com.droidcare.boundary;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

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
		// ALL VIEWS THAT NEED TO BE CUSTOMIZED ARE PUT HERE
		TextView appointmentIdText, appointmentTypeText, appointmentDateTimeText,
				 appointmentHealthIssueText, appointmentConsultantText;
	}
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		// Getting the corresponding appointment based on the position on the list
		Appointment appointment = (Appointment) getItem(position);
		
		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null) {
			// The view layout for each list item
			convertView = mInflater.inflate(R.layout.appointment_list_item, parent, false);
			holder = new ViewHolder();
			
			// Take all views needed to be customized here
			holder.appointmentIdText = (TextView) convertView.findViewById(R.id.Field_AppointmentID);
			holder.appointmentTypeText = (TextView) convertView.findViewById(R.id.Field_AppointmentType);
			holder.appointmentDateTimeText = (TextView) convertView.findViewById(R.id.Field_AppointmentDateTime);
			holder.appointmentHealthIssueText = (TextView) convertView.findViewById(R.id.Field_AppointmentHealthIssue);
			holder.appointmentConsultantText = (TextView) convertView.findViewById(R.id.Field_AppointmentConsultant);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (appointment != null) {
			Date d = new Date(appointment.getDateTimeMillis());
			String dateTimeString = Global.dateFormat.format(d);
			
			// Customize the view based on each appointment details
			holder.appointmentIdText.setText("" + appointment.getId());
			holder.appointmentTypeText.setText(appointment.getType());
			holder.appointmentDateTimeText.setText(dateTimeString);
			holder.appointmentHealthIssueText.setText(appointment.getHealthIssue());
			holder.appointmentConsultantText.setText(appointment.getConsultantName());
		}
		
		return convertView;
	}
}