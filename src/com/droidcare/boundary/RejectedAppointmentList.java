package com.droidcare.boundary;

import java.util.ArrayList;

import com.droidcare.control.Global;
import com.droidcare.entity.Appointment;
import com.droidcare.entity.FollowUpAppointment;
import com.droidcare.entity.ReferralAppointment;

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
		Intent intent = new Intent(getActivity().getApplicationContext(), AppointmentDetailsActivity.class);
		Appointment a = this.rejectedAppointmentList.get(position);
		intent.putExtra("appointmentType", a.getType());
		
		// USE OF POLYMORPHISM
		if (a.getType().equalsIgnoreCase(Appointment.NORMAL)) {
			intent.putExtra("appointment", a);
		} else if (a.getType().equalsIgnoreCase(Appointment.REFERRAL)) {
			intent.putExtra("referralAppointment", (ReferralAppointment) a);
		} else if (a.getType().equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			intent.putExtra("followUpAppointment", (FollowUpAppointment) a);
		}		
		
		startActivity(intent);
	}
	
	/**
	 * FOR THE SAKE OF @pciang FINGERS, PLEASE HAVE A SHORTER METHOD NAME
	 * Use a more "descriptive" word please -___-
	 */
	public void fetchList() {
		this.rejectedAppointmentList = Global.getAppointmentManager().getRejectedAppointments();
	}
}