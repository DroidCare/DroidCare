package com.droidcare.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import java.util.ArrayList;

import com.droidcare.control.Global;
import com.droidcare.entity.Appointment;
import com.droidcare.entity.FollowUpAppointment;
import com.droidcare.entity.ReferralAppointment;

import android.widget.ListView;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This class is a list fragment subclass which holds and shows all appointments with the "Pending" status.
 * Clicking on one item of this list will bring User to the corresponding details of the appointment.
 *
 */

public class PendingAppointmentList extends ListFragment {
	private ArrayList<Appointment> pendingAppointmentList;
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the pending appointment list from the Global AppointmentManager
		this.fetchList();
		mAdapter = new AppointmentListAdapter(getActivity(), this.pendingAppointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Creating a new intent
		Intent intent = new Intent(getActivity().getApplicationContext(), AppointmentDetailsActivity.class);
		Appointment a = this.pendingAppointmentList.get(position);
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
	
	// CALL THIS WHENEVER A CHANGE IS MADE IN THE APPOINTMENT LIST!!
	// Add the new Appointment object to the list in AppointmentManager
	public void fetchList () {
		this.pendingAppointmentList = Global.getAppointmentManager().getPendingAppointments();
	}
}