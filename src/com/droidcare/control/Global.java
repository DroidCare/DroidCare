package com.droidcare.control;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class Global {
	
	/*
	 * Constants for URLs.
	 */
	public static String APP_NAME = "DroidCare";
	public static String BASE_URL = "https://dc-kenrick95.rhcloud.com/";
	public static String USER_URL = BASE_URL + "user/";
	public static String APPOINTMENT_URL = BASE_URL + "appointment/";
	
	public static String USER_LOGIN_URL = USER_URL + "login";
	public static String USER_REGISTER_URL = USER_URL + "register";
	public static String USER_UPDATE_URL = USER_URL + "update";
	public static String USER_LOGOUT_URL = USER_URL + "logout";
    public static String USER_FORGET_URL = USER_URL + "forget";

	public static String USER_APPOINTMENT_URL = APPOINTMENT_URL + "user";
	public static String GET_APPOINTMENT_ATTACH_URL = APPOINTMENT_URL + "attachment/";
	
	/*
	 * Date format used in back-end PHP.
	 */
	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	
	private static AppSession appSession;
	private static UserManager userManager;
	private static AppointmentManager appointmentManager;
    private static LoginManager loginManager;
    private static RegisterManager registerManager;
    private static ImageManager imageManager;
    private static AlarmSetter alarmSetter;
	
	public static void init(Context context) {
		AppSession.init(context);
		
		userManager	= UserManager.getInstance();
		appSession	= AppSession.getInstance();
//		appointmentManager = AppointmentManager.getInstance();
        loginManager = LoginManager.getInstance();
        registerManager = RegisterManager.getInstance();
        imageManager = ImageManager.getInstance();
        alarmSetter = AlarmSetter.getInstance();
	}

    public static void initAppointmentManager() {
        appointmentManager = AppointmentManager.getInstance();
    }
	
	public static AppSession getAppSession() {
		return appSession;
	}
	
	public static UserManager getUserManager() {
		return userManager;
	}
	
	public static AppointmentManager getAppointmentManager() {
		return appointmentManager;
	}

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    public static RegisterManager getRegisterManager() {
        return registerManager;
    }
    
    public static ImageManager getImageManager() {
    	return imageManager;
    }
    
    public static AlarmSetter getAlarmSetter() {
    	return alarmSetter;
    }
}