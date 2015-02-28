package com.droidcare;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

/*
 * Hi @edocsss, you can be my co-author here! :v
 */

public class RejectedAppointmentList extends ListFragment {
	private ArrayList<Appointment> appointmentList;
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.fetchList();
		mAdapter = new AppointmentListAdapter(getActivity(), this.appointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Do nothing at the moment.
	}
	
	/**
	 * FOR THE SAKE OF @pciang FINGERS, PLEASE HAVE A SHORTER METHOD NAME
	 * Use a more "descriptive" word please -___-
	 */
	public void fetchList() {
		appointmentList = Global.getAppointmentManager().getRejectedAppointments();
	}
}