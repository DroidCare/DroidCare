package com.droidcare;

import android.content.Context;
import android.widget.Toast;

public class PatientAppointmentManager extends AppointmentManager {
	public PatientAppointmentManager () {
		super();
	}
	
	/*
	 * Add Patient specific methods here
	 * create, modify, cancel
	 */
	
	public void cancelAppointment (Context context, Appointment appointment) {
		// Double check Pending status
		if (appointment.getStatus() == Appointment.PENDING) {
			this.removePendingAppointment(appointment);
			
			// @pciang: please DELETE the entry in the database
			// SIMPLE FEEDBACK
			Toast toast = Toast.makeText(context, "Appointment Cancelled", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
}
