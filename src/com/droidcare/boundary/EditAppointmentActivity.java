package com.droidcare.boundary;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import static junit.framework.Assert.assertNotNull;

/**
 * 
 * @author Edwin Candinegara
 * Display user interface for modifying a pending appointment.
 *
 */
public class EditAppointmentActivity extends Activity {
	/**
	 * The {@link Appointment} object to be modified
	 */
	private Appointment appointment;
	
	/**
	 * The selected consultant's ID
	 */
	private int consultantId;
	
	/**
	 * consultantName = the selected consultant's name
	 * date = the selected appointment date
	 * time = the selected appointment time
	 */
	private String consultantName, date, time, originalDate;
	
	/**
	 * A holder for error messages
	 */
	private LinearLayout editAppointmentMessages;
	
	/**
	 * A list of consultant details in the form of {@link ConsultantDetails} objects
	 */
	private ArrayList<ConsultantDetails> consultants;
	
	/**
	 * An event listener for the consultant spinner. This listener updates the current value of {@link #consultantId}
	 * and {@link #consultantName}.
	 */
	private OnItemSelectedListener onConsultantSelectedListener = new OnItemSelectedListener () {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// Get the correct position in ArrayList consultants
			EditAppointmentActivity.this.consultantId = EditAppointmentActivity.this.consultants.get(position).id;
			EditAppointmentActivity.this.consultantName = EditAppointmentActivity.this.consultants.get(position).name;
			
			// Fetch availability time if BOTH DATE AND CONSULTANT'S NAME ARE ALREADY SELECTED (Consultant ID exists!)
			if (!EditAppointmentActivity.this.date.isEmpty() && EditAppointmentActivity.this.consultantId >= 0) {
				EditAppointmentActivity.this.fetchConsultantAvailability();
				((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(true);
				
				// Reset the AppointmentTime spinner selection (to the default one)
				((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setSelection(0);
//				EditAppointmentActivity.this.time = "";
			} else {
				((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(false);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// DO NOTHING
		}
	};
	
	/**
	 * An event listener for the time spinner. This listener updates the current value of {@link #time}.
	 */
	private OnItemSelectedListener onTimeSelectedListener = new OnItemSelectedListener () {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			EditAppointmentActivity.this.time = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// DO NOTHING
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_appointment_activity);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle b = this.getIntent().getExtras();
		this.appointment = b.getParcelable("appointment");
		
		// Initialization
		this.editAppointmentMessages = (LinearLayout) findViewById(R.id.LL_EditAppointmentMessages);
		this.initializeView();
		this.setData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_appointment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	        case android.R.id.home:
	            // back action bar clicked; go back to AppointmentDetails page
	        	this.finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
		//return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Initialize all views in the layout depending on the appointment's type
	 */
	public void initializeView () {
		String appointmentType = this.appointment.getType();
		
		if (appointmentType.equalsIgnoreCase(Appointment.NORMAL)) {
			((LinearLayout) findViewById(R.id.LL_AppointmentReferrerName)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentPreviousId)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentAttachment)).setVisibility(View.GONE);
		} else if (appointmentType.equalsIgnoreCase(Appointment.REFERRAL)) {
			((LinearLayout) findViewById(R.id.LL_AppointmentPreviousId)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentAttachment)).setVisibility(View.GONE);
		} else if (appointmentType.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			((LinearLayout) findViewById(R.id.LL_AppointmentReferrerName)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic)).setVisibility(View.GONE);
		}
		
		((Spinner) findViewById(R.id.Spinner_ConsultantName)).setOnItemSelectedListener(this.onConsultantSelectedListener);
		((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setOnItemSelectedListener(this.onTimeSelectedListener);
	}
	
	/**
	 * Set the initial data of the layout depending on the current {@link #appointment} object 
	 */
	public void setData () {
		String appointmentType = this.appointment.getType();
		this.consultantId = this.appointment.getConsultantId();
		this.consultantName = this.appointment.getConsultantName();
		
		String dateTime = Global.dateFormat.format(new Date(this.appointment.getDateTimeMillis())); 
		originalDate = this.date = dateTime.substring(0, dateTime.indexOf(" "));
		this.time = dateTime.substring(dateTime.indexOf(" ") + 1);
        ((Button) findViewById(R.id.Field_AppointmentDate)).setText(date);
		
		((TextView) findViewById(R.id.Field_AppointmentType)).setText(appointment.getType().substring(0, 1).toUpperCase() 
				  													  + appointment.getType().substring(1));
		((EditText) findViewById(R.id.Field_AppointmentHealthIssue)).setText(this.appointment.getHealthIssue());

		this.fetchConsultantDetails();
//		this.fetchConsultantAvailability();
		
		if (appointmentType.equalsIgnoreCase(Appointment.REFERRAL)) {
			ReferralAppointment r = (ReferralAppointment) this.appointment;
			
			((EditText) findViewById(R.id.Field_AppointmentReferrerName)).setText(r.getReferrerName());
			((EditText) findViewById(R.id.Field_AppointmentReferrerClinic)).setText(r.getReferrerClinic());
		} else if (appointmentType.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			FollowUpAppointment f = (FollowUpAppointment) this.appointment;
			Bitmap imageBitmap = Global.getImageManager().decodeImageBase64(f.getAttachment());

			((ImageView) findViewById(R.id.ImageView_AppointmentAttachment)).setImageBitmap(imageBitmap);
			((TextView) findViewById(R.id.Field_AppointmentPreviousId)).setText("" + f.getPreviousId());
		}
	}
	
	/**
	 * 
	 * @author Edwin Candinegara
	 * Stores relevant information of consultants which are displayed in {@link CreateAppointmentActivity#consultants}.
	 */
	private class ConsultantDetails {
		/**
		 * The consultant's ID
		 */
		private int id;
		
		/**
		 * name = the consultant's name
		 * specialization = the consultant's specialization
		 */
		private String name, specialization;
		
		/**
		 * Constructs a {@link ConsultantDetails} object.
		 * @param id				the consultant's ID
		 * @param name				the consultant's name
		 * @param specialization	the consultant's specialization
		 */
		private ConsultantDetails (int id, String name, String specialization) {
			this.id = id;
			this.name = name;
			this.specialization = specialization;
		}
	}
	
	 /**
     * Fetch the consultant details based on the patient's country
     */
    private void fetchConsultantDetails () {
    	this.consultants = new ArrayList<ConsultantDetails> ();
    	
	    /* -------------------------------------------------------------------------- */

        ProgressDialog pd = ProgressDialog.show(this, null, "Loading list of available consultant in your country...", true);
        new SimpleHttpPost(new Pair<String, String>("location", Global.getUserManager().getUser().getCountry())) {
            private ProgressDialog pd;
            private ArrayList<String> consultantSpinnerData;
            
            public SimpleHttpPost init(ProgressDialog pd) {
                this.pd = pd;
                this.consultantSpinnerData = new ArrayList<String> ();
                return this;
            }
            
            @Override
            public void onFinish(String responseText) {
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray messages = response.getJSONArray("message");
                            
                            for (int i = 0; i < messages.length(); i++) {
                            	JSONObject params = messages.getJSONObject(i);
                            	ConsultantDetails c = new ConsultantDetails(params.getInt("id"),
                            												params.getString("full_name"),
                            												params.getString("specialization"));
                            	
                            	// Default value should be at least the current consultant
                            	if (c.id == EditAppointmentActivity.this.appointment.getConsultantId()) {
                            		EditAppointmentActivity.this.consultants.add(0, c);
                            		this.consultantSpinnerData.add(0, c.name + " - " + c.specialization);
                            	} else {
                            		EditAppointmentActivity.this.consultants.add(c);
                            		this.consultantSpinnerData.add(c.name + " - " + c.specialization);
                            	}
                            }

                            break;
                            
                        default:
                            break;
                    }

                    // Populate Consultant Name Spinner
                    Spinner consultantSpinner = (Spinner) findViewById(R.id.Spinner_ConsultantName);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String> (EditAppointmentActivity.this
                            , android.R.layout.simple_spinner_item, consultantSpinnerData);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    consultantSpinner.setAdapter(adapter);

                // Do nothing on exception
                } catch (JSONException e) {
                }

                pd.dismiss();
            }
        }.init(pd).send(Global.USER_CONSULTANT_URL);
    }
	
    /**
     * Fetch the consultant's time availability based on the selected date and consultant
     */
    private void fetchConsultantAvailability () {
        ProgressDialog pd = ProgressDialog.show(this, null, "Loading consultant's availability...", true);
        String dateString = this.date;
        
        new SimpleHttpPost(){
            private ArrayList<String> timeList;
            private ProgressDialog pd;
            public SimpleHttpPost init(ProgressDialog pd) {
                this.pd = pd;
                this.timeList = new ArrayList<String>();
                return this;
            }
            
            @Override
            public void onFinish(String responseText) {
                if(originalDate.equals(date)) {
                    this.timeList.add(EditAppointmentActivity.this.time);
                }

                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray time = response.getJSONArray("message");
                            
                            for (int i = 0; i < time.length(); i++) {
                            	String t = time.getString(i);
                            	String timeData = t.substring(t.indexOf(" ") + 1);
                            	
                            	// Add only if the time is not the current appointment time
                            	if (!timeData.equalsIgnoreCase(EditAppointmentActivity.this.time)) {
                            		this.timeList.add(timeData);
                            	}
                            }
                            break;
                        default:
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }

                // Populate Appointment Time Spinner
                Spinner timeSpinner = (Spinner) findViewById(R.id.Spinner_AppointmentTime);
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (EditAppointmentActivity.this,
                        android.R.layout.simple_spinner_item, timeList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeSpinner.setAdapter(adapter);

                pd.dismiss();
            }
        }.init(pd).send(Global.APPOINTMENT_TIMESLOT_URL + String.format("/%d/%s", consultantId, dateString));
    }
    
    /**
	 * Clear all views in {@link #editAppointmentMessages}
	 */
	private void clearMessages() {
        this.editAppointmentMessages.removeAllViews();
    }

	/**
	 * Put an error message to {@link #createAppointmentMessages}
	 * @param message	the error message
	 */
    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.editAppointmentMessages.addView(textView);
    }
    
    /**
	 * Create a {@link DatePickerFragment} from which patient can choose his/her desired date
	 * @param v	the clicked button
	 */
    public void onDateButtonClicked (View v) {
		DialogFragment datePicker = new DatePickerFragment () {
            
			private View btn;
			
            public DatePickerFragment init (View v){
                this.btn = v;
                return this;
            }

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				((Button) btn).setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
				EditAppointmentActivity.this.date = ((Button) findViewById(R.id.Field_AppointmentDate)).getText().toString();
				
				// Fetch availability time if BOTH DATE AND CONSULTANT'S NAME ARE ALREADY SELECTED
				if (!EditAppointmentActivity.this.date.isEmpty() && EditAppointmentActivity.this.consultantId >= 0) {
					EditAppointmentActivity.this.fetchConsultantAvailability();
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(true);
					
					// Reset the AppointmentTime spinner selection (to the default one)
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setSelection(0);
//					EditAppointmentActivity.this.time = "";
				} else {
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(false);
				}
			}
			
		}.init(v);
		
		datePicker.show(getFragmentManager(), "datePicker");
	}
    
    /**
	 * Submit request to the back-end server to modify the appointment
	 * @param v	the related View
	 */
    public void onEditAppointment (View v) {
    	// All integers are not empty for sure
    	this.clearMessages();
    	int id = this.appointment.getId(),
    		patientId = this.appointment.getPatientId(), 
    		consultantId = this.consultantId;
    	
    	String dateTime = this.date + " " + this.time,
    		   healthIssue = ((EditText) findViewById(R.id.Field_AppointmentHealthIssue)).getText().toString(),
    		   sessionId = Global.getUserManager().getUser().getSessionId();
    	
    	Date d, timeCheck;
    	
    	int valid = 1;
    	if (healthIssue.isEmpty()) {
    		valid = 0;
    		putMessage("Health issue must not be empty!");
    	}
    	
    	try {
			d = Global.dateFormat.parse(dateTime);
			timeCheck = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
			
			// The appointment must be made at least 1 day in advance
			// If dateTime is still the same as the original appointment date and time, ignore
			if (!dateTime.equalsIgnoreCase(originalDate) && d.before(timeCheck)) {
	    		valid = 0;
	    		putMessage("Appointment date and time must not be before the current time!");
	    	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	if (valid == 1) {
            ProgressDialog pd = ProgressDialog.show(this, null, "Editing appointment...", true);
    		((PatientAppointmentManager) Global.getAppointmentManager()).editAppointment(this.appointment
                    , patientId , consultantId, dateTime, healthIssue
                    , new PatientAppointmentManager.OnFinishListener() {
                private ProgressDialog pd;
                
                public PatientAppointmentManager.OnFinishListener init(ProgressDialog pd){
                    this.pd = pd;
                    return this;
                }
                
                @Override
                public void onFinish(String responseText) {
                    pd.dismiss();
                    EditAppointmentActivity.this.finish();
                }
            }.init(pd));
    	}
    }
}
