package com.droidcare;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppointmentManager {
	
	// Force singleton
	private static AppointmentManager instance = new AppointmentManager();
	
	private ArrayList<Appointment> upcomingAppointments;
	private ArrayList<Appointment> pendingAppointments;
	private ArrayList<Appointment> rejectedAppointments;
	
	private AppointmentManager () {
		upcomingAppointments = new ArrayList<Appointment>();
		pendingAppointments = new ArrayList<Appointment>();
		rejectedAppointments = new ArrayList<Appointment>();
	}
	
	public static AppointmentManager getInstance() {
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
							healthIssue = param.getString("health_issue"),
							attachmentPaths = param.getString("attachment_paths"),
							type = param.getString("type"),
							referrerName = param.getString("referrer_name"),
							referrerClinic = param.getString("referrer_clinic"),
							remarks = param.getString("remarks"),
							status = param.getString("status");
					
					Appointment appointment = new Appointment(id
							, patientId, consultantId, dateTime, healthIssue
							, attachmentPaths, type, referrerName, referrerClinic
							, previousId, remarks, status);
					
					/*
					 * @pciang will continue from here!
					 */
				}
				break;
				
		// Do nothing on exception and status != 0
			default:
				break;
			}
		} catch (JSONException e) {
		}
		
		return false;
	}
	
	public void addUpcomingAppointment (Appointment appointment) {
		upcomingAppointments.add(appointment);
	}
	
	public ArrayList<Appointment> getUpcomingAppointments () {
		return upcomingAppointments;
	}
	
	public void addPendingAppointment (Appointment appointment) {
		pendingAppointments.add(appointment);
	}
	
	public ArrayList<Appointment> getPendingAppointments () {
		return pendingAppointments;
	}
	
	public void addRejectedAppointment(Appointment appointment) {
		rejectedAppointments.add(appointment);
	}
	
	public ArrayList<Appointment> getRejectedAppointments() {
		return rejectedAppointments;
	}
	
	public void clearUpcomingAppointments () {
		upcomingAppointments.clear();
	}
	
	public void clearPendingAppointments () {
		pendingAppointments.clear();
	}
	
	public void clearRejectedAppointments() {
		rejectedAppointments.clear();
	}
}
