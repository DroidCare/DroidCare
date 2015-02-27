package com.droidcare;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class Global {
	public static String APP_NAME = "DroidCare";
	public static String BASE_URL = "https://dc-kenrick95.rhcloud.com/";
	public static String USER_URL = BASE_URL + "user/";
	public static String APPOINTMENT_URL = BASE_URL + "appointment/";
	
	public static String USER_LOGIN_URL = USER_URL + "login";
	public static String USER_REGISTER_URL = USER_URL + "register";
	public static String USER_UPDATE_URL = USER_URL + "update";
	public static String USER_LOGOUT_URL = USER_URL + "logout";
	
	public static String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	
	public static String USER_APPOINTMENT_URL = APPOINTMENT_URL + "user";
	public static String GET_APPOINTMENT_ATTACH_URL = APPOINTMENT_URL + "attachment/";
	
	private static User user = null;
	private static SharedPreferences settings = null;
	
	private static AppointmentManager appointmentManager = null;
	
	public static void init(Context context){
		// Make sure that SharedPreferences is initialized only once
		if(settings == null){
			settings = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
		}
	}
	
	public static String getStringPrefs(String key){
		return settings.getString(key, "");
	}
	
	public static boolean putStringPrefs(String key, String value){
		return settings.edit().putString(key, value).commit();
	}
	
	public static boolean clearPrefs(){
		return settings.edit().clear().commit();
	}
	
	/**
	 * User's details includes appointments associated to this user
	 */
	public static void fetchUserDetails(){
		String session_id = getStringPrefs("session_id");
		if(session_id.isEmpty()){ // If no session_id
			user = null;
			return;
		}
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("session_id", session_id);
		
		String responseText = new HttpPostRequest(data).send(USER_URL);
		
		int status = -1;
		try {
			JSONObject response = new JSONObject(responseText);
			
			status = response.getInt("status");
			switch(status){
				
			case 0:
				JSONObject params = response.getJSONObject("message");
				
				int		id = params.getInt("id");
				
				String	email = params.getString("email"),
						fullName = params.getString("full_name"),
						address = params.getString("address"),
						passportNumber = params.getString("passport_number"),
						nationality = params.getString("nationality"),
						dateOfBirth = params.getString("date_of_birth"),
						type = params.getString("type");
				
				char	gender = params.getString("gender").charAt(0);
				
				user = new User(id, email, fullName, address
						, gender, passportNumber, nationality,
						dateOfBirth, type);
				
				// RETRIEVE ALL APPOINTMENTS AND ADD IT TO APPOINTMENTMANAGER
				appointmentManager = new AppointmentManager();
				
				break;
		
		// Immediately clears everything 
		// if status != 0
		// OR
		// an exception is caught
			default:
				// session_id expired
				clearPrefs();
				user = null;
				break;
			}
		// Clear session_id on exception
		} catch (JSONException e) {
			clearPrefs();
			user = null;
		}
	}
	
	public static User getUser() {
		return user;
	}
	
	public static void clearUser() {
		user = null;
	}
	
	public static AppointmentManager getAppointmentManager() {
		return appointmentManager;
	}
	
	public static void clearAppointmentManager() {
		appointmentManager.clearPendingAppointments();
		appointmentManager.clearUpcomingAppointments();
		appointmentManager = null;
	}
}