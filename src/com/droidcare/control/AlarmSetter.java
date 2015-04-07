<<<<<<< HEAD
package com.droidcare.control;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Sets the notification "ALARM" depending on the date of each appointment.
 * @author Edwin Candinegara
 */

public class AlarmSetter {
	/**
	 * This variable is used to promote singleton design pattern.
	 */
	private static AlarmSetter instance = new AlarmSetter();
	
	/**
	 * Returns {@link #instance}
	 * @return	returns {@link #instance}.
	 */
	public static AlarmSetter getInstance() {
		instance = new AlarmSetter();
		return instance;
	}
	
	/**
	 * This method is responsible for setting an "ALARM" in order to notify the user for upcoming appointments.
	 * The "ALARM" is set 1 day before the accepted {@link Appointment} date and time.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setAcceptedAppointmentAlarm (Context context, Appointment appointment) {
		// Intent to run the AlarmReceiver when the alarm is on
		Intent oneDayNotificationIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		oneDayNotificationIntent.putExtra("appointment", appointment);
		oneDayNotificationIntent.putExtra("notification", true);
		oneDayNotificationIntent.putExtra("acceptedAppointment", true);
		
		Intent setStatusOnFinishIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		setStatusOnFinishIntent.putExtra("appointment", appointment);
		setStatusOnFinishIntent.putExtra("notification", false);
		setStatusOnFinishIntent.putExtra("acceptedAppointment", true);
		
		// Make it a PendingIntent because it is going to be run in a later stage
		int oneDayNotificationId = -appointment.getId();
		int setStatusOnFinishId = appointment.getId();
		
		// Use the same alarmId and the FLAG_CANCEL_CURRENT to overwrite the old PendingIntent
		PendingIntent oneDayNotificationSender = PendingIntent.getBroadcast(context.getApplicationContext(), oneDayNotificationId, oneDayNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		PendingIntent setStatusOnFinish = PendingIntent.getBroadcast(context.getApplicationContext(), setStatusOnFinishId, setStatusOnFinishIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Set when the PendingIntent should be executed
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		// Must check this, if not the notification will be called again and again (behavior of the alarm manager probably)
		if (appointment.getDateTimeMillis() - 24 * 3600 * 1000 >= cal.getTimeInMillis()) {
			// Set alarm 1 DAY BEFORE THE APPOINTMENT -> change necessarily
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() - 24 * 3600 * 1000, oneDayNotificationSender);
		}
		
		// Prevent multiple notification
		if (appointment.getDateTimeMillis() + 1800 * 1000 >= cal.getTimeInMillis()) {
			// Set the appointment to FINISHED after 30 minutes of the appointment time
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() + 1800 * 1000, setStatusOnFinish);
		}
	}

	/**
	 * Reminds consultants to accept or reject a pending appointment 2 days before the appointment date and time.
	 * This method is called by the consultant only.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setPendingAppointmentAlarm(Context context, Appointment appointment) {
		Intent twoDaysNotificationIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		twoDaysNotificationIntent.putExtra("appointment", appointment);
		twoDaysNotificationIntent.putExtra("notification", true);
		twoDaysNotificationIntent.putExtra("acceptedAppointment", false);
		
		// Make it a PendingIntent because it is going to be run in a later stage
		int twoDaysNotificationId = -appointment.getId() - 1000;
		
		// Use the same alarmId and the FLAG_CANCEL_CURRENT to overwrite the old PendingIntent
		PendingIntent twoDaysNotificationSender = PendingIntent.getBroadcast(context.getApplicationContext(), twoDaysNotificationId, twoDaysNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Set when the PendingIntent should be executed
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		if (appointment.getDateTimeMillis() - 2 * 24 * 3600 * 1000 >= cal.getTimeInMillis()) {
			// Set alarm 2 DAYS BEFORE THE APPOINTMENT -> change necessarily
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() - 2 * 24 * 3600 * 1000, twoDaysNotificationSender);
		}
	}
	
	/**
	 * Sets the appointment status to FINISHED if the appointment date and time has passed and it is neither
	 * accepted nor rejected by the consultant.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setMissedPendingAppointmentAlarm (Context context, Appointment appointment) {
		Intent setStatusOnFinishIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		setStatusOnFinishIntent.putExtra("appointment", appointment);
		setStatusOnFinishIntent.putExtra("notification", false);
		setStatusOnFinishIntent.putExtra("acceptedAppointment", false);
		
		int setStatusId = appointment.getId() + 1000;
		PendingIntent setStatusOnFinish = PendingIntent.getBroadcast(context.getApplicationContext(), setStatusId, setStatusOnFinishIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		if (appointment.getDateTimeMillis() + 1000 >= cal.getTimeInMillis()) {
			// Set when the PendingIntent should be executed
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() + 1000, setStatusOnFinish);
		}
	}
}
=======
package com.droidcare.control;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.droidcare.entity.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author Edwin Candinegara
 * Sets the notification "ALARM" depending on the date of each appointment.
 */

public class AlarmSetter {
	/**
	 * This variable is used to promote singleton design pattern.
	 */
	private static AlarmSetter instance = new AlarmSetter();
	
	/**
	 * Returns {@link #instance}
	 * @return	returns {@link #instance}.
	 */
	public static AlarmSetter getInstance() {
		instance = new AlarmSetter();
		return instance;
	}
	
	/**
	 * This method is responsible for setting an "ALARM" in order to notify the user for upcoming appointments.
	 * The "ALARM" is set 1 day before the accepted {@link Appointment} date and time.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setAcceptedAppointmentAlarm (Context context, Appointment appointment) {
		// Intent to run the AlarmReceiver when the alarm is on
		Intent oneDayNotificationIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		oneDayNotificationIntent.putExtra("appointment", appointment);
		oneDayNotificationIntent.putExtra("notification", true);
		oneDayNotificationIntent.putExtra("acceptedAppointment", true);
		
		Intent setStatusOnFinishIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		setStatusOnFinishIntent.putExtra("appointment", appointment);
		setStatusOnFinishIntent.putExtra("notification", false);
		setStatusOnFinishIntent.putExtra("acceptedAppointment", true);
		
		// Make it a PendingIntent because it is going to be run in a later stage
		int oneDayNotificationId = -appointment.getId();
		int setStatusOnFinishId = appointment.getId();
		
		// Use the same alarmId and the FLAG_CANCEL_CURRENT to overwrite the old PendingIntent
		PendingIntent oneDayNotificationSender = PendingIntent.getBroadcast(context.getApplicationContext(), oneDayNotificationId, oneDayNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		PendingIntent setStatusOnFinish = PendingIntent.getBroadcast(context.getApplicationContext(), setStatusOnFinishId, setStatusOnFinishIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Set when the PendingIntent should be executed
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		// Must check this, if not the notification will be called again and again (behavior of the alarm manager probably)
		if (appointment.getDateTimeMillis() - 24 * 3600 * 1000 >= cal.getTimeInMillis()) {
			// Set alarm 1 DAY BEFORE THE APPOINTMENT -> change necessarily
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() - 24 * 3600 * 1000, oneDayNotificationSender);
		}
		
		// Prevent multiple notification
		if (appointment.getDateTimeMillis() + 1800 * 1000 >= cal.getTimeInMillis()) {
			// Set the appointment to FINISHED after 30 minutes of the appointment time
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() + 1800 * 1000, setStatusOnFinish);
		}
	}

	/**
	 * Reminds consultants to accept or reject a pending appointment 2 days before the appointment date and time.
	 * This method is called by the consultant only.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setPendingAppointmentAlarm(Context context, Appointment appointment) {
		Intent twoDaysNotificationIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		twoDaysNotificationIntent.putExtra("appointment", appointment);
		twoDaysNotificationIntent.putExtra("notification", true);
		twoDaysNotificationIntent.putExtra("acceptedAppointment", false);
		
		// Make it a PendingIntent because it is going to be run in a later stage
		int twoDaysNotificationId = -appointment.getId() - 1000;
		
		// Use the same alarmId and the FLAG_CANCEL_CURRENT to overwrite the old PendingIntent
		PendingIntent twoDaysNotificationSender = PendingIntent.getBroadcast(context.getApplicationContext(), twoDaysNotificationId, twoDaysNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Set when the PendingIntent should be executed
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		if (appointment.getDateTimeMillis() - 2 * 24 * 3600 * 1000 >= cal.getTimeInMillis()) {
			// Set alarm 2 DAYS BEFORE THE APPOINTMENT -> change necessarily
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() - 2 * 24 * 3600 * 1000, twoDaysNotificationSender);
		}
	}
	
	/**
	 * Sets the appointment status to FINISHED if the appointment date and time has passed and it is neither
	 * accepted nor rejected by the consultant.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setMissedPendingAppointmentAlarm (Context context, Appointment appointment) {
		Intent setStatusOnFinishIntent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		setStatusOnFinishIntent.putExtra("appointment", appointment);
		setStatusOnFinishIntent.putExtra("notification", false);
		setStatusOnFinishIntent.putExtra("acceptedAppointment", false);
		
		int setStatusId = appointment.getId() + 1000;
		PendingIntent setStatusOnFinish = PendingIntent.getBroadcast(context.getApplicationContext(), setStatusId, setStatusOnFinishIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		
		if (appointment.getDateTimeMillis() + 1000 >= cal.getTimeInMillis()) {
			// Set when the PendingIntent should be executed
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() + 1000, setStatusOnFinish);
		}
	}
}
>>>>>>> branch 'master' of git@github.com:DroidCare/DroidCare.git
