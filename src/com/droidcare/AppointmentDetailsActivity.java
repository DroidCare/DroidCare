package com.droidcare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class AppointmentDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appointment_details_activity);
		
		Bundle data = getIntent().getExtras();
		Appointment appointment = (Appointment) data.getParcelable("appointment");
		
		TextView textview = (TextView) findViewById(R.id.main_textview);
		textview.setText("Appointment ID: " + appointment.getId());

        /*
            Immutable fields
         */
        for(int id: new int[] {R.id.patient_field
                , R.id.consultant_field
                , R.id.dateTime_field
                , R.id.healthIssue_field
                , R.id.remarks_field
                , R.id.appointmentStatus_field}) {
            ((EditText) findViewById(id)).setOnKeyListener(null);
        }

        ((EditText) findViewById(R.id.patient_field)).setText("John Doe");
        ((EditText) findViewById(R.id.consultant_field)).setText("Dr. Neo Cortex");
        ((EditText) findViewById(R.id.dateTime_field)).setText(Global.dateFormat.format(
                new Date(appointment.getDateTimeMillis())));
        ((EditText) findViewById(R.id.healthIssue_field)).setText(appointment.getHealthIssue());
        ((EditText) findViewById(R.id.remarks_field)).setText(appointment.getRemarks());
        ((EditText) findViewById(R.id.appointmentStatus_field)).setText(
                Appointment.getStatus(appointment.getStatus()));
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
}
