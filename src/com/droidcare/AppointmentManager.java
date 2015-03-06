package com.droidcare;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.util.Pair;

public abstract class AppointmentManager {
    public interface OnFinishListener {
        public abstract void onFinish(String responseText);
    }

	// Force singleton
	// Use polymorphism
	private static AppointmentManager instance;
	
	private ArrayList<Appointment> acceptedAppointments;
	private ArrayList<Appointment> pendingAppointments;
	private ArrayList<Appointment> rejectedAppointments;
	private ArrayList<Appointment> finishedAppointments;
	
	public AppointmentManager () {
		acceptedAppointments = new ArrayList<Appointment>();
		pendingAppointments = new ArrayList<Appointment>();
		rejectedAppointments = new ArrayList<Appointment>();
		finishedAppointments = new ArrayList<Appointment>();
	}
	
	public static AppointmentManager getInstance() {
		if (Global.getUserManager().getUser().getType().equalsIgnoreCase("patient")) {
			instance = new PatientAppointmentManager();
		} else {
			instance = new ConsultantAppointmentManager();
		}
		
		return instance;
	}
	
	/**
	 * This method is blocking. Call this method in a new AsyncTask.
	 * @return Returns <i>true</i> if manager successfully obtained list of appointments.
	 */
	/*
    public boolean fetchAppointmentList() {
		String sessionId = Global.getAppSession().getString("session_id");
		if(sessionId == null) {
			// Unsuccessful
			return false;
		}
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("session_id", sessionId);
		
		String responseText = new HttpPostRequest(data).send(Global.USER_APPOINTMENT_URL);
		
		int responseStatus = -1;
		try {
			JSONObject response = new JSONObject(responseText);
			
			responseStatus = response.getInt("status");
			switch(responseStatus) {
			
			case 0:
				JSONArray paramList = response.getJSONArray("message");
				
				for(int i = 0, size = paramList.length(); i < size; ++i){
					JSONObject param = paramList.getJSONObject(i);
					
					int		id = param.getInt("id"),
							patientId = param.getInt("patient_id"),
							consultantId = param.getInt("consultant_id"),
							previousId = param.getInt("previous_id");
					
					String	dateTime = param.getString("date_time"),
							patientName = param.getString("patient_name"),
							consultantName = param.getString("consultant_name"),
							healthIssue = param.getString("health_issue"),
							attachmentPaths = param.getString("attachment_paths"),
							type = param.getString("type"),
							referrerName = param.getString("referrer_name"),
							referrerClinic = param.getString("referrer_clinic"),
							remarks = param.getString("remarks"),
							status = param.getString("status");
					
					Appointment appointment = new Appointment(id
							, patientId, consultantId, dateTime, patientName, consultantName
							, healthIssue, attachmentPaths, type, referrerName, referrerClinic
							, previousId, remarks, status);

					switch(appointment.getStatus()) {

					case Appointment.PENDING:
						addPendingAppointment(appointment);
						break;
					case Appointment.ACCEPTED:
						addAcceptedAppointment(appointment);
						break;
					case Appointment.REJECTED:
						addRejectedAppointment(appointment);
						break;
					case Appointment.FINISHED:
						addFinishedAppointment(appointment);
						break;

					default:
						break;
					}
				}
				
				return true;
				
		// Do nothing on exception and status != 0
			default:
				break;
			}
		} catch (JSONException e) {
		}
		
		return false;
	}
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
                Log.d("DEBUGGING", "responseText = " + responseText);
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray messages = response.getJSONArray("message");
                            Log.d("DEBUGGING", "now starting to loop messages JSONArray length = " + messages.length());
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
                                        attachmentPaths = params.getString("attachment_paths"),
                                        type = params.getString("type"),
                                        referrerName = params.getString("referrer_name"),
                                        referrerClinic = params.getString("referrer_clinic"),
                                        remarks = params.getString("remarks"),
                                        status = params.getString("status");

                                Appointment appointment = new Appointment(id
                                        , patientId, consultantId, dateTime, patientName, consultantName
                                        , healthIssue, attachmentPaths, type, referrerName, referrerClinic
                                        , previousId, remarks, status);

                                Log.d("DEBUGGING", "appointment.getStatus() = " + appointment.getStatus());
                                switch(appointment.getStatus()) {
                                    case Appointment.ACCEPTED:
                                        addAcceptedAppointment(appointment);
                                        break;
                                    case Appointment.REJECTED:
                                        addRejectedAppointment(appointment);
                                        break;
                                    case Appointment.FINISHED:
                                        addFinishedAppointment(appointment);
                                        break;
                                    case Appointment.PENDING:
                                        addPendingAppointment(appointment);
                                        break;
                                }
                            }

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


	
	public boolean addAcceptedAppointment (Appointment appointment) {
		return acceptedAppointments.add(appointment);
	}
	
	public boolean removeAcceptedAppointment (Appointment appointment) {
		return acceptedAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getAcceptedAppointments () {
		return acceptedAppointments;
	}
	
	public boolean addPendingAppointment (Appointment appointment) {
		return pendingAppointments.add(appointment);
	}
	
	public boolean removePendingAppointment (Appointment appointment) {
		return pendingAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getPendingAppointments () {
		return pendingAppointments;
	}
	
	public boolean addRejectedAppointment(Appointment appointment) {
		return rejectedAppointments.add(appointment);
	}
	
	public boolean removeRejectedAppointment(Appointment appointment) {
		return rejectedAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getRejectedAppointments() {
		return rejectedAppointments;
	}
	
	public boolean addFinishedAppointment(Appointment appointment) {
		return finishedAppointments.add(appointment);
	}
	
	public boolean removeFinishedAppointment(Appointment appointment) {
		return finishedAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getFinishedAppointments() {
		return finishedAppointments;
	}
	
	public void clearAcceptedAppointments () {
		acceptedAppointments.clear();
	}
	
	public void clearPendingAppointments () {
		pendingAppointments.clear();
	}
	
	public void clearRejectedAppointments() {
		rejectedAppointments.clear();
	}
	
	public void clearFinishedAppointments() {
		finishedAppointments.clear();
	}
}