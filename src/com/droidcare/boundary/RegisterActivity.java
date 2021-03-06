package com.droidcare.boundary;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.droidcare.*;
import com.droidcare.control.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Activity handling user registration.
 * @author Peter
 */

public class RegisterActivity extends Activity {
	/**
	 * Constants defining that the user prefers SMS notification 
	 */
    private static final int SMS_NOTIFICATION = 1;
    
    /**
     * Constants defining that the user prefers Email notification
     */
    private static final int EMAIL_NOTIFICATION = 2;
    
    /**
	 * Stores what is/are the preferred notification type/s in the form of int
	 */
    private int notificationType = 0;
    
    /**
     * Stores what is/are the preferred notification type/s in the form of String
     */
    private String notificationTypeString;
    
    /**
     * A {@link LinearLayout} holding error messages
     */
	private LinearLayout registerMessages;
	
	/**
	 * A date formatter for the user's date of birth
	 */
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    
    /**
     * Handle date fragment creation
     * @param btn	the View firing the event
     */
	public void pickDateBtn (View btn) {
		DialogFragment datePicker = new DatePickerFragment(){
            private View btn;
            public DatePickerFragment init(View btn){
                this.btn = btn;
                return this;
            }

			@Override
			public void onDateSet(DatePicker view
                    , int year, int monthOfYear, int dayOfMonth) {
				((Button) btn).setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
			}
		}.init(btn);
		datePicker.show(getFragmentManager(), "datePicker");
	}
	
	/**
     * Clear all messages held in {@link #registerMessages}
     */
    private void clearMessages() {
        registerMessages.removeAllViews();
    }
    
