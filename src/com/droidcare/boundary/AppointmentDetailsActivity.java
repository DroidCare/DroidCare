package com.droidcare.boundary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import static junit.framework.Assert.assertTrue;

public class AppointmentDetailsActivity extends Activity {
	/**
	 * The {@link Appointment} object to be displayed
	 */
	private Appointment appointment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_details);
		
		Bundle data = getIntent().getExtras();
		this.appointment = data.getParcelable("appointment");

		// Cancel the notification of THIS APPOINTMENT
		// Notification ID is always the same as Appointment ID
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(appointment.getId());
		
		// SETTING UP LAYOUT
		this.configureLayout();
		
		// POPULATING THE APPOINTMENT DATA
		this.setData();
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
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Configures the activity layout depending the {@link User} type, the {@link Appointment} status,
	 * and the {@link Appointment} type.
	 * @param appointmentType		the current {@link Appointment} type
	 * @param appointmentStatus	 	the current {@link Appointment} status
	 * @param userType				the {@link User} type
	 */
	private void configureLayout () {
		String userType = Global.getUserManager().getUser().getType();
		
		if (userType.equalsIgnoreCase("patient")) {
			((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentRemarksEditable)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentConsultantName)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.LL_AppointmentPatientName)).setVisibility(View.GONE);
		} else if (userType.equalsIgnoreCase("consultant")) {
			((Button) findViewById(R.id.Button_EditAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_CancelAppointment)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentRemarks)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentConsultantName)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentPatientName)).setVisibility(View.VISIBLE);
		}
		
		// Regardless of the type of user, if it is a NON-PENDING appointment, no button is showed
		if (!this.appointment.getStatus().equalsIgnoreCase(Appointment.PENDING)) {
			((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
			
			((Button) findViewById(R.id.Button_EditAppointment)).setVisibility(View.GONE);
			((Button) findViewById(R.id.Button_CancelAppointment)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.LL_AppointmentRemarks)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.LL_AppointmentRemarksEditable)).setVisibility(View.GONE);
        }
		
		// Showing LinearLayout depending on the appointment type
		LinearLayout 	lPreviousId = (LinearLayout) findViewById(R.id.LL_AppointmentPreviousId),
						lReferrerName = (LinearLayout) findViewById(R.id.LL_AppointmentReferrerName),
						lReferrerClinic = (LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic),
						lAttachment = (LinearLayout) findViewById(R.id.LL_AppointmentAttachment);
		
		if (this.appointment.getType().equalsIgnoreCase(Appointment.NORMAL)) {
			lPreviousId.setVisibility(View.GONE);
			lReferrerName.setVisibility(View.GONE);
			lReferrerClinic.setVisibility(View.GONE);
			lAttachment.setVisibility(View.GONE);
		} else if (this.appointment.getType().equalsIgnoreCase(Appointment.REFERRAL)) {
			lPreviousId.setVisibility(View.GONE);
			lAttachment.setVisibility(View.GONE);
		} else if (this.appointment.getType().equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			lReferrerName.setVisibility(View.GONE);
			lReferrerClinic.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Sets the {@link Appointment} details onto the corresponding layout view.
	 * @param appointment	the {@link Appointment) object to be displayed in details
	 */
	private void setData () {
		Date d = new Date(appointment.getDateTimeMillis());
		String dateTimeString = Global.dateFormat.format(d);
		
		// POPULATE GENERAL DATA
		((TextView) findViewById(R.id.Field_AppointmentId)).setText("" + appointment.getId());
		((TextView) findViewById(R.id.Field_AppointmentStatus)).setText(appointment.getStatus().substring(0, 1).toUpperCase() 
				  													    + appointment.getStatus().substring(1));
		((TextView) findViewById(R.id.Field_AppointmentType)).setText(appointment.getType().substring(0, 1).toUpperCase() 
																	  + appointment.getType().substring(1));
		((TextView) findViewById(R.id.Field_AppointmentConsultantName)).setText(appointment.getConsultantName());
		((TextView) findViewById(R.id.Field_AppointmentPatientName)).setText(appointment.getPatientName());
		((TextView) findViewById(R.id.Field_AppointmentDateTime)).setText(dateTimeString);
		((TextView) findViewById(R.id.Field_AppointmentHealthIssue)).setText(appointment.getHealthIssue());
		((TextView) findViewById(R.id.Field_AppointmentRemarks)).setText(appointment.getRemarks());
		((EditText) findViewById(R.id.Field_AppointmentRemarksEditable)).setText(appointment.getRemarks());
		
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
	
	/**
	 * onClick listener method for Edit Appointment button in the layout.
	 * This method is patient specific method.
	 * @param v	the clicked {@link View} object
	 */
	public void openEditAppointment (View v) {
		Intent intent = new Intent(this, EditAppointmentActivity.class);
		intent.putExtra("appointment", appointment);
		startActivity(intent);
	}

	/**
	 * onClick listener method for Cancel Appointment button in the layout.
	 * This method is patient specific method.
	 * @param v	the clicked {@link View} object
	 */
	public void cancelAppointment (View v) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Cancel Appointment");
		alertBuilder.setMessage("Are you sure?");
		
		// Cancel appointment on CONFIRMATION
		alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Dismiss the AlertDialog box
				dialog.dismiss();
				
				// Cancel the appointment
				ProgressDialog pd = ProgressDialog.show(AppointmentDetailsActivity.this, null, "Cancelling appointment ...", true);
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
		                            AppointmentDetailsActivity.super.onBackPressed();
		                            break;
		                        default:
		                            Toast onFailToast = Toast.makeText(AppointmentDetailsActivity.this
		                                    , "Failed to cancel appointment", Toast.LENGTH_LONG);
		                            onFailToast.show();
		                            AppointmentDetailsActivity.super.onBackPressed();
		                            break;
		                    }
		                    // Do nothing on exception
		                } catch (JSONException e) {
		                }
		            }
		        }.init(pd));
			}
		});
		
		// Go back to the details if NO
		alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		// Pop up the AlertDialog box
		alertBuilder.show();
	}

	/**
	 * onClick listener method for Accept Appointment button in the layout.
	 * This method is consultant specific method.
	 * @param v	the clicked {@link View} object
	 */
	public void acceptAppointment (View v) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Accept Appointment");
		alertBuilder.setMessage("Are you sure?");
		
		alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Dismiss the AlertDialog box
				dialog.dismiss();
				
				// Accept the appointment
				// Set the new remarks
				String remarks = ((EditText) findViewById(R.id.Field_AppointmentRemarksEditable)).getText().toString();
				appointment.setRemarks(remarks);
				
				// Loading progress bar
		        ProgressDialog pd = ProgressDialog.show(AppointmentDetailsActivity.this, null, "Accepting appointment ...", true);
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
                            switch (response.getInt("status")) {
                                case 0:
                                    Toast toast = Toast.makeText(AppointmentDetailsActivity.this
                                            , "Appointment accepted", Toast.LENGTH_SHORT);
                                    toast.show();

                                    // Go back to the details view and update the status and remarks
                                    ((TextView) findViewById(R.id.Field_AppointmentStatus)).setText(appointment.getStatus());
                                    ((TextView) findViewById(R.id.Field_AppointmentRemarks)).setText(appointment.getRemarks());
                                    ((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
                                    ((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
                                    ((LinearLayout) findViewById(R.id.LL_AppointmentRemarksEditable)).setVisibility(View.GONE);
                                    ((LinearLayout) findViewById(R.id.LL_AppointmentRemarks)).setVisibility(View.VISIBLE);

                                    break;
                                default:
                                    Toast onFailToast = Toast.makeText(AppointmentDetailsActivity.this
                                            , "Failed to accept appointment", Toast.LENGTH_SHORT);
                                    onFailToast.show();

                                    // Go back to the details view and update the status and remarks
                                    ((TextView) findViewById(R.id.Field_AppointmentStatus)).setText(appointment.getStatus());
                                    ((TextView) findViewById(R.id.Field_AppointmentRemarks)).setText(appointment.getRemarks());
                                    ((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
                                    ((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
                                    ((LinearLayout) findViewById(R.id.LL_AppointmentRemarksEditable)).setVisibility(View.GONE);
                                    ((LinearLayout) findViewById(R.id.LL_AppointmentRemarks)).setVisibility(View.VISIBLE);

                                    break;
                            }
                            // Do nothing on exception
                        } catch (JSONException e) {
                        }
                    }
                }.init(pd));
			}
		});
		
		// Go back to the details if NO
		alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		// Pop up the AlertDialog box
		alertBuilder.show();
	}
	
	/**
	 * onClick listener method for Reject Appointment button in the layout.
	 * This method is consultant specific method.
	 * @param v	the clicked {@link View} object
	 */
	public void rejectAppointment (View v) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Reject Appointment");
		alertBuilder.setMessage("Are you sure?");
		
		alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Dismiss the AlertDialog box
				dialog.dismiss();
				
				// Set the new remarks
				String remarks = ((EditText) findViewById(R.id.Field_AppointmentRemarksEditable)).getText().toString();
				appointment.setRemarks(remarks);
				
				// Loading progress bar
		        ProgressDialog pd = ProgressDialog.show(AppointmentDetailsActivity.this, null, "Rejecting appointment ...", true);
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
		                            
		                            // Go back to the details view and update the status and remarks
		                    		((TextView) findViewById(R.id.Field_AppointmentStatus)).setText(appointment.getStatus());
		                    		((TextView) findViewById(R.id.Field_AppointmentRemarks)).setText(appointment.getRemarks());
		                    		((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
		                			((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
		                			((LinearLayout) findViewById(R.id.LL_AppointmentRemarksEditable)).setVisibility(View.GONE);
		                			((LinearLayout) findViewById(R.id.LL_AppointmentRemarks)).setVisibility(View.VISIBLE);
		                			
		                            break;
		                        default:
		                            Toast onFailToast = Toast.makeText(AppointmentDetailsActivity.this
		                                    , "Failed to reject appointment", Toast.LENGTH_SHORT);
		                            onFailToast.show();
		                            
		                            // Go back to the details view and update the status and remarks
		                    		((TextView) findViewById(R.id.Field_AppointmentStatus)).setText(appointment.getStatus());
		                    		((TextView) findViewById(R.id.Field_AppointmentRemarks)).setText(appointment.getRemarks());
		                    		((Button) findViewById(R.id.Button_AcceptAppointment)).setVisibility(View.GONE);
		                			((Button) findViewById(R.id.Button_RejectAppointment)).setVisibility(View.GONE);
		                			((LinearLayout) findViewById(R.id.LL_AppointmentRemarksEditable)).setVisibility(View.GONE);
		                			((LinearLayout) findViewById(R.id.LL_AppointmentRemarks)).setVisibility(View.VISIBLE);
		                    		
		                            break;
		                    }
		                // Do nothing on exception
		                } catch (JSONException e) {
		                }
		            }
		        }.init(pd));
			}
		});
		
		// Go back to the details if NO
		alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		// Pop up the AlertDialog box
		alertBuilder.show();
	}
}
