package com.droidcare;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Global {
	public static String APP_NAME = "DroidCare";
	public static String BASE_URL = "https://dc-kenrick95.rhcloud.com";
	public static String USER_URL = BASE_URL + "/user";
	
	public static String USER_LOGIN_URL = USER_URL + "/login";
	public static String USER_REGISTER_URL = USER_URL + "/register";
	public static String USER_UPDATE_URL = USER_URL + "/update";
	public static String USER_LOGOUT_URL = USER_URL + "/logout";
	
	private static User user = null;
	private static SharedPreferences settings = null;
	
	public static void init(Context context){
		// Make sure that SharedPreferences is initialized only once
		if(settings == null){
			settings = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
		}
	}
	
	public static String getStringPrefs(String key){
		return settings.getString(key, null);
	}
	
	public static boolean putStringPrefs(String key, String value){
		return settings.edit().putString(key, value).commit();
	}
	
	public static boolean clearPrefs(){
		return settings.edit().clear().commit();
	}
	
	public static User getUser(){
		if(user != null){
			return user;
		}
		
		String session_id = getStringPrefs("session_id");
		Log.d("session_id", "=" + session_id);
		
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
				
				String	email = params.getString("email"),
						fullName = params.getString("full_name"),
						address = params.getString("address"),
						passportNumber = params.getString("passport_number"),
						nationality = params.getString("nationality"),
						dateOfBirth = params.getString("date_of_birth"),
						type = params.getString("type");
				
				char	gender = params.getString("gender").charAt(0);
				
				user = new User(email, fullName, address
						, gender, passportNumber, nationality,
						dateOfBirth, type);
				
				break;
			default:
				// session_id expired
				clearPrefs();
				user = null;
				break;
			}
		// Do nothing on exception
		} catch (JSONException e) {
			clearPrefs();
			user = null;
		}
		
		return user;
	}
}