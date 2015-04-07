package com.droidcare.control;

import com.droidcare.entity.*;

import android.util.Pair;
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

    /**
     * Creates a new appointment and stores it into the database
     * @param patientId			the patient's ID
     * @param consultantId		the consultant's ID
     * @param dateTime			the appointment's date and time
     * @param healthIssue		the patient's health issue
     * @param attachment		the Base64 encoded image attachment string if it is a follow-up appointment 
     * @param type				the appointment type
     * @param referrerName		the referrer's name if it is a referral appointment
     * @param referrerClinic	the referrer's clinic if it is a referral appointment
     * @param previousId		the previous appointment ID if it is a follow-up appointment
     * @param onFinishListener	an OnFinishListener object determining what to do after the new appointment is stored in the database
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
    
    /**
     * Edit an existing pending appointment entry in database
     * @param appointment	the {@link Appointment} object to be modified
     */
    public void editAppointment (Appointment appointment, int patientId
            , int consultantId, String dateTime, String healthIssue, String referrerName, String referrerClinic
            , OnFinishListener onFinishListener) {
    	String previousId = "", attachmentImage = "";

        new SimpleHttpPost(new Pair<String, String>("id", "" + appointment.getId())
                , new Pair<String, String>("patient_id", "" + patientId)
                , new Pair<String, String>("consultant_id", "" + consultantId)
                , new Pair<String, String>("date_time", dateTime)
                , new Pair<String, String>("health_issue", healthIssue)
                , new Pair<String, String>("referrer_name", referrerName)
                , new Pair<String, String>("referrer_clinic", referrerClinic)
                , new Pair<String, String>("session_id", Global.getUserManager().getUser().getSessionId())) {
            private OnFinishListener listener;
            public SimpleHttpPost init(OnFinishListener listener) {
                this.listener = listener;
                return this;
            }
            @Override
            public void onFinish(String responseText) {
                listener.onFinish(responseText);
            }
        }.init(onFinishListener).send(Global.APPOINTMENT_EDIT_URL);
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
