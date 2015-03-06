package com.droidcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class AppointmentDetailsActivity extends Activity {
	private Appointment appointment;
	private String userType = Global.getUserManager().getUser().getType();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Show two different layouts depending on the user type
		if (userType.equalsIgnoreCase("patient")) 
			setContentView(R.layout.appointment_details_patient_activity);
		else 
			setContentView(R.layout.appointment_details_consultant_activity);
		
		// Unpack the parcelled Appointment data
		Bundle data = getIntent().getExtras();
		appointment = (Appointment) data.getParcelable("appointment");
		
		TextView textview = (TextView) findViewById(R.id.main_textview);
		textview.setText("Appointment ID: " + appointment.getId());
		
		// PATIENT SPECIFIC
		if (userType.equalsIgnoreCase("patient") && appointment.getStatus() != Appointment.PENDING) {
			Button editButton = (Button) findViewById(R.id.editAppointment_button);
			Button cancelButton = (Button) findViewById(R.id.cancelAppointment_button);
			
			editButton.setVisibility(View.GONE);
			cancelButton.setVisibility(View.GONE);
		}
		
		// CONSULTANT SPECIFIC
		if (userType.equalsIgnoreCase("consultant") && appointment.getStatus() != Appointment.PENDING) {
			Button acceptButton = (Button) findViewById(R.id.acceptAppointment_button);
			Button rejectButton = (Button) findViewById(R.id.rejectAppointment_button);
			
			acceptButton.setVisibility(View.GONE);
			rejectButton.setVisibility(View.GONE);
		}
		
		// TODO: NEED TO ADD THE ATTACHMENT IMAGE HERE! -> use ImageView
        ((TextView) findViewById(R.id.patient_field)).setText(Global.getUserManager().getUser().getFullName());
        ((TextView) findViewById(R.id.consultant_field)).setText("Dr."); // APPOINTMENT DETAILS MUST CONTAIN THIS!
        ((TextView) findViewById(R.id.dateTime_field)).setText(Global.dateFormat.format(new Date(appointment.getDateTimeMillis())));
        ((TextView) findViewById(R.id.healthIssue_field)).setText(appointment.getHealthIssue());
        ((TextView) findViewById(R.id.remarks_field)).setText(appointment.getRemarks());
        ((TextView) findViewById(R.id.appointmentStatus_field)).setText(Appointment.translate(appointment.getStatus()));
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
	
	// PATIENT SPECIFIC METHODS!
	public void openEditAppointment (View v) {
		Intent intent = new Intent(this, EditAppointmentActivity.class);
		intent.putExtra("appointment", appointment);
		startActivity(intent);
	}
	
	public void cancelAppointment (View v) {
		// CALL PATIENT APPOINTMENT MANAGER TO CANCEL THE APPOINTMENT
		((PatientAppointmentManager) Global.getAppointmentManager()).cancelAppointment(this, appointment);
		
		// Go back to the HomeActivity
        finish();
	}
	
	
	// CONSULTANT SPECIFIC METHODS! 
	public void acceptAppointment (View v) {
		((ConsultantAppointmentManager) Global.getAppointmentManager()).acceptAppointment(this, appointment);
		
		// GIVE FEEDBACK HERE
	}
	
	public void rejectAppointment (View v) {
		((ConsultantAppointmentManager) Global.getAppointmentManager()).rejectAppointment(this, appointment);
		
		// GIVE FEEDBACK HERE
	}
}
