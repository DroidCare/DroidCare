package com.droidcare.control;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

    public interface OnFinishListener {
        public abstract void onFinish(String responseText);
    }
	
	/**
	 * Accepts a pending {@link Appointment} object.
	 * @param appointment	a pending {@link Appointment} object to be accepted by the consultant.
	 */
	public void acceptAppointment (Appointment appointment, OnFinishListener onFinishListener) {
		// NEED TO SEND EMAIL NOTIFICATION TO THE USER!
		if (appointment.getStatus().equalsIgnoreCase(Appointment.PENDING)) {
            new SimpleHttpPost(new Pair<String, String>("id", "" + appointment.getId())
                    , new Pair<String, String>("status", Appointment.ACCEPTED)
                    , new Pair<String, String>("remarks", appointment.getRemarks())
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
                                appointment.setStatus(Appointment.ACCEPTED);
                                ConsultantAppointmentManager.this.removePendingAppointment(appointment);
                                ConsultantAppointmentManager.this.addAcceptedAppointment(appointment);
                                break;
                            default:
                                break;
                        }
                    // Do nothing on exception
                    } catch (JSONException e) {
                    }
                    listener.onFinish(responseText);
                }
            }.init(onFinishListener, appointment).send(Global.APPOINTMENT_STATUS_URL);
		}
	}
	
	/**
	 * Rejects a pending {@link Appointment} object.
	 * @param appointment	a pending {@link Appointment} object to be rejected by the consultant.
	 */
	public void rejectAppointment (Appointment appointment, OnFinishListener onFinishListener) {
		// NEED TO SEND EMAIL NOTIFICATION TO THE USER!
		if (appointment.getStatus().equalsIgnoreCase(Appointment.PENDING)) {
            new SimpleHttpPost(new Pair<String, String>("id", "" + appointment.getId())
                    , new Pair<String, String>("status", Appointment.REJECTED)
                    , new Pair<String, String>("remarks", appointment.getRemarks())
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
                                appointment.setStatus(Appointment.REJECTED);
                                ConsultantAppointmentManager.this.removePendingAppointment(appointment);
                                ConsultantAppointmentManager.this.addRejectedAppointment(appointment);
                                break;
                            default:
                                break;
                        }
                    // Do nothing on exception
                    } catch (JSONException e) {
                    }
                    listener.onFinish(responseText);
                }
            }.init(onFinishListener, appointment).send(Global.APPOINTMENT_STATUS_URL);
		}
	}
}
