package com.droidcare.control;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
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
	 * The "ALARM" is set 1 day before the {@link Appointment} date and time.
	 * @param context		{@link Context} object from which this method is called
	 * @param appointment	an {@link Appointment} object which will be passed to the {@link PendingIntent} object for the "ALARM"
	 */
	public void setAlarm (Context context, Appointment appointment) {
		// Intent to run the AlarmReceiver when the alarm is on
		Intent intent = new Intent (context.getApplicationContext(), AlarmReceiver.class);
		intent.putExtra("appointment", appointment);
		
		// Make it a PendingIntent because it is going to be run in a later stage
		int alarmId = -appointment.getId();
		
		// Use the same alarmId and the FLAG_CANCEL_CURRENT to overwrite the old PendingIntent
		PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Set when the PendingIntent should be executed
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		// Set alarm 1 DAY BEFORE THE APPOINTMENT -> change necessarily
		am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() - 24 * 3600 * 1000, sender);
	}	
}
