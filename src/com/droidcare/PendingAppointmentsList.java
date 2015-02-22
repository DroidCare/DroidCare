package com.droidcare;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import android.widget.Toast;

public class PendingAppointmentsList extends ListFragment {
	private List<Appointment> upcomingAppointmentList;
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.upcomingAppointmentList = new ArrayList<Appointment>();
		this.upcomingAppointmentList.add(new Appointment());
		this.upcomingAppointmentList.add(new Appointment());
		this.upcomingAppointmentList.add(new Appointment());
		
		mAdapter = new AppointmentListAdapter(getActivity(), this.upcomingAppointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
	}
}