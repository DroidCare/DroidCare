package com.droidcare.control;

import com.droidcare.R;
import com.droidcare.R.drawable;
import com.droidcare.boundary.AppointmentDetailsActivity;
import com.droidcare.entity.Appointment;

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
 * Receives "ALARM" broadcasts for notification purpose (the type of notification
 * depends on the user's choice).
 *
 */

public class AlarmReceiver extends BroadcastReceiver {
	
	/**
	 *  This method definition is overriding the onReceive method in BroadcastReceiver. The method
	 *  defines what to do when an "ALARM" is on. It notifies the user through local notification,
	 *  SMS, or email depending on what the user chooses.
	 */
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
	
	/**
	 * This method is responsible for showing local notification in the user's Android phone.
	 * 
	 * @param context		this refers to the {@code context} of the method caller (BroadcastReceiver's context)
	 * @param appointment	an {@link Appointment} object whose information will be used in the local notification content.
	 */
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
	
	/**
	 * This method is responsible for sending a Email notification to the user's registered email.
	 * @param appointment	an {@link Appointment} object whose information will be used in the Email notification content.
	 */
	public void sendEmailNotification (Appointment appointment) {
		// @pciang : please implement the POST / GET request to the PHP EMAIL HANDLER
	}
}