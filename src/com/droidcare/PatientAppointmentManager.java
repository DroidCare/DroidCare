package com.droidcare;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author Edwin Candinegara
 * Responsible for creating, modifying, and cancelling the patient's {@link Appointment} objects.
 *
 */

public class PatientAppointmentManager extends AppointmentManager {
	/**
	 * 	An empty constructor. Only call super().
	 */
	public PatientAppointmentManager () {
		super();
	}
	
	/*
	 * Add Patient specific methods here
	 * create, modify, cancel
	 */
	
	/**
	 * Cancels a pending {@link Appointment} object.
	 * @param context		the activity context from which this method is called.
	 * @param appointment	a pending {@link Appointment} object to be cancelled by the patient.
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
