package com.droidcare.control;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

    public interface OnFinishListener {
        public abstract void onFinish(String responseText);
    }

	/*
	 * Add Patient specific methods here
	 * create, modify, cancel
	 */
	
	/**
	 * Cancels a pending {@link Appointment} object.
	 * @param appointment	a pending {@link Appointment} object to be cancelled by the patient.
	 */
	public void cancelAppointment (Appointment appointment, OnFinishListener onFinishListener) {
		// Double check Pending status
<<<<<<< HEAD
		if (appointment.getStatus().equalsIgnoreCase(Appointment.PENDING)) {
            new SimpleHttpPost(new Pair<String, String>("id", "" + appointment.getId())
                    , new Pair<String, String>("session_id", Global.getUserManager().getUser().getSessionId())) {
                private OnFinishListener listener;
                private Appointment appointment;
                public SimpleHttpPost init(OnFinishListener listener, Appointment appointment) {
                    this.listener = listener;
                    this.appointment = appointment;
                    return this;
                }

                @Override
                public void onFinish(String responseText) {
                    try {
                        JSONObject response = new JSONObject(responseText);
                        switch(response.getInt("status")) {
                            case 0:
                                PatientAppointmentManager.this.removePendingAppointment(appointment);
                                break;
                            default:
                                break;
                        }
                    // Do nothing on exception
                    } catch (JSONException e) {
                    }
                    listener.onFinish(responseText);
                }
            }.init(onFinishListener, appointment).send(Global.APPOINTMENT_CANCEL_URL);
=======
		if (appointment.getStatus() == Appointment.PENDING) {
			this.removePendingAppointment(appointment);
			
			// @pciang: please DELETE the entry in the database
			this.deleteAppointmentDB(appointment);
			
			// SIMPLE FEEDBACK
			Toast toast = Toast.makeText(context, "Appointment cancelled!", Toast.LENGTH_SHORT);
			toast.show();
>>>>>>> origin/master
		}
	}
}
