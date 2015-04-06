package com.droidcare.control;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.droidcare.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
	/**
	 * The context of the appointment list
	 */
	private Context context;
	
	/**
	 * Constructs a {@link AppointmentListAdapter} object
	 * @param context	the context of this appointment list
	 * @param items		the items to be displayed in the list
	 */
	public AppointmentListAdapter (Context context, List<Appointment> items) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
	}
	
	/**
	 * 
	 * @author Edwin Candinegara
	 * A class which holds all views used in the Appointment List
	 *
	 */
	private class ViewHolder {
		// ALL VIEWS THAT NEED TO BE CUSTOMIZED ARE PUT HERE
		private TextView appointmentIdText, appointmentTypeText, appointmentDateTimeText,
				 		 appointmentHealthIssueText, appointmentConsultantText, appointmentPatientText;
	}
	
	/**
	 * Sets up the content of each list item
	 */
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		// Getting the corresponding appointment based on the position on the list
		Appointment appointment = getItem(position);
		
		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null) {
			// The view layout for each list item
			convertView = mInflater.inflate(R.layout.appointment_list_item, parent, false);
			holder = new ViewHolder();
			
			// Different layout for different user
			if (Global.getUserManager().getUser().getType().equalsIgnoreCase("patient")) {
				((LinearLayout) convertView.findViewById(R.id.LL_AppointmentPatient)).setVisibility(View.GONE);
				((LinearLayout) convertView.findViewById(R.id.LL_AppointmentConsultant)).setVisibility(View.VISIBLE);
			} else if (Global.getUserManager().getUser().getType().equalsIgnoreCase("consultant")) {
				((LinearLayout) convertView.findViewById(R.id.LL_AppointmentPatient)).setVisibility(View.VISIBLE);
				((LinearLayout) convertView.findViewById(R.id.LL_AppointmentConsultant)).setVisibility(View.GONE);
			}
			
			// Take all views needed to be customized here
			holder.appointmentIdText = (TextView) convertView.findViewById(R.id.Field_AppointmentID);
			holder.appointmentTypeText = (TextView) convertView.findViewById(R.id.Field_AppointmentType);
			holder.appointmentDateTimeText = (TextView) convertView.findViewById(R.id.Field_AppointmentDateTime);
			holder.appointmentHealthIssueText = (TextView) convertView.findViewById(R.id.Field_AppointmentHealthIssue);
			holder.appointmentConsultantText = (TextView) convertView.findViewById(R.id.Field_AppointmentConsultant);
			holder.appointmentPatientText = (TextView) convertView.findViewById(R.id.Field_AppointmentPatient);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (appointment != null) {
			Date d = new Date(appointment.getDateTimeMillis());
			String dateTimeString = Global.dateFormat.format(d);
			
			// Customize the view based on each appointment details
			holder.appointmentIdText.setText("" + appointment.getId());
			holder.appointmentTypeText.setText(appointment.getType().substring(0, 1).toUpperCase() 
					  						   + appointment.getType().substring(1));
			holder.appointmentDateTimeText.setText(dateTimeString);
			holder.appointmentHealthIssueText.setText(appointment.getHealthIssue());
			holder.appointmentConsultantText.setText(appointment.getConsultantName());
			holder.appointmentPatientText.setText(appointment.getPatientName());
		}
		
		return convertView;
	}
}