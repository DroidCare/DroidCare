package com.droidcare.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import java.util.ArrayList;
import com.droidcare.control.*;
import com.droidcare.entity.*;
import android.widget.ListView;

/**
 * This class is a list fragment subclass which holds and shows all appointments with the "Accepted" status.
 * Clicking on one item of this list will bring User to the corresponding details of the appointment.
 * @author Stephanie
 */

public class FinishedAppointmentList extends ListFragment {
	/**
	 * A list of finished appointments retrieved from {@link AppointmentManager}
	 */
	private ArrayList<Appointment> finishedAppointmentList;
	
	/**
	 * The list adapter to display the appointments to the list fragment
	 */
	private AppointmentListAdapter mAdapter;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the upcoming appointment list from the Global AppointmentManager
		this.fetchList();
		mAdapter = new AppointmentListAdapter(getActivity(), this.finishedAppointmentList);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Creating a new intent
		Intent intent = new Intent(getActivity(), AppointmentDetailsActivity.class);
		Appointment a = this.finishedAppointmentList.get(position);
		
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
	public void fetchList () {
		this.finishedAppointmentList = Global.getAppointmentManager().getFinishedAppointments();
	}
}