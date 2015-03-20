package com.droidcare.boundary;

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

public class EditAppointmentActivity extends Activity {
	private Appointment appointment;
	private int consultantId = -1;
	private String consultantName, date, time; 
	private LinearLayout editAppointmentMessages;
	private ArrayList<ConsultantDetails> consultants;
	
	// Spinner OnItemSelectedListener definition
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
				EditAppointmentActivity.this.time = "";
			} else {
				((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(false);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// DO NOTHING
		}
	};
	
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
		
		return super.onOptionsItemSelected(item);
	}
	
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

	public void setData () {
		String appointmentType = this.appointment.getType();
		this.consultantId = this.appointment.getConsultantId();
		this.consultantName = this.appointment.getConsultantName();
		
		String dateTime = Global.dateFormat.format(new Date(this.appointment.getDateTimeMillis())); 
		this.date = dateTime.substring(0, dateTime.indexOf(" "));
		this.time = dateTime.substring(dateTime.indexOf(" ") + 1);
		
		((TextView) findViewById(R.id.Field_AppointmentType)).setText(appointmentType);
		((EditText) findViewById(R.id.Field_AppointmentHealthIssue)).setText(this.appointment.getHealthIssue());
		
		this.fetchConsultantDetails();
		this.fetchConsultantAvailability();
		
		if (appointmentType.equalsIgnoreCase(Appointment.REFERRAL)) {
			ReferralAppointment r = (ReferralAppointment) this.appointment;
			
			((EditText) findViewById(R.id.Field_AppointmentReferrerName)).setText(r.getReferrerName());
			((EditText) findViewById(R.id.Field_AppointmentReferrerClinic)).setText(r.getReferrerClinic());
		} else if (appointmentType.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			FollowUpAppointment f = (FollowUpAppointment) this.appointment;
			Bitmap imageBitmap = Global.getImageManager().decodeImageBase64(f.getAttachment());
			
			((ImageView) findViewById(R.id.Field_AppointmentAttachment)).setImageBitmap(imageBitmap);
			((EditText) findViewById(R.id.Field_AppointmentPreviousId)).setText(f.getPreviousId());
		}
	}
	
	/* PRIVATE CLASS */
	private class ConsultantDetails {
		private int id;
		private String name, specialization;
		
		private ConsultantDetails (int id, String name, String specialization) {
			this.id = id;
			this.name = name;
			this.specialization = specialization;
		}
	}
	
	// FETCH CONSULTANT DETAILS WITH THE SAME LOCATION!!
    private void fetchConsultantDetails () {
    	// PUSH ALL CONSULTANT INFO FROM PHP REQUEST TO THIS ARRAY LIST
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
	
    private void fetchConsultantAvailability () {
    	ArrayList<String> timeList = new ArrayList<String> ();
        ProgressDialog pd = ProgressDialog.show(this, null, "Loading consultant's availability...", true);
        String dateString = this.date;
        
        new SimpleHttpPost(){
            private ArrayList<String> timeList;
            private ProgressDialog pd;
            public SimpleHttpPost init(ProgressDialog pd, ArrayList<String> timeList) {
                this.pd = pd;
                this.timeList = timeList;
                return this;
            }
            
            @Override
            public void onFinish(String responseText) {
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray time = response.getJSONArray("message");
                            
                            for (int i = 0; i < time.length(); i++) {
                            	String t = time.getString(i);
                            	
                            	if (t.substring(t.indexOf(" ") + 1)
                            		 .equalsIgnoreCase(EditAppointmentActivity.this.time)) {
                            		timeList.add(EditAppointmentActivity.this.time);
                            	} else {
                            		timeList.add(t.substring(t.indexOf(" ") + 1));
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
        }.init(pd, timeList).send(Global.APPOINTMENT_TIMESLOT_URL + String.format("/%d/%s"
                , consultantId
                , dateString));
    }
    
	// Debugging purpose
	private void clearMessages() {
        this.editAppointmentMessages.removeAllViews();
    }

	// Debugging purpose
    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.editAppointmentMessages.addView(textView);
    }

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
					EditAppointmentActivity.this.time = "";
				} else {
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(false);
				}
			}
			
		}.init(v);
		
		datePicker.show(getFragmentManager(), "datePicker");
	}

    public void onEditAppointment (View v) {
    	// NEED IMPLEMENTATION
    }
}
