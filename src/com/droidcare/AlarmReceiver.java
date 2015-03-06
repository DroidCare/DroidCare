package com.droidcare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This class is basically run when a certain "ALARM" is on for notification purpose (the type of notification
 * depends on the user's choice) 
 *
 */

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive (Context context, Intent intent) {
		Appointment appointment = (Appointment) intent.getExtras().getParcelable("appointment");
		
		// Regardless of the user's choice of notification, local notification is still shown
		showLocalNotification(context, appointment);
		
		// Notification based on the user's choice
		// SMS notification is NOT IMPLEMENTED AS FOR NOW
		String notificationType = Global.getUserManager().getUser().getNotification();
		if (notificationType.equalsIgnoreCase("email")) {
			sendEmailNotification(appointment);
		} else if (notificationType.equalsIgnoreCase("sms")) {
			// DO NOTHING
		} else if (notificationType.equalsIgnoreCase("all")) {
			// DO EMAIL ONLY
			sendEmailNotification(appointment);
		}
	}
	
	public void  showLocalNotification (Context context, Appointment appointment) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder mBuilder = new Notification.Builder(context);
		int notificationId = appointment.getId();
		
		// Open the AppointmentDetailedView when the notification is pressed
		Intent intent = new Intent (context, AppointmentDetailsActivity.class);
		intent.putExtra("appointment", appointment);
		PendingIntent pIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Create notification
		// PLEASE CHANGE THE CONTENT AND LAYOUT OF THE NOTIFICATION
		// NOTE: WITHOUT icon, the notification will not be shown
		mBuilder.setContentTitle("APPOINTMENT NOTIFICATION")
				.setContentText("APPOINTMENT DETAILS HERE!")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntent);
		
		notificationManager.notify(notificationId, mBuilder.build());
	}
	
	public void sendEmailNotification (Appointment appointment) {
		// @pciang : please implement the POST / GET request to the PHP EMAIL HANDLER
	}
}