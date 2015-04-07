package com.droidcare.control;

import java.util.Date;

import com.droidcare.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Receives "ALARM" broadcasts for notification purpose (the type of notification
 * depends on the user's choice).
 * @author Edwin Candinegara
 */

public class AlarmReceiver extends BroadcastReceiver {
	/**
     * Interface used to allow the {@link SimpleHttpPost}
     * to run some code when it has finished executing.
     */
    public interface OnFinishListener {
        public abstract void onFinish(String responseText);
    }

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
		boolean acceptedAppointment = data.getBoolean("acceptedAppointment");
		
		if (appointment != null) {
			// For Accepted Appointment alarm
			if (acceptedAppointment) {
				if (notification) {
					// Regardless of the user's choice of notification, local notification is still shown
					showLocalNotification(context, appointment, "Upcoming Appointment:\n" 
							 									 + appointment.getHealthIssue() + " - "
							 									 + appointment.getConsultantName() + "\n" 
							 									 + "Tomorrow, "
							 									 + Global.dateFormat.format(new Date(appointment.getDateTimeMillis())));
					
					String notificationType = Global.getUserManager().getUser().getNotification();
					if (notificationType.equalsIgnoreCase("email")) {
						sendEmailNotification(appointment, new OnFinishListener() {
	                        @Override
	                        public void onFinish(String responseText) {
	                            // Do nothing
	                        }
	                    });
					} else if (notificationType.equalsIgnoreCase("sms")) {
						sendSMSNotification(context, appointment, "Dear " + appointment.getPatientName() + "\n\n"
								  								   + "You have an appointment tomorrow, " 
								  								   + Global.dateFormat.format(new Date(appointment.getDateTimeMillis()))
								  								   + " with " + appointment.getConsultantName() + ".\n"
								  								   + "Your health issue: " + appointment.getHealthIssue() + ".\n");
					} else if (notificationType.equalsIgnoreCase("all")) {
						sendSMSNotification(context, appointment, "Dear " + appointment.getPatientName() + "\n\n"
																		  + "You have an appointment tomorrow, " 
																		  + Global.dateFormat.format(new Date(appointment.getDateTimeMillis()))
																		  + " with " + appointment.getConsultantName() + ".\n"
																		  + "Your health issue: " + appointment.getHealthIssue() + ".\n");
						
						sendEmailNotification(appointment, new OnFinishListener() {
	                        @Override
	                        public void onFinish(String responseText) {
	                            // Do nothing
	                        }
	                    });
					}
				} else {
					// UPDATE STATUS IN DATABASE
	                new SimpleHttpPost(new Pair<String, String>("id", "" + appointment.getId())
	                        , new Pair<String, String>("status", Appointment.FINISHED)
	                        , new Pair<String, String>("remarks", appointment.getRemarks())
	                        , new Pair<String, String>("session_id", Global.getUserManager().getUser().getSessionId())) {
	                    private Appointment appointment;

	                    public SimpleHttpPost init(Appointment appointment) {
	                        this.appointment = appointment;
	                        return this;
	                    }

	                    @Override
	                    public void onFinish(String responseText) {
	                        try {
	                            JSONObject response = new JSONObject(responseText);
	                            switch(response.getInt("status")) {
	                                case 0:
	                                    appointment.setStatus(Appointment.FINISHED);
	                                    Global.getAppointmentManager().addFinishedAppointment(appointment);
	                                    Global.getAppointmentManager().removeAcceptedAppointment(appointment);
	                                    break;
	                                default:
	                                    break;
	                            }
	                        // Do nothing on exception
	                        } catch (JSONException e) {
	                        }
	                    }
	                }.init(appointment).send(Global.APPOINTMENT_STATUS_URL);
				}
			} 
			
			// For Pending Appointment alarm
			else {
				Log.d("PENDING APPOINTMENT ALARM", "INSIDE");
				if (notification) {
					// Regardless of the user's choice of notification, local notification is still shown
					showLocalNotification(context, appointment, "You have a pending appointment which is due in 2 days, " 
							 									 + Global.dateFormat.format(new Date(appointment.getDateTimeMillis()))
							 									 + ". Please respond to the appointment request as soon as possible.");
					
					String notificationType = Global.getUserManager().getUser().getNotification();
					if (notificationType.equalsIgnoreCase("email")) {
						sendEmailNotification(appointment, new OnFinishListener() {
	                        @Override
	                        public void onFinish(String responseText) {
	                            // Do nothing
	                        }
	                    });
					} else if (notificationType.equalsIgnoreCase("sms")) {
						sendSMSNotification(context, appointment, "Dear " + appointment.getConsultantName() + "\n\n"
								  								   + "You have a pending appointment which is due in 2 days, " 
								  								   + Global.dateFormat.format(new Date(appointment.getDateTimeMillis()))
								  								   + " with " + appointment.getPatientName() + ".\n"
								  								   + "Please respond to the appointment request as soon as possible.");
					} else if (notificationType.equalsIgnoreCase("all")) {
						sendSMSNotification(context, appointment, "Dear " + appointment.getConsultantName() + "\n\n"
								   								   + "You have a pending appointment which is due in 2 days, " 
								   								   + Global.dateFormat.format(new Date(appointment.getDateTimeMillis()))
								   								   + " with " + appointment.getPatientName() + ".\n"
								   								   + "Please respond to the appointment request as soon as possible.");
						
						sendEmailNotification(appointment, new OnFinishListener() {
	                        @Override
	                        public void onFinish(String responseText) {
	                            // Do nothing
	                        }
	                    });
					}
				} else {
					// UPDATE STATUS IN DATABASE
	                new SimpleHttpPost(new Pair<String, String>("id", "" + appointment.getId())
	                        , new Pair<String, String>("status", Appointment.FINISHED)
	                        , new Pair<String, String>("remarks", appointment.getRemarks())
	                        , new Pair<String, String>("session_id", Global.getUserManager().getUser().getSessionId())) {
	                    private Appointment appointment;

	                    public SimpleHttpPost init(Appointment appointment) {
	                        this.appointment = appointment;
	                        return this;
	                    }

	                    @Override
	                    public void onFinish(String responseText) {
	                        try {
	                            JSONObject response = new JSONObject(responseText);
	                            switch(response.getInt("status")) {
	                                case 0:
	                                    appointment.setStatus(Appointment.FINISHED);
	                                    Global.getAppointmentManager().addFinishedAppointment(appointment);
	                                    Global.getAppointmentManager().removePendingAppointment(appointment);
	                                    Log.d("REMOVE PENDING TO FINISH", "REMOVED");
	                                    
	                                    break;
	                                default:
	                                	Log.d("REMOVE PENDING TO FINISH", "FAILED");
	                                    break;
	                            }
	                        // Do nothing on exception
	                        } catch (JSONException e) {
	                        }
	                    }
	                }.init(appointment).send(Global.APPOINTMENT_STATUS_URL);
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
	private void  showLocalNotification (Context context, Appointment appointment, String content) {
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
		mBuilder.setContentTitle("DroidCare Appointment Reminder")
				.setContentText(content)
				.setSmallIcon(R.drawable.ic_logo)
				.setContentIntent(pIntent);
		
		notificationManager.notify(notificationId, mBuilder.build());
	}
	
	/**
	 * Simulates a SMS notification. This method only SIMULATES an incoming SMS. It does not
	 * use any GSM provider to actually send a SMS notification.
	 * @param context		the context from which the intent will be called
	 * @param appointment	a due {@link Appointment} object
	 */
	private void sendSMSNotification (Context context, Appointment appointment, String content) {
			String senderNo = "DroidCare Notification";
			
			if (!appointment.getRemarks().isEmpty()) {
				content += "Appointment remarks: " + appointment.getRemarks() + "/n";
			}
			
			content += "Call (+65) 9545 1111 if you need further assistance. Thank you.";
			
			// Simulate SMS
			// http://stackoverflow.com/questions/642076/how-to-save-sms-to-inbox-in-android/872131#872131
			ContentValues values = new ContentValues();
			values.put("address", "(+65) 9545 1111");
			values.put("body", content);
			context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}
	
	/**
	 * This method is responsible for sending a Email notification to the user's registered email.
	 * @param appointment	an {@link Appointment} object whose information will be used in the Email notification content.
	 */
	private void sendEmailNotification (Appointment appointment, OnFinishListener onFinishListener) {
        new SimpleHttpPost( new Pair<String, String>("id", "" + appointment.getId())
                			, new Pair<String, String>("session_id", Global.getAppSessionManager().retrieveSessionId())) {
            private OnFinishListener listener;
            public SimpleHttpPost init(OnFinishListener listener) {
                this.listener = listener;
                return this;
            }
            @Override
            public void onFinish(String responseText) {
                listener.onFinish(responseText);
            }
        }.init(onFinishListener).send(Global.APPOINTMENT_NOTIFY_URL);
	}
}