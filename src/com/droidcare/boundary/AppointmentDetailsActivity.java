package com.droidcare.boundary;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import org.json.JSONException;
import org.json.JSONObject;

public class AppointmentDetailsActivity extends Activity {
	private Appointment appointment;
	private String userType = Global.getUserManager().getUser().getType();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_details);
		
		Bundle data = getIntent().getExtras();
		this.appointment = data.getParcelable("appointment");
		String appointmentType = appointment.getType();

		// Cancel the notification of THIS APPOINTMENT
		// Notification ID is always the same as Appointment ID
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(appointment.getId());
		
		// SETTING UP LAYOUT
		this.configureLayout(appointmentType, 
							 appointment.getStatus(),
							 Global.getUserManager().getUser().getType());
		
		// POPULATING THE APPOINTMENT DATA
		this.setData(appointment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.appointment_detailed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void configureLayout (String appointmentType, String appointmentStatus, String userType) {
		// Showing buttons depending on the user type
		if (this.userType.equalsIgnoreCase("patient")) {
			((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
		} else if (this.userType.equalsIgnoreCase("consultant")) {
			((Button) findViewById(R.id.Button_EditAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_CancelAppointment)).setVisibility(View.GONE);
		}
		
		// Regardless of the type of user, if it is a NON-PENDING appointment, no button is showed
		if (!appointmentStatus.equalsIgnoreCase(Appointment.PENDING)) {
			((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
			
			((Button) findViewById(R.id.Button_EditAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_CancelAppointment)).setVisibility(View.GONE);
		}
		
		// Showing LinearLayout depending on the appointment type
		LinearLayout 	lPreviousId = (LinearLayout) findViewById(R.id.LL_AppointmentPreviousId),
						lReferrerName = (LinearLayout) findViewById(R.id.LL_AppointmentReferrerName),
						lReferrerClinic = (LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic),
						lAttachment = (LinearLayout) findViewById(R.id.LL_AppointmentAttachment);
		
		if (appointmentType.equalsIgnoreCase(Appointment.NORMAL)) {
			lPreviousId.setVisibility(View.GONE);
			lReferrerName.setVisibility(View.GONE);
			lReferrerClinic.setVisibility(View.GONE);
			lAttachment.setVisibility(View.GONE);
		} else if (appointmentType.equalsIgnoreCase(Appointment.REFERRAL)) {
			lPreviousId.setVisibility(View.GONE);
			lAttachment.setVisibility(View.GONE);
		} else if (appointmentType.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			lReferrerName.setVisibility(View.GONE);
			lReferrerClinic.setVisibility(View.GONE);
		}
	}
	
	private void setData (Appointment appointment) {
		Date d = new Date(appointment.getDateTimeMillis());
		String dateTimeString = Global.dateFormat.format(d);
		
		// POPULATE GENERAL DATA
		((TextView) findViewById(R.id.Field_AppointmentId)).setText("" + appointment.getId());
		((TextView) findViewById(R.id.Field_AppointmentStatus)).setText(appointment.getStatus());
		((TextView) findViewById(R.id.Field_AppointmentType)).setText(appointment.getType());
		((TextView) findViewById(R.id.Field_AppointmentConsultantName)).setText(appointment.getConsultantName());
		((TextView) findViewById(R.id.Field_AppointmentDateTime)).setText(dateTimeString);
		((TextView) findViewById(R.id.Field_AppointmentHealthIssue)).setText(appointment.getHealthIssue());
		((TextView) findViewById(R.id.Field_AppointmentRemarks)).setText(appointment.getRemarks());
		
		if (appointment.getType().equalsIgnoreCase(Appointment.REFERRAL)) {
			ReferralAppointment a = (ReferralAppointment) appointment;
			((TextView) findViewById(R.id.Field_AppointmentReferrerName)).setText(a.getReferrerName());
			((TextView) findViewById(R.id.Field_AppointmentReferrerClinic)).setText(a.getReferrerClinic());
		} else if (appointment.getType().equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			FollowUpAppointment a = (FollowUpAppointment) appointment;
			Bitmap attachmentImage = Global.getImageManager().decodeImageBase64(a.getAttachment());
			
			((TextView) findViewById(R.id.Field_AppointmentPreviousId)).setText("" + a.getPreviousId());
			((ImageView) findViewById(R.id.Field_AppointmentAttachment)).setImageBitmap(attachmentImage);
		}
	}
	
	// PATIENT SPECIFIC METHODS!
	public void openEditAppointment (View v) {
		Intent intent = new Intent(this, EditAppointmentActivity.class);
		intent.putExtra("appointmentType", appointment.getType());
		intent.putExtra("appointment", appointment);
		startActivity(intent);
	}

	public void cancelAppointment (View v) {
        ProgressDialog pd = ProgressDialog.show(this, null, "Cancelling appointment ...", true);
        pd.show();
        ((PatientAppointmentManager) Global.getAppointmentManager()).cancelAppointment(appointment
                , new PatientAppointmentManager.OnFinishListener() {
            private ProgressDialog pd;
            public PatientAppointmentManager.OnFinishListener init(ProgressDialog pd) {
                this.pd = pd;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                pd.dismiss();
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch (response.getInt("status")) {
                        case 0:
                            Toast toast = Toast.makeText(AppointmentDetailsActivity.this
                                    , "Appointment cancelled!", Toast.LENGTH_SHORT);
                            toast.show();
//                            AppointmentDetailsActivity.this.finish();
                            AppointmentDetailsActivity.super.onBackPressed();
                            break;
                        default:
                            Toast onFailToast = Toast.makeText(AppointmentDetailsActivity.this
                                    , "Failed to cancel appointment", Toast.LENGTH_LONG);
                            onFailToast.show();
//                            AppointmentDetailsActivity.this.finish();
                            AppointmentDetailsActivity.super.onBackPressed();
                            break;
                    }
                    // Do nothing on exception
                } catch (JSONException e) {
                }
            }
        }.init(pd));
	}

	public void acceptAppointment (View v) {
        ProgressDialog pd = ProgressDialog.show(this, null, "Accepting appointment ...", true);
        pd.show();
		((ConsultantAppointmentManager) Global.getAppointmentManager()).acceptAppointment(appointment
                , new ConsultantAppointmentManager.OnFinishListener() {
            private ProgressDialog pd;
            public ConsultantAppointmentManager.OnFinishListener init(ProgressDialog pd) {
                this.pd = pd;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                pd.dismiss();
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            Toast toast = Toast.makeText(AppointmentDetailsActivity.this
                                    , "Appointment accepted", Toast.LENGTH_SHORT);
                            toast.show();
                            AppointmentDetailsActivity.this.finish();
                            break;
                        default:
                            Toast onFailToast = Toast.makeText(AppointmentDetailsActivity.this
                                    , "Failed to accept appointment", Toast.LENGTH_SHORT);
                            onFailToast.show();
                            AppointmentDetailsActivity.this.finish();
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }
            }
        }.init(pd));
	}
	
	public void rejectAppointment (View v) {
        ProgressDialog pd = ProgressDialog.show(this, null, "Rejecting appointment ...", true);
        pd.show();
        ((ConsultantAppointmentManager) Global.getAppointmentManager()).rejectAppointment(appointment
                , new ConsultantAppointmentManager.OnFinishListener() {
            private ProgressDialog pd;
            public ConsultantAppointmentManager.OnFinishListener init(ProgressDialog pd) {
                this.pd = pd;
                return this;
            }
            @Override
            public void onFinish(String responseText) {
                pd.dismiss();
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            Toast toast = Toast.makeText(AppointmentDetailsActivity.this
                                    , "Appointment rejected", Toast.LENGTH_SHORT);
                            toast.show();
                            AppointmentDetailsActivity.this.finish();
                            break;
                        default:
                            Toast onFailToast = Toast.makeText(AppointmentDetailsActivity.this
                                    , "Failed to reject appointment", Toast.LENGTH_SHORT);
                            onFailToast.show();
                            AppointmentDetailsActivity.this.finish();
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }
            }
        }.init(pd));
	}
}
