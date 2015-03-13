package com.droidcare.control;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

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
		Bundle data = intent.getExtras();
		Appointment appointment = data.getParcelable("appointment");
		boolean notification = data.getBoolean("notification");
		
		if (appointment != null) {
			if (notification) {
				// Regardless of the user's choice of notification, local notification is still shown
				showLocalNotification(context, appointment);
				
				// Notification based on the user's choice
				// SMS notification is NOT IMPLEMENTED AS FOR NOW
				String notificationType = Global.getUserManager().getUser().getNotification();
				if (notificationType.equalsIgnoreCase("email")) {
					sendEmailNotification(appointment);
				} else if (notificationType.equalsIgnoreCase("sms")) {
					sendSMSNotification(context, appointment);
				} else if (notificationType.equalsIgnoreCase("all")) {
					sendSMSNotification(context, appointment);
					sendEmailNotification(appointment);
				}
			} 
			// ALARM for a just-finished appointment
			else {
				appointment.setStatus(Appointment.FINISHED);
				Global.getAppointmentManager().updateStatusDB(appointment); // UPDATE DATABASE -> IMPLEMENTATION NEEDED!!
				Global.getAppointmentManager().addFinishedAppointment(appointment);
				
				// Double check that status is from ACCEPTED to FINISHED
				if (appointment.getStatus().equalsIgnoreCase(Appointment.ACCEPTED)) {
					Global.getAppointmentManager().removeAcceptedAppointment(appointment);
				}
			}
		} else {
			Log.d("APPOINTMENT NULL", "THE APPOINTMENT IS NOT RETRIEVED PROPERLY! POSSIBLY BECAUSE OF PARCELABLE!");
		}
	}
	
	/**
	 * This method is responsible for showing local notification in the user's Android phone.
	 * 
	 * @param context		this refers to the {@code context} of the method caller (BroadcastReceiver's context)
	 * @param appointment	an {@link Appointment} object whose information will be used in the local notification content.
	 */
	private void  showLocalNotification (Context context, Appointment appointment) {
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
	 * Simulates a SMS notification. This method only SIMULATES an incoming SMS. It does not
	 * use any GSM provider to actually send a SMS notification.
	 * @param context		the context from which the intent will be called
	 * @param appointment	a due {@link Appointment} object
	 */
	private void sendSMSNotification (Context context, Appointment appointment) {
		try {
			String senderNo = "+65 9494 8080";
			String content = "Appointment ID: " + appointment.getId();
			
			byte[] pdu = null;
			byte[] scBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD("0000000000");
			byte[] senderBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(senderNo);
			int lsmcs = scBytes.length;
			
			byte[] dateBytes = new byte[7];
			Calendar calendar = new GregorianCalendar();
			dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
			dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
	        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
	        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
	        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
	        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
	        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
	        
	        ByteArrayOutputStream bo = new ByteArrayOutputStream();
	        bo.write(lsmcs);
	        bo.write(scBytes);
	        bo.write(0x04);
	        bo.write((byte) senderNo.length());
	        bo.write(senderBytes);
	        bo.write(0x00);
	        bo.write(0x00);
	        bo.write(dateBytes);
	        
	        String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
	        Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
	        Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod("stringToGsm7BitPacked", new Class[] { String.class });
	        stringToGsm7BitPacked.setAccessible(true);
	        byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null, content);
	        bo.write(bodybytes);
	        pdu = bo.toByteArray();
	        
	        // Send the message into the inbox and let the usual SMS handling happen - SMS appearing in Inbox, a notification with sound, etc.
	        startSmsReceiverService(context, pdu);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts an intent which simulates incoming SMS.
	 * @param context	the context from which the intent is called
	 * @param pdu		the Protocol Data Unit (PDU) format for SMS
	 */
	private void startSmsReceiverService (Context context, byte[] pdu) {
		Intent intent = new Intent();
		intent.setClassName("com.android.mms", "com.android.mms.transaction.SmsReceiverService");
		intent.setAction("android.provider.Telephony.SMS_RECEIVED");
		intent.putExtra("pdus", new Object[] { pdu });
		intent.putExtra("format", "3gpp");
		context.startService(intent);
	}
	
	/**
	 * Reverses a byte.
	 * @param b	a byte to be reversed
	 * @return  the reversed byte
	 */
	private byte reverseByte(byte b) {
        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
    }
	
	/**
	 * This method is responsible for sending a Email notification to the user's registered email.
	 * @param appointment	an {@link Appointment} object whose information will be used in the Email notification content.
	 */
	private void sendEmailNotification (Appointment appointment) {
		// @pciang : please implement the POST / GET request to the PHP EMAIL HANDLER
	}
}