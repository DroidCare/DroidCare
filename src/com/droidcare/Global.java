package com.droidcare;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class Global {
	public static String APP_NAME = "DroidCare";
	public static String BASE_URL = "https://dc-kenrick95.rhcloud.com";
	public static String USER_URL = BASE_URL + "/user";
	
	public static String USER_LOGIN_URL = USER_URL + "/login";
	public static String USER_REGISTER_URL = USER_URL + "/register";
	public static String USER_UPDATE_URL = USER_URL + "/update";
	public static String USER_LOGOUT_URL = USER_URL + "/logout";
	
	
	private static User user = null;
	private static SharedPreferences settings;
	
	public static void init(Context context){
		settings = context.getSharedPreferences(APP_NAME, 0);
		
		String session_id = settings.getString("session_id", null);
		
		// If there is no session_id stored in the device
		if(session_id == null){
			return;
		}
		
		// Else,
		// fetch user's data with existing session_id
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("session_id", session_id);
		
		String responseText = new HttpPostRequest(data).send(Global.USER_URL);
		
		int status = -1;
		try {
			JSONObject response = new JSONObject(responseText);
			
			status = response.getInt("status");
			
			switch(status){
			
			case 0:
				JSONObject params = response.getJSONObject("message");
				String	email = params.getString("email"),
						full_name = params.getString("full_name"),
						address = params.getString("address"),
						passport_number = params.getString("passport_number"),
						nationality = params.getString("nationality"),
						// Unsure about DOB data type, supposedly String
						date_of_birth = params.getString("date_of_birth"),
						type = params.getString("type");
				
				char	gender = params.getString("gender").charAt(0);
				
				user = new User(email, full_name, address
						, gender, passport_number, nationality
						, date_of_birth, type);
				
				break;
			default:
				SharedPreferences.Editor editor = settings.edit();
				
				// Session expired
				// and ignore error messages
				editor.clear();
				user = null;
				
				editor.commit();
				break;
			}
		} catch (JSONException e) {
		}
	}
	
	public static User getUser(){
		return user;
	}
}