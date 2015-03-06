package com.droidcare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmSetter {
	private static AlarmSetter instance;
	
	public static AlarmSetter getInstance() {
		instance = new AlarmSetter();
		return instance;
	}
	
	public void setAlarm (Context context, Appointment appointment) {
		// Intent to run the AlarmReceiver when the alarm is on
		Intent intent = new Intent (context, AlarmReceiver.class);
		intent.putExtra("appointment", appointment);
		
		// Make it a PendingIntent because it is going to be run in a later stage
		int alarmId = -appointment.getId(); // notificationId = appointment.getId()
		PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Set when the PendingIntent should be executed
		// Use the same alarmId to overwrite the old one??
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		// Set alarm 1 DAY BEFORE THE APPOINTMENT -> change necessarily
		am.set(AlarmManager.RTC_WAKEUP, appointment.getDateTimeMillis() - 24 * 3600 * 1000, sender);
	}	
}
