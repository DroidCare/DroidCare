package com.droidcare.boundary;

import java.util.ArrayList;
import com.droidcare.control.*;
import com.droidcare.entity.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

/**
 * This class is a list fragment subclass which holds and shows all appointments with the "Rejected" status.
 * Clicking on one item of this list will bring User to the corresponding details of the appointment.
 * @author Stephanie
 */

public class RejectedAppointmentList extends ListFragment {
	/**
	 * A list of rejected appointments retrieved from {@link AppointmentManager}
	 */
	private ArrayList<Appointment> rejectedAppointmentList;
	
	/**
	 * The list adapter to display the appointments to the list fragment
	 */
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