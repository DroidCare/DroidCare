package com.droidcare.boundary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.droidcare.R;
import com.droidcare.control.AppointmentManager;
import com.droidcare.control.Global;
import com.droidcare.control.PatientAppointmentManager;
import com.droidcare.control.SimpleHttpPost;
import com.droidcare.entity.Appointment;
import com.droidcare.entity.FollowUpAppointment;
import com.droidcare.entity.ReferralAppointment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * 
 * @author Edwin Candinegara
 * Display user interface for creating a new appointment.
 *
 */
public class CreateAppointmentActivity extends Activity {
	/**
	 * Constant definition for image selection for follow-up appointment type
	 */
	private static final int SELECT_PICTURE = 1;
	
	/**
	 * A holder for error messages
	 */
	private LinearLayout createAppointmentMessages;
	
	/**
	 * The selected consultant's ID
	 */
	private int consultantId = -1; // Keep track of the consultant id
	
	/**
	 * consultantName = the selected consultant's name
	 * date = the selected appointment date
	 * time = the selected appointment time
	 * attachmentImageString = the Base 64 encoded attachment image string
	 * type = the appointment's type
	 */
	private String consultantName = "", date = "", time = "", attachmentImageString = "", type = Appointment.NORMAL;
	
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
	
	/**
	 * An event listener for the time spinner. This listener updates the current value of {@link #time}.
	 */
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
		
        createAppointmentMessages = (LinearLayout) findViewById(R.id.LL_CreateAppointmentMessages);
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
	
	/**
	 * Initialize all views in the layout
	 */
	public void initializeView () {
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
	
	/**
	 * Clear all views in {@link #createAppointmentMessages}
	 */
	private void clearMessages() {
        this.createAppointmentMessages.removeAllViews();
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
	 * Put an error message to {@link #createAppointmentMessages}
	 * @param message	the error message
	 */
    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.createAppointmentMessages.addView(textView);
    }
    
    /**
     * Fetch the consultant details based on the patient's country
     */
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
                CreateAppointmentActivity.this.consultants.add(new ConsultantDetails(-1, "", ""));
                consultantSpinnerData.add("");
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
                            	
                            	// Store each Consultant's data -> same order for both array
                            	CreateAppointmentActivity.this.consultants.add(c);
                            	this.consultantSpinnerData.add(c.name + " - " + c.specialization);
                            }

                            break;
                            
                        default:
                            break;
                    }

                    // Populate Consultant Name Spinner
                    Spinner consultantSpinner = (Spinner) findViewById(R.id.Spinner_ConsultantName);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String> (CreateAppointmentActivity.this
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
    	ArrayList<String> timeList = new ArrayList<String> ();

        ProgressDialog pd = ProgressDialog.show(this, null, "Loading consultant's availability...", true);
        // Kasih date donk mas
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
                timeList.add("");
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONArray time = response.getJSONArray("message");
                            
                            for (int i = 0; i < time.length(); i++) {
                            	String t = time.getString(i);
                            	
                            	timeList.add(t.substring(t.indexOf(" ") + 1));
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (CreateAppointmentActivity.this,
                        android.R.layout.simple_spinner_item, timeList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeSpinner.setAdapter(adapter);

                pd.dismiss();
            }
        }.init(pd, timeList).send(Global.APPOINTMENT_TIMESLOT_URL + String.format("/%d/%s"
                , consultantId
                , dateString));
    }
	
	/**
	 * An event listener for the appointment's type radio group
	 * @param v	the corresponding View object
	 */
	public void onAppointmentTypeClicked (View v) {
		boolean checked = ((RadioButton) v).isChecked();

        this.clearMessages();
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
			Log.d("DEBUGGING", "SUM TING WONG!");
			break;
		}
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
	
