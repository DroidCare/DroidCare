package com.droidcare;

import android.content.Context;
import android.content.SharedPreferences;

public class Global {
	public static String APP_NAME = "DroidCare";
	private static User user = null;
	private static SharedPreferences settings;
	
	public static void init(Context context){
		settings = context.getSharedPreferences(APP_NAME, 0);
		
		String session_id = settings.getString("session_id", null);
		
		if(session_id != null){
			
		} else{
			
		}
	}
	
	public static void resume(Context context){
		settings = context.getSharedPreferences(APP_NAME, 0);
	}
	
	public static User getUser(){
		return user;
	}
}