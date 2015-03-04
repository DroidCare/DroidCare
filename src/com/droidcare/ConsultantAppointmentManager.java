package com.droidcare;

import android.content.Context;
import android.widget.Toast;

public class ConsultantAppointmentManager extends AppointmentManager {
	public ConsultantAppointmentManager () {
		super();
	}
	
	/*
	 * Add Consultant specific methods here
	 */
	
	public void acceptAppointment (Context context, Appointment appointment) {
		if (appointment.getStatus() == Appointment.PENDING) {
			appointment.setStatus(Appointment.ACCEPTED);
			this.removePendingAppointment(appointment);
			this.addAcceptedAppointment(appointment);
			
			// UPDATE DATABASE HERE!
			// While updating, give a progress bar probably?
			
			// ON FINISH -> simple feedback
			Toast toast = Toast.makeText(context, "Appointment Accepted", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void rejectAppointment (Context context, Appointment appointment) {
		if (appointment.getStatus() == Appointment.PENDING) {
			appointment.setStatus(Appointment.REJECTED);
			this.removePendingAppointment(appointment);
			this.addRejectedAppointment(appointment);
			
			// UPDATE DATABASE HERE!!
			// While updating, give a progress bar probably?
			
			// ON FINISH -> simple feedback
			Toast toast = Toast.makeText(context, "Appointment Rejected", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
