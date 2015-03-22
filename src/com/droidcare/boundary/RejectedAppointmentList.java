package com.droidcare.boundary;

import java.util.ArrayList;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class RejectedAppointmentList extends ListFragment {
	private ArrayList<Appointment> rejectedAppointmentList;
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.fetchList();
		mAdapter = new AppointmentListAdapter(getActivity(), this.rejectedAppointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Creating a new intent
		Intent intent = new Intent(getActivity(), AppointmentDetailsActivity.class);
		Appointment a = this.rejectedAppointmentList.get(position);
		
		// USE OF POLYMORPHISM
		if (a.getType().equalsIgnoreCase(Appointment.NORMAL)) {
			intent.putExtra("appointment", a);
		} else if (a.getType().equalsIgnoreCase(Appointment.REFERRAL)) {
			intent.putExtra("appointment", (ReferralAppointment) a);
		} else if (a.getType().equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			intent.putExtra("appointment", (FollowUpAppointment) a);
		}
		
		startActivity(intent);
	}
	
	/**
	 * Fetch the appointment list from {@link AppointmentManager}
	 */
	public void fetchList() {
		this.rejectedAppointmentList = Global.getAppointmentManager().getRejectedAppointments();
	}
}