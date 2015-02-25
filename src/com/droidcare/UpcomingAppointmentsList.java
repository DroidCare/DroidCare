package com.droidcare;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This class is a list fragment subclass which holds and shows all appointments with the "Upcoming" status.
 * Clicking on one item of this list will bring User to the corresponding details of the appointment.
 *
 */

public class UpcomingAppointmentsList extends ListFragment {
	private ArrayList<Appointment> upcomingAppointmentList;
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the upcoming appointment list from the Global AppointmentManager 
		this.upcomingAppointmentList = Global.getAppointmentManager().getPendingAppointments();
		mAdapter = new AppointmentListAdapter(getActivity(), this.upcomingAppointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
	}
}