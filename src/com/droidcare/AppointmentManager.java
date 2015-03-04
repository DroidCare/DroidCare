package com.droidcare;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class AppointmentManager {
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
					
					// Log.d("appointment.getType()", "=" + appointment.getStatus());
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
	
	public void addAcceptedAppointment (Appointment appointment) {
		acceptedAppointments.add(appointment);
	}
	
	public void removeAcceptedAppointment (Appointment appointment) {
		acceptedAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getAcceptedAppointments () {
		return acceptedAppointments;
	}
	
	public void addPendingAppointment (Appointment appointment) {
		pendingAppointments.add(appointment);
	}
	
	public void removePendingAppointment (Appointment appointment) {
		pendingAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getPendingAppointments () {
		return pendingAppointments;
	}
	
	public void addRejectedAppointment(Appointment appointment) {
		rejectedAppointments.add(appointment);
	}
	
	public void removeRejectedAppointment(Appointment appointment) {
		rejectedAppointments.remove(appointment);
	}
	
	public ArrayList<Appointment> getRejectedAppointments() {
		return rejectedAppointments;
	}
	
	public void addFinishedAppointment(Appointment appointment) {
		finishedAppointments.add(appointment);
	}
	
	public void removeFinishedAppointment(Appointment appointment) {
		finishedAppointments.remove(appointment);
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