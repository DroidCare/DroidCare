package com.droidcare.control;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author Edwin Candinegara
 * A subclass of {@link AppointmentManager}. Instantiated only when the user is a consultant. This class
 * is responsible for accepting and rejecting pending appointment.
 *
 */

public class ConsultantAppointmentManager extends AppointmentManager {
	/**
	 * Empty constructor. Only call super().
	 */
	public ConsultantAppointmentManager () {
		super();
	}
	
	/**
	 * Accepts a pending {@link Appointment} object.
	 * @param context		the activity context from which this method is called.
	 * @param appointment	a pending {@link Appointment} object to be accepted by the consultant.
	 */
	public void acceptAppointment (Context context, Appointment appointment) {
		if (appointment.getStatus() == Appointment.PENDING) {
			appointment.setStatus(Appointment.ACCEPTED);
			this.removePendingAppointment(appointment);
			this.addAcceptedAppointment(appointment);
			
			// UPDATE DATABASE HERE!
			// While updating, give a progress bar probably?
			this.updateStatusDB(appointment);
			
			// Add an Alarm
			Global.getAlarmSetter().setAlarm(context, appointment);
			
			// ON FINISH -> simple feedback
			Toast toast = Toast.makeText(context, "Appointment accepted!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	/**
	 * Rejects a pending {@link Appointment} object.
	 * @param context		the context from which this method is called.
	 * @param appointment	a pending {@link Appointment} object to be rejected by the consultant.
	 */
	public void rejectAppointment (Context context, Appointment appointment) {
		if (appointment.getStatus() == Appointment.PENDING) {
			appointment.setStatus(Appointment.REJECTED);
			this.removePendingAppointment(appointment);
			this.addRejectedAppointment(appointment);
			
			// UPDATE DATABASE HERE!!
			// While updating, give a progress bar probably?
			this.updateStatusDB(appointment);
			
			// ON FINISH -> simple feedback
			Toast toast = Toast.makeText(context, "Appointment rejected!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