	/**
	 * Opens a built-in activity from which patient can choose the attachment image
	 * @param v	the related View to this event
	 */
	public void onSelectImage (View v) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
	}
	
	/**
	 * Handle the result from {@link #onSelectImage(View)}
	 */
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
			Uri imageUri = data.getData();
			Bitmap imageBitmap = getThumbnail(imageUri);
			
			if (imageBitmap != null) {
				this.attachmentImageString = Global.getImageManager().encodeImageBase64(imageBitmap);
                Log.d("DEBUGGING", "attach=" + attachmentImageString);
				((ImageView) findViewById(R.id.ImageView_AppointmentAttachment)).setVisibility(View.VISIBLE);
				((ImageView) findViewById(R.id.ImageView_AppointmentAttachment)).setImageBitmap(imageBitmap);
			}

			/*
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
			*/
		}
	}
	
	/* http://stackoverflow.com/questions/3879992/get-bitmap-from-an-uri-android */
	/**
	 * Process the image attachment from {@link #onActivityResult(int, int, Intent)} and compress it
	 * @param uri	the image URI
	 * @return		the compressed image Bitmap 
	 */
	private Bitmap getThumbnail (Uri uri) {
		final int THUMBNAIL_SIZE = 180;
		try {
			InputStream input = this.getContentResolver().openInputStream(uri);
			BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
			
			onlyBoundsOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
			input.close();
			
			if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) return null;
			
			int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ?
								onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
			double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
			
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
	        bitmapOptions.inDither = true;
	        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        
	        input = this.getContentResolver().openInputStream(uri);
	        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
	        input.close();
	        
	        return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Get the nearest power of two of the sample ratio
	 * @param ratio	the current ratio
	 * @return		the nearest power of two
	 */
	private int getPowerOfTwoForSampleRatio (double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0) return 1;
		else return k;
	}
	
	/**
	 * Submit request to the back-end server to create an appointment
	 * @param v	the related View
	 */
	public void onCreateAppointment (View v) {
        clearMessages();

		// USE APPOINTMENT MANAGER TO HANDLE CREATING APPOINTMENT
		// ((PatientAppointmentManager) Global.getAppointmentManager()).createAppointment();

		// Check empty fields first
		int valid = 1;
		
		int patientId = Global.getUserManager().getUser().getId(),
			consultantId = this.consultantId;
		
		String 	patientName = Global.getUserManager().getUser().getFullName(),
				consultantName = this.consultantName,
				dateTime = this.date + " " + this.time,
				healthIssue = ((EditText) findViewById(R.id.Field_AppointmentHealthIssue)).getText().toString(),
				type = this.type,
				sessionId = Global.getUserManager().getUser().getSessionId();
		
		// Validity checking
		if (consultantName.isEmpty()) {
			valid = 0;
            putMessage("Consultant Name must not be empty!");
		}
		
		if (this.date.isEmpty()) {
			valid = 0;
            putMessage("Date must not be empty!");
		}
		
		if (this.time.isEmpty()) {
			valid = 0;
            putMessage("Time must not be empty!");
		}
		
		if (healthIssue.isEmpty()) {
			valid = 0;
            putMessage("Health issue must not be empty!");
		}
		
		if (type.isEmpty()) {
			valid = 0;
		}
		
		// Let the appointment ID be any integer since the appointment list will be re-fetched when the user
		// goes back to the home activity
        String  referrerName = "",
                referrerClinic = "",
                attachment = "",
                prevId = "";
        
        int previousId = -1;
		if (type.equalsIgnoreCase(Appointment.REFERRAL)) {
			referrerName = ((EditText) findViewById(R.id.Field_AppointmentReferrerName)).getText().toString();
			referrerClinic = ((EditText) findViewById(R.id.Field_AppointmentReferrerClinic)).getText().toString();
			
			if (referrerName.isEmpty()) {
				valid = 0;
                putMessage("Referrer\'s name must not be empty!");
			}
			
			if (referrerClinic.isEmpty()) {
				valid = 0;
                putMessage("Referrer\'s clinic must not be empty!");
			}
		} else if (type.equalsIgnoreCase(Appointment.FOLLOW_UP)) {
			attachment = this.attachmentImageString;
			prevId = ((EditText) findViewById(R.id.Field_AppointmentPreviousId)).getText().toString();
			
			if (attachment.isEmpty()) {
				valid = 0;
                putMessage("Attachment must not be empty!");
			}
			
			if (prevId.isEmpty()) {
				valid = 0;
                putMessage("Previous appointment id must not be empty!");
			} else {
				previousId = Integer.parseInt(prevId);
			}
		}
		
		// When the appointment creation is done, go back to HOME ACTIVITY
        if(valid == 1) {
            ProgressDialog pd = ProgressDialog.show(this, null, "Creating appointment...", true);
            ((PatientAppointmentManager) Global.getAppointmentManager()).createAppointment(patientId, consultantId
                    , dateTime, healthIssue, attachment, type, referrerName, referrerClinic
                    , previousId, new PatientAppointmentManager.OnFinishListener() {
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
                                new AlertDialog.Builder(CreateAppointmentActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle(null)
                                        .setMessage("You have successfully created an appointment.")
                                        .setNeutralButton(R.string.Button_OK, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                CreateAppointmentActivity.this.finish();
                                            }
                                        })
                                        .show();
                                return;
                            default:
                                new AlertDialog.Builder(CreateAppointmentActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(null)
                                        .setMessage("Failed to create an appointment!")
                                        .setNeutralButton(R.string.Button_OK, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.init(pd));
        }
	}
}
