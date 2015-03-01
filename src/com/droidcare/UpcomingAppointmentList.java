package com.droidcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import java.util.ArrayList;
import android.widget.ListView;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This class is a list fragment subclass which holds and shows all appointments with the "Upcoming" status.
 * Clicking on one item of this list will bring User to the corresponding details of the appointment.
 *
 */

public class UpcomingAppointmentList extends ListFragment {
	private ArrayList<Appointment> upcomingAppointmentList;
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the upcoming appointment list from the Global AppointmentManager
		this.fetchList();
		mAdapter = new AppointmentListAdapter(getActivity(), this.upcomingAppointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Creating a new intent
		Intent intent = new Intent(getActivity().getApplicationContext(), AppointmentDetailsActivity.class);
		Appointment a = this.upcomingAppointmentList.get(position);
		intent.putExtra("appointment", a);
		startActivity(intent);
	}
	
	// CALL THIS WHENEVER A CHANGE IS MADE IN THE APPOINTMENT LIST!!
	// Add the new Appointment object to the list in AppointmentManager
	public void fetchList () {
		this.upcomingAppointmentList = Global.getAppointmentManager().getUpcomingAppointments();
	}
}