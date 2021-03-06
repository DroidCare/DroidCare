package com.droidcare.control;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.droidcare.entity.*;

import android.content.Context;
import android.util.Pair;

/**
 * Fetches, stores and manages a list of {@link Appointment} objects.
 * @author Edwin Candinegara
 */

public abstract class AppointmentManager {
	/**
     * Interface used to allow the {@link SimpleHttpPost}
     * to run some code when it has finished executing.
     */
    public interface OnFinishListener {
    	/**
    	 * Declaration of onFinish method signature.
    	 * @param responseText	the status of the process. 
    	 */
        public abstract void onFinish(String responseText);
    }

    /**
     * An instance of {@link AppointmentManager}. Use this to promote singleton design pattern.
     * If the user is a patient, instantiate {@link #instance} into a {@link PatientAppointmentManager} object.
     * If the user is a consultant, instantiate {@link #instance} into a {@link ConsultantAppointmentManager} object.
     */
	private static AppointmentManager instance = null;
	
	/**
	 * A list of appointments which have been accepted by the consultants.
	 */
	private ArrayList<Appointment> acceptedAppointments;
	
	/**
	 * A list of appointments which are active and have not been accepted by the consultants.
	 */
	private ArrayList<Appointment> pendingAppointments;
	
	/**
	 * A list of appointments which have been rejected by the consultants.
	 */
	private ArrayList<Appointment> rejectedAppointments;
	
	/**
	 * A list of appointments which have finished.
	 */
	private ArrayList<Appointment> finishedAppointments;
	
	/**
	 * Constructs an {@link AppointmentManager} object and initiates all appointment lists.
	 */
	public AppointmentManager () {
		acceptedAppointments = new ArrayList<Appointment>();
		pendingAppointments = new ArrayList<Appointment>();
		rejectedAppointments = new ArrayList<Appointment>();
		finishedAppointments = new ArrayList<Appointment>();
	}
	
	/** 
	 * Returns {@link #instance}.
	 * @return returns {@link #instance}.
	 */
	public static AppointmentManager getInstance() {
        if(instance == null) {
            if(Global.getUserManager().getUser() == null) {
                return null;
            } else {
                return instance = Global.getUserManager().getUser().getType().equalsIgnoreCase("patient") ?
                        new PatientAppointmentManager() : new ConsultantAppointmentManager();
            }
        }
        
        return instance;
    }

	/**
	 * Fetches all appointments data related to this user and categorizes each appointment according to its status.
	 * @param onFinishListener	an interface in which the implementation of {@code onFinish} can be found.
	 */
    public void retrieveAppointmentList(OnFinishListener onFinishListener) {
        String sessionId = Global
                .getUserManager().getUser().getSessionId();
        if(sessionId == null || sessionId.isEmpty()) {
            return;
        }

//        Log.d("DEBUGGING", "retrieving sessionId = " + sessionId);
//        Log.d("DEBUGGING", Global.USER_APPOINTMENT_URL + String.format("/%d", Global.getUserManager().getUser().getId()));
        new SimpleHttpPost(new Pair<String, String>("session_id", sessionId)) {
            private OnFinishListener listener;
            public SimpleHttpPost init(OnFinishListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray messages = response.getJSONArray("message");
                            for(int i = 0, size = messages.length(); i < size; ++i) {
                                JSONObject params = messages.getJSONObject(i);

                                int		id = params.getInt("id"),
                                        patientId = params.getInt("patient_id"),
                                        consultantId = params.getInt("consultant_id"),
                                        previousId = params.getInt("previous_id");

                                String	dateTime = params.getString("date_time"),
                                        patientName = params.getString("patient_name"),
                                        consultantName = params.getString("consultant_name"),
                                        healthIssue = params.getString("health_issue"),
                                        attachment = params.getString("attachment"),
                                        type = params.getString("type"),
                                        referrerName = params.getString("referrer_name"),
                                        referrerClinic = params.getString("referrer_clinic"),
                                        remarks = params.getString("remarks"),
                                        status = params.getString("status");
                                
                                Appointment appointment = null;
                                if (type.equalsIgnoreCase(Appointment.NORMAL)) {
                                	appointment = new Appointment(id
                                			, patientId, consultantId, dateTime, patientName, consultantName
                                			, healthIssue, type, remarks, status);
                                } else if (type.equalsIgnoreCase(Appointment.REFERRAL)) {
                                	appointment = new ReferralAppointment(id, patientId
                                			, consultantId, dateTime, patientName, consultantName, healthIssue
                                			, type, remarks, status, referrerName, referrerClinic);
                                } else if (type.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
                                	appointment = new FollowUpAppointment(id, patientId
                                			, consultantId, dateTime, patientName, consultantName, healthIssue
                                			, type, remarks, status, attachment, previousId);
                                }
                                
                                // DOUBLE CHECK
                                if (appointment != null) {
	                                if (appointment.getStatus().equalsIgnoreCase(Appointment.ACCEPTED)) {
	                                    addAcceptedAppointment(appointment);
                                    } else if (appointment.getStatus().equalsIgnoreCase(Appointment.REJECTED)) {
	                                	addRejectedAppointment(appointment);
	                                } else if (appointment.getStatus().equalsIgnoreCase(Appointment.FINISHED)) {
	                                	addFinishedAppointment(appointment);
	                                } else if (appointment.getStatus().equalsIgnoreCase(Appointment.PENDING)) {
	                                	addPendingAppointment(appointment);
	                                }
                                }
                            }
                            
                            // Sort based on the dateTimeMillis -> ASCENDING
                            // Smaller dateTimeMillis = closer appointment date and time
//                            Collections.sort(AppointmentManager.this.acceptedAppointments);
//                            Collections.sort(AppointmentManager.this.pendingAppointments);
//                            Collections.sort(AppointmentManager.this.rejectedAppointments);
//                            Collections.sort(AppointmentManager.this.finishedAppointments);

                            break;
                            
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.onFinish(responseText);
            }
        }.init(onFinishListener).send(Global.USER_APPOINTMENT_URL);
//                + String.format("/%d", Global.getUserManager().getUser().getId()));
    }

	/**
	 * Adds an accepted {@link Appointment} object to {@link #acceptedAppointments}.
	 * @param appointment	an accepted {@link Appointment} object.
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean addAcceptedAppointment (Appointment appointment) {
		return acceptedAppointments.add(appointment);
	}
	
	/**
	 * Remove an accepted {@link Appointment} object from {@link #acceptedAppointments}
	 * @param appointment	an accepted {@link Appointment} object to be removed 
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean removeAcceptedAppointment (Appointment appointment) {
		int id = appointment.getId();
		for (int i = 0; i < this.acceptedAppointments.size(); i++) {
			if (this.acceptedAppointments.get(i).getId() == id) {
				this.acceptedAppointments.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the list of accepted {@link Appointment} objects.
	 * @return	{@link #acceptedAppointments}, the list of accepted {@link Appointment} objects.
	 */
	public ArrayList<Appointment> getAcceptedAppointments () {
		return acceptedAppointments;
	}
	
	/**
	 * Adds a pending {@link Appointment} object to {@link #pendingAppointments}.
	 * @param appointment	a pending {@link Appointment} object.
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean addPendingAppointment (Appointment appointment) {
		return pendingAppointments.add(appointment);
	}
	
	/**
	 * Remove a pending {@link Appointment} object from {@link #pendingAppointments}
	 * @param appointment	a pending {@link Appointment} object to be removed 
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean removePendingAppointment (Appointment appointment) {
		int id = appointment.getId();
		for (int i = 0; i < this.pendingAppointments.size(); i++) {
			if (this.pendingAppointments.get(i).getId() == id) {
				this.pendingAppointments.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the list of pending {@link Appointment} objects.
	 * @return	{@link #pendingAppointments}, the list of pending {@link Appointment} objects.
	 */
	public ArrayList<Appointment> getPendingAppointments () {
		return pendingAppointments;
	}
	
