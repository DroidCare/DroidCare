package com.droidcare.control;

import java.text.SimpleDateFormat;
import java.util.Locale;
import android.content.Context;

/**
 * Contains global constants used in the app.
 * @author Edwin Candinegara
 */
public class Global {

    /**
     * Application's name.
     */
	public static String APP_NAME = "DroidCare";

    /**
     * Base URL. Contains: <b><u>https://dc-kenrick95.rhcloud.com/</u></b>
     */
	public static String BASE_URL = "https://dc-kenrick95.rhcloud.com/";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/user</u></b>
     */
	public static String USER_URL = BASE_URL + "user/";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/</u></b>
     */
	public static String APPOINTMENT_URL = BASE_URL + "appointment/";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><b><u>https://dc-kenrick95.rhcloud.com/login</u></b></b>
     */
	public static String USER_LOGIN_URL = USER_URL + "login";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/user/register</u></b>
     */
	public static String USER_REGISTER_URL = USER_URL + "register";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/user/update</u></b>
     */
	public static String USER_UPDATE_URL = USER_URL + "update";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/user/logout</u></b>
     */
	public static String USER_LOGOUT_URL = USER_URL + "logout";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/user/forget</u></b>
     */
    public static String USER_FORGET_URL = USER_URL + "forget";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/user/consultant</u></b>
     */
    public static String USER_CONSULTANT_URL = USER_URL + "consultant";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/user</u></b>
     */
	public static String USER_APPOINTMENT_URL = APPOINTMENT_URL + "user";

    /**
     * Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/attachment/</u></b>
     */
	public static String APPOINTMENT_ATTACH_URL = APPOINTMENT_URL + "attachment/";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/cancel</u></b>
     */
    public static String APPOINTMENT_CANCEL_URL = APPOINTMENT_URL + "cancel";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/status</u></b>
     */
    public static String APPOINTMENT_STATUS_URL = APPOINTMENT_URL + "status";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/notify</u></b>
     */
    public static String APPOINTMENT_NOTIFY_URL = APPOINTMENT_URL + "notify";
    
    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/timeslot</u></b>
     */
    public static String APPOINTMENT_TIMESLOT_URL = APPOINTMENT_URL + "timeslot";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/</u></b>
     */
    public static String APPOINTMENT_NEW_URL = APPOINTMENT_URL + "new";

    /**
     * Extension of {@link Global#BASE_URL}. Contains: <b><u>https://dc-kenrick95.rhcloud.com/appointment/edit</u></b>
     */
    public static String APPOINTMENT_EDIT_URL = APPOINTMENT_URL + "edit";

    /**
     * Boolean. Indicates if it is the first time AppointmentManager initialized.
     */
    public static boolean firstInitialization = false;
	
	/**
	 * Date format used in back-end PHP. Used in an instantiation of {@link SimpleDateFormat} variable {@link Global#dateFormat}.
	 */
	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Instantiation of SimpleDateFormat class.
     * Contains the format from {@link Global#DATE_FORMAT}
     */
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	
	/**
	 * An instance of {@link AppSessionManager} which is used globally
	 */
	private static AppSessionManager appSessionManager;
	
	/**
	 * An instance of {@link UserManager} which is used globally
	 */
	private static UserManager userManager;
	
	/**
	 * An instance of {@link AppointmentManager} which is used globally.
	 * This variable may be instantiated to {@link PatientAppointmentManager} or {@link ConsultantAppointmentManager}
	 * depending on the user type.
	 */
	private static AppointmentManager appointmentManager;
	
	/**
	 * An instance of {@link LoginManager} which is used globally
	 */
    private static LoginManager loginManager;
    
    /**
     * An instance of {@link RegisterManager} which is used globally
     */
    private static RegisterManager registerManager;
    
    /**
     * An instance of {@link ImageManager} which is used globally
     */
    private static ImageManager imageManager;
    
    /**
     * An instance of {@link AlarmSetter} which is used globally
     */
    private static AlarmSetter alarmSetter;

    /**
     * Initializes most controller class (except AppointmentManager).
     * This static method should be called once and only once.
     * @param context Application context.
     */
	public static void init(Context context) {
		AppSessionManager.init(context);
        ImageManager.init(context);
		
		userManager	= UserManager.getInstance();
		appSessionManager = AppSessionManager.getInstance();
        loginManager = LoginManager.getInstance();
        registerManager = RegisterManager.getInstance();
        imageManager = ImageManager.getInstance();
        alarmSetter = AlarmSetter.getInstance();
	}

    /**
     * Initializes AppointmentManager controller class. AppointmentManager is not initialized in Global.init().
     */
    public static void initAppointmentManager() {
        appointmentManager = AppointmentManager.getInstance();
    }

   /**
 	* Returns AppSession controller class.
 	* @return AppSession. Application's session (e.g. Login Session ID)
 	*/
	public static AppSessionManager getAppSessionManager() {
		return appSessionManager;
	}
	
    /**
     * Returns UserManager controller class.
     * @return UserManager. User Manager controller class.
     */
	public static UserManager getUserManager() {
		return userManager;
	}

    /**
     * Returns AppointmentManager controller class.
     * @return AppointmentManager. Appointment Manager controller class.
     */
	public static AppointmentManager getAppointmentManager() {
		return appointmentManager;
	}

	/**
	 * Returns LoginManager controller class.
	 * @return LoginManager. Login Manager controller class.
	 */
    public static LoginManager getLoginManager() {
        return loginManager;
    }

    /**
     * Returns RegisterManager controller class.
     * @return RegisterManager. Register Manager controller class.
     */
    public static RegisterManager getRegisterManager() {
        return registerManager;
    }

    /**
     * Returns ImageManager controller class.
     * @return ImageManager. Image Manager controller class.
     */
    public static ImageManager getImageManager() {
    	return imageManager;
    }

    /**
     * Returns AlarmSetter controller class.
     * @return AlarmSetter. Alarm Setter controller class.
     */
    public static AlarmSetter getAlarmSetter() {
    	return alarmSetter;
    }
}
