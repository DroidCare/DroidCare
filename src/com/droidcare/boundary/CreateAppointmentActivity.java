package com.droidcare.boundary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.droidcare.R;
import com.droidcare.control.Global;
import com.droidcare.control.SimpleHttpPost;
import com.droidcare.entity.Appointment;
import com.droidcare.entity.FollowUpAppointment;
import com.droidcare.entity.ReferralAppointment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateAppointmentActivity extends Activity {
	private static final int SELECT_PICTURE = 1;
	private LinearLayout createAppointmentMessages;
	private int consultantId = -1; // Keep track of the consultant id
	private String consultantName = "", date = "", time = "", attachmentImageString, type = Appointment.NORMAL;
	private ArrayList<ConsultantDetails> consultants;
	
	// Spinner OnItemSelectedListener definition
	private OnItemSelectedListener onConsultantSelectedListener = new OnItemSelectedListener () {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// Get the correct position in ArrayList consultants
			CreateAppointmentActivity.this.consultantId = CreateAppointmentActivity.this.consultants.get(position).id;
			CreateAppointmentActivity.this.consultantName = CreateAppointmentActivity.this.consultants.get(position).name;
			
			// Fetch availability time if BOTH DATE AND CONSULTANT'S NAME ARE ALREADY SELECTED (Consultant ID exists!)
			if (!CreateAppointmentActivity.this.date.isEmpty() && CreateAppointmentActivity.this.consultantId >= 0) {
				CreateAppointmentActivity.this.fetchConsultantAvailability();
				((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(true);
				
				// Reset the AppointmentTime spinner selection (to the default one)
				((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setSelection(0);
				CreateAppointmentActivity.this.time = "";
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
			CreateAppointmentActivity.this.time = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// DO NOTHING
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_appointment);
		
		this.initializeView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_appointment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}
	
	// Set which View object to be shown onCreate
	public void initializeView () {
		this.createAppointmentMessages = (LinearLayout) findViewById(R.id.LL_CreateAppointmentMessages);
		this.clearMessages();
		
		// Configure layout
		((LinearLayout) findViewById(R.id.LL_AppointmentReferrerName)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.LL_AppointmentPreviousId)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.LL_AppointmentAttachment)).setVisibility(View.GONE);
		((ImageView) findViewById(R.id.ImageView_AppointmentAttachment)).setVisibility(View.GONE);
		((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(false);
		
		// Fetch Consultant Details
		this.fetchConsultantDetails();
		
		// Set onItemSelectedListener
		((Spinner) findViewById(R.id.Spinner_ConsultantName)).setOnItemSelectedListener(this.onConsultantSelectedListener);
		((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setOnItemSelectedListener(this.onTimeSelectedListener);
	}
	
	// Debugging purpose
	private void clearMessages() {
        this.createAppointmentMessages.removeAllViews();
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
	
	// Debugging purpose
    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.createAppointmentMessages.addView(textView);
    }
    
    // FETCH CONSULTANT DETAILS WITH THE SAME LOCATION!!
    private void fetchConsultantDetails () {
    	// Push String result to this.consultantDetails with the format:
    	// Key: Consultant Name - Specialization
    	// Value: Consultant ID
    	
    	// PUSH ALL CONSULTANT INFO FROM PHP REQUEST TO THIS ARRAY LIST
    	this.consultants = new ArrayList<ConsultantDetails> ();
    	
    	// Together with the ArrayList above, also push a String -> Consultant Name + " - " + Consultant Specialization
    	// To populate the spinner
    	ArrayList<String> consultantSpinnerData = new ArrayList<String> ();
    	
    	// LOOPING HERE
    	// Add data to those ArrayLists (the order must be the same between the two list)
    	//
    	// Skeleton:
    	// For each JSON entry:
    	//  	int id = JSONArray[i]["id"]
    	//		String name = JSONArray[i]["name"], specialization = JSONArray[i]["specialization"]
    	// 		ConsultantDetails c = new ConsultantDetails(id, name, specialization);
    	//		this.consultants.add(c);
    	//		consultantSpinnerData.add(name + " - " + specialization);
    	
	    /* -------------------------------------------------------------------------- */

        ProgressDialog pd = ProgressDialog.show(this, null, "Loading list of available consultant in your country...", true);
        new SimpleHttpPost(new Pair<String, String>("location", Global.getUserManager().getUser().getCountry())) {
            private ProgressDialog pd;
            private ArrayList<String> consultantSpinnerData;
            public SimpleHttpPost init(ProgressDialog pd, ArrayList<String> consultantSpinnerData) {
                this.pd = pd;
                this.consultantSpinnerData = consultantSpinnerData;
                return this;
            }
            @Override
            public void onFinish(String responseText) {
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray messages = response.getJSONArray("message");

                            // Populate Consultant Name Spinner
                            Spinner consultantSpinner = (Spinner) findViewById(R.id.Spinner_ConsultantName);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String> (CreateAppointmentActivity.this
                                    , android.R.layout.simple_spinner_item, consultantSpinnerData);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            consultantSpinner.setAdapter(adapter);

                            // Continue here

                            break;
                        default:
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }

                pd.dismiss();
            }
        }.init(pd, consultantSpinnerData).send(Global.USER_CONSULTANT_URL);
    }
    
    // FETCH CONSULTANT AVAILABILITY ONLY AFTER THE CONSULTANT AND DATE ARE ALREADY CHOSEN
    private void fetchConsultantAvailability () {
    	// Response returns time string for a given date
    	// Push all time to dateList
    	ArrayList<String> dateList = new ArrayList<String> ();
    	
    	// Populate Appointment Time Spinner
    	Spinner timeSpinner = (Spinner) findViewById(R.id.Spinner_AppointmentTime);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,
    								   dateList);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	timeSpinner.setAdapter(adapter);
    }
	
	// Display the correct layout depending on which appointment type is chosen
	public void onAppointmentTypeClicked (View v) {
		boolean checked = ((RadioButton) v).isChecked();
		
		switch (v.getId()) {
		case R.id.Radio_NormalAppointment:
			if (checked) {
				((LinearLayout) findViewById(R.id.LL_AppointmentReferrerName)).setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic)).setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.LL_AppointmentPreviousId)).setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.LL_AppointmentAttachment)).setVisibility(View.GONE);
				this.type = Appointment.NORMAL;
			}
			
			break;
		
		case R.id.Radio_ReferralAppointment:
			if (checked) {
				((LinearLayout) findViewById(R.id.LL_AppointmentReferrerName)).setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic)).setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.LL_AppointmentPreviousId)).setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.LL_AppointmentAttachment)).setVisibility(View.GONE);
				this.type = Appointment.REFERRAL;
			}
			
			break;
			
		case R.id.Radio_FollowUpAppointment:
			if (checked) {
				((LinearLayout) findViewById(R.id.LL_AppointmentReferrerName)).setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.LL_AppointmentReferrerClinic)).setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.LL_AppointmentPreviousId)).setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.LL_AppointmentAttachment)).setVisibility(View.VISIBLE);
				this.type = Appointment.FOLLOW_UP;
			}
			
			break;
			
		default:
			Log.d("DEBUGGING", "SOMETHING WRONG!");
			break;
		}
	}
	
	// For date fragment
	public void onDateButtonClicked (View v) {
		DialogFragment datePicker = new DatePickerFragment () {
            
			private View btn;
			
            public DatePickerFragment init (View v){
                this.btn = v;
                return this;
            }

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				((Button) btn).setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
				CreateAppointmentActivity.this.date = ((Button) findViewById(R.id.Field_AppointmentDate)).getText().toString();
				
				// Fetch availability time if BOTH DATE AND CONSULTANT'S NAME ARE ALREADY SELECTED
				if (!CreateAppointmentActivity.this.date.isEmpty() && CreateAppointmentActivity.this.consultantId >= 0) {
					CreateAppointmentActivity.this.fetchConsultantAvailability();
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(true);
					
					// Reset the AppointmentTime spinner selection (to the default one)
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setSelection(0);
					CreateAppointmentActivity.this.time = "";
				} else {
					((Spinner) findViewById(R.id.Spinner_AppointmentTime)).setEnabled(false);
				}
			}
			
		}.init(v);
		
		datePicker.show(getFragmentManager(), "datePicker");
	}
	
	// For opening gallery
	public void onSelectImage (View v) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
	}
	
	// For handling the chosen attachment image + showing it to the ImageView
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
			Uri imageUri = data.getData();
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
				this.attachmentImageString = Global.getImageManager().encodeImageBase64(bitmap);
				
				// Set thumbnail
				((ImageView) findViewById(R.id.ImageView_AppointmentAttachment)).setVisibility(View.VISIBLE);
				((ImageView) findViewById(R.id.ImageView_AppointmentAttachment)).setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Submit CREATE request to PHP
	public void onCreateAppointment (View v) {
		// USE APPOINTMENT MANAGER TO HANDLE CREATING APPOINTMENT
		// ((PatientAppointmentManager) Global.getAppointmentManager()).createAppointment();
		
		// Check empty fields first
		
		int patientId = Global.getUserManager().getUser().getId(),
			consultantId = this.consultantId;
		
		String 	patientName = Global.getUserManager().getUser().getFullName(),
				consultantName = this.consultantName,
				dateTime = this.date + " " + this.time,
				healthIssue = ((EditText) findViewById(R.id.Field_AppointmentHealthIssue)).getText().toString(),
				type = this.type,
				sessionId = Global.getUserManager().getUser().getSessionId();
		
		// Let the appointment ID be any integer since the appointment list will be re-fetched when the user
		// goes back to the home activity
		if (type.equalsIgnoreCase(Appointment.NORMAL)) {
			Appointment appointment = new Appointment(-1, patientId, consultantId, dateTime, patientName,
													  consultantName, healthIssue, type, "", Appointment.PENDING);
		} else if (type.equalsIgnoreCase(Appointment.REFERRAL)) {
			String 	referrerName = ((EditText) findViewById(R.id.Field_AppointmentReferrerName)).getText().toString(),
					referrerClinic = ((EditText) findViewById(R.id.Field_AppointmentReferrerClinic)).getText().toString();
			
			ReferralAppointment appointment = new ReferralAppointment(-1, patientId, consultantId, dateTime, patientName,
																	  consultantName, healthIssue, type, "", Appointment.PENDING,
																	  referrerName, referrerClinic);
		} else if (type.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			String attachment = this.attachmentImageString;
			int previousId = Integer.parseInt(((EditText) findViewById(R.id.Field_AppointmentPreviousId)).getText().toString());
			
			FollowUpAppointment appointment = new FollowUpAppointment(-1, patientId, consultantId, dateTime, patientName,
																	  consultantName, healthIssue, type, "", Appointment.PENDING,
																	  attachment, previousId);
		}
		
		// When the appointment creation is done, go back to HOME ACTIVITY
		this.finish();
	}
}