    /**
     * Add a message to {@link #registerMessages}
     * @param message	the message to be added into {@link #registerMessages}
     */
    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        registerMessages.addView(textView);
    }
    
    /**
     * An event listener handling user registration
     * @param view	the View firing the event
     */
	public void doRegisterUser(View view) {
        clearMessages();
		view.setEnabled(false);

        String  passportNumber = ((EditText) findViewById(R.id.passport_field)).getText().toString(),
                fullName = ((EditText) findViewById(R.id.name_field)).getText().toString(),
                address = ((EditText) findViewById(R.id.address_field)).getText().toString(),
                phoneNumber = ((EditText) findViewById(R.id.phone_number_field)).getText().toString(),
                country = ((Spinner) findViewById(R.id.country_field)).getSelectedItem().toString(),
                email = ((EditText) findViewById(R.id.email_field)).getText().toString(),
                dateOfBirth = ((Button) findViewById(R.id.dob_field)).getText().toString(),
                gender = ((Spinner) findViewById(R.id.gender_field)).getSelectedItem().toString(),
                nationality = ((Spinner) findViewById(R.id.nationality_field)).getSelectedItem().toString(),
                password = ((EditText) findViewById(R.id.password_field)).getText().toString(),
                confirm = ((EditText) findViewById(R.id.confirm_field)).getText().toString();

        int valid = 1;
        if(passportNumber == null || passportNumber.isEmpty()) {
            putMessage("Passport number is empty!");
            valid = 0;
        } 
        
        if (fullName == null || fullName.isEmpty()) {
            putMessage("Full name is empty!");
            valid = 0;
        } 
        
        if (address == null || address.isEmpty()) {
            putMessage("Address field is empty!");
            valid = 0;
        }
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
        	putMessage("Phone number is empty!");
        	valid = 0;
        }
        
        if (country == null || country.isEmpty()) {
            putMessage("Country field is empty!");
            valid = 0;
        } 
        
        if (email == null || email.isEmpty()) {
            putMessage("Email address is empty!");
            valid = 0;
        }
        
        try {
        	// CHECKING DATE OF BIRTH VALIDITY
            Calendar cal = new GregorianCalendar();
            TimeZone t = TimeZone.getTimeZone("GMT+8");
            cal.setTimeZone(t);
            
            Calendar dob = new GregorianCalendar();
            dob.setTimeZone(t);
            dob.setTime(dateOfBirthFormat.parse(dateOfBirth));
            
            if (dob.get(Calendar.YEAR) > cal.get(Calendar.YEAR)) {
            	putMessage("Please select a valid date of birth!");
            	valid = 0;
            } else if (dob.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            	if (dob.get(Calendar.MONTH) > cal.get(Calendar.MONTH)) {
            		putMessage("Please select a valid date of birth!");
            		valid = 0;
            	} else if (dob.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && dob.get(Calendar.DAY_OF_MONTH) >= cal.get(Calendar.DAY_OF_MONTH)) {
            		putMessage("Please select a valid date of birth!");
            		valid = 0;
            	}
            }
        } catch (ParseException e) {
            putMessage("Please select your date of birth!");
            valid = 0;
        }

        if (gender == null || gender.isEmpty()) {
            putMessage("Please select your gender!");
            valid = 0;
        } 
        
        if (nationality == null || nationality.isEmpty()) {
            putMessage("Please select your nationality!");
            valid = 0;
        } 
        
        if (password == null || password.isEmpty()) {
            putMessage("Password field is empty!");
            valid = 0;
        } 
        
        if (confirm == null || confirm.isEmpty()) {
            putMessage("Confirm password field is empty!");
            valid = 0;
        } 
        
        if (password != null && password.length() < RegisterManager.PASSWORD_MINIMUM_LENGTH) {
            putMessage("Password is too short!");
            valid = 0;
        } 
        
        if (password != null && confirm != null && !password.equals(confirm)) {
            putMessage("Password and confirm password mismatch!");
            valid = 0;
        }

        switch(valid) {
            case 0:
                view.setEnabled(true);
                break;

            default:
                ProgressDialog pd = ProgressDialog.show(this, null, "Registering user ...", true);
                
                switch (notificationType) {
                case 0:
                	notificationTypeString = "local";
                	break;
                	
                case 1:
                	notificationTypeString = "sms";
                	break;
                	
                case 2:
                	notificationTypeString = "email";
                	break;
                
                case 3:
                	notificationTypeString = "all";
                	break;
                }
                
                Global.getRegisterManager().registerUser(	passportNumber
                											, fullName
                											, address
                											, phoneNumber
                											, country
                											, email
                											, dateOfBirth
                											, gender
                											, nationality
                											, password
                											, notificationTypeString
                											, new RegisterManager.OnFinishTaskListener() {
                    private View btn;
                    private ProgressDialog pd;

                    public RegisterManager.OnFinishTaskListener init(View btn, ProgressDialog pd) {
                        this.btn = btn;
                        this.pd = pd;
                        return this;
                    }

                    @Override
                    public void onFinishTask(String responseText) {
                        pd.dismiss();
                        btn.setEnabled(true);

                        try {
                            JSONObject response = new JSONObject(responseText);
                            JSONArray messages = response.getJSONArray("message");
                            switch(response.getInt("status")) {
                                case 0:
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                    break;
                                default:
                                    clearMessages();
                                    for(int i = 0, size = messages.length(); i  < size; ++i) {
                                        putMessage(messages.getString(i));
                                    }
                                    break;
                            }
                        // Do nothing on exception
                        } catch (JSONException e) {
                        }

                    }
                }.init(view, pd));
                
                break;
        }
    }
	
	/**
     * An event listener handling which notification mode/s is/are preferred
     * @param v		the view firing the event
     */
	public void onSMSNotificationClick (View v) {
		if (((CheckBox) v).isChecked()) {
			notificationType += SMS_NOTIFICATION;
		} else {
			notificationType -= SMS_NOTIFICATION;
		}
	}
	
	/**
     * An event listener handling which notification mode/s is/are preferred
     * @param v		the view firing the event
     */
	public void onEmailNotificationClick (View v) {
		if (((CheckBox) v).isChecked()) {
			notificationType += EMAIL_NOTIFICATION;
		} else {
			notificationType -= EMAIL_NOTIFICATION;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		registerMessages = (LinearLayout) findViewById(R.id.register_messages);
		clearMessages();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
}