	/**
	 * Adds a rejected {@link Appointment} object to {@link #rejectedAppointments}.
	 * @param appointment	a rejected {@link Appointment} object.
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean addRejectedAppointment(Appointment appointment) {
		return rejectedAppointments.add(appointment);
	}
	
	/**
	 * Remove a rejected {@link Appointment} object from {@link #rejectedAppointments}
	 * @param appointment	a rejected {@link Appointment} object to be removed 
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean removeRejectedAppointment(Appointment appointment) {
		int id = appointment.getId();
		for (int i = 0; i < this.rejectedAppointments.size(); i++) {
			if (this.rejectedAppointments.get(i).getId() == id) {
				this.rejectedAppointments.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the list of rejected {@link Appointment} objects.
	 * @return	{@link #rejectedAppointments}, the list of rejected {@code Appointment} objects.
	 */
	public ArrayList<Appointment> getRejectedAppointments() {
		return rejectedAppointments;
	}
	
	/**
	 * Adds a finished {@link Appointment} object to {@link #finishedAppointments}.
	 * @param appointment	a finished {@link Appointment} object.
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean addFinishedAppointment(Appointment appointment) {
		return finishedAppointments.add(appointment);
	}
	
	/**
	 * Remove a finished {@link Appointment} object from {@link #finishedAppointments}
	 * @param appointment	a finished {@link Appointment} object to be removed 
	 * @return				{@code true} if success, {@code false} otherwise.
	 */
	public boolean removeFinishedAppointment(Appointment appointment) {
		int id = appointment.getId();
		for (int i = 0; i < this.finishedAppointments.size(); i++) {
			if (this.finishedAppointments.get(i).getId() == id) {
				this.finishedAppointments.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the list of finished {@link Appointment} objects.
	 * @return	{@link #finishedAppointments}, the list of finished {@link Appointment} objects.
	 */
	public ArrayList<Appointment> getFinishedAppointments() {
		return finishedAppointments;
	}
	
	/**
	 * Removes all accepted {@link Appointment} objects in {@link #acceptedAppointments}
	 */
	public void clearAcceptedAppointments () {
		acceptedAppointments.clear();
	}
	
	/**
	 * Removes all pending {@link Appointment} objects in {@link #pendingAppointments}
	 */
	public void clearPendingAppointments () {
		pendingAppointments.clear();
	}
	
	/**
	 * Removes all rejected {@link Appointment} objects in {@link #rejectedAppointments}
	 */
	public void clearRejectedAppointments() {
		rejectedAppointments.clear();
	}
	
	/**
	 * Removes all finished {@link Appointment} objects in {@link #finishedAppointments}
	 */
	public void clearFinishedAppointments() {
		finishedAppointments.clear();
	}
	
	/**
	 * Set all accepted appointment "ALARM" to remind the patient and consultant for an appointment
	 * one day in advance.
	 * @param context the context from which this method is called
	 */
	public void setAcceptedAppointmentAlarms (Context context) {
		for (Appointment a: this.acceptedAppointments) {
			Global.getAlarmSetter().setAcceptedAppointmentAlarm(context, a);
		}
	}
	
	/**
	 * Set all pending appointment "ALARM" to remind the consultant in case he/she still has
	 * some pending appointments which are going to due in 2 days.
	 * @param context the context from which this method is called.
	 */
	public void setPendingAppointmentAlarms (Context context) {
		for (Appointment a: this.pendingAppointments) {
			Global.getAlarmSetter().setPendingAppointmentAlarm(context, a);
		}
	}
	
	/**
	 * Set all missed pending appointment status to finished after the appointment date and time
	 * @param context the context from which this method is called
	 */
	public void updateMissedPendingAppointment (Context context) {
		for (Appointment a: this.pendingAppointments) { 
			Global.getAlarmSetter().setMissedPendingAppointmentAlarm(context, a);
		}
	}
	
	/**
	 * Remove the current {@link AppointmentManager} instance
	 */
    public static void removeManager() {
        instance = null;
    }
}