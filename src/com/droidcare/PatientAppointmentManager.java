package com.droidcare;

public class PatientAppointmentManager extends AppointmentManager {
	public PatientAppointmentManager () {
		super();
	}
	
	/*
	 * Add Patient specific methods here
	 * create, modify, cancel
	 */
	
	public void cancelAppointment (Appointment appointment) {
		// Double check Pending status
		if (appointment.getStatus() == Appointment.PENDING) {
			this.removePendingAppointment(appointment);
		}
		
		// @pciang: please DELETE the entry in the database
	}
}
