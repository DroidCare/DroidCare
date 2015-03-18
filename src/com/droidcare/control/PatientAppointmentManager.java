package com.droidcare.control;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.control.AppointmentManager.OnFinishListener;
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
    
    public void createAppointment(int patientId, int consultantId
            , String dateTime, String healthIssue, String attachment
            , String type, String referrerName, String referrerClinic
            , int previousId, OnFinishListener onFinishListener) {
        new SimpleHttpPost(new Pair<String, String>("patient_id", "" + patientId)
                , new Pair<String, String>("consultant_id", "" + consultantId)
                , new Pair<String, String>("date_time", dateTime)
                , new Pair<String, String>("health_issue", healthIssue)
                , new Pair<String, String>("attachment", attachment)
                , new Pair<String, String>("type", type)
                , new Pair<String, String>("referrer_name", referrerName)
                , new Pair<String, String>("referrer_clinic", referrerClinic)
                , new Pair<String, String>("previous_id", "" + (previousId == -1 ? "" : previousId))
                , new Pair<String, String>("session_id", Global.getUserManager().getUser().getSessionId()) ) {
            private OnFinishListener listener;
            public SimpleHttpPost init(OnFinishListener listener) {
                this.listener = listener;
                return this;
            }
            @Override
            public void onFinish(String responseText) {
                listener.onFinish(responseText);
            }
        }.init(onFinishListener).send(Global.APPOINTMENT_NEW_URL);
    }
    
    public void editAppointment (Appointment appointment) {
    	// USE POLYMORPHISM DEPENDING ON THE APPOINTMENT TYPE
    }
	
	/**
	 * Cancels a pending {@link Appointment} object.
	 * @param appointment	a pending {@link Appointment} object to be cancelled by the patient.
	 */
	public void cancelAppointment (Appointment appointment, OnFinishListener onFinishListener) {
		// Double check Pending status
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
		}
	}
}
