package com.droidcare;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RegisterActivity extends Activity {
    private static final int SMS_NOTIFICATION = 1, EMAIL_NOTIFICATION = 2;
    
    private int notificationType = 0;
    private String notificationTypeString;
	private LinearLayout registerMessages;
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

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

    private void clearMessages() {
        registerMessages.removeAllViews();
    }

    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        registerMessages.addView(textView);
    }

	public void doRegisterUser(View view) {
        clearMessages();
		view.setEnabled(false);

        String  passportNumber = ((EditText) findViewById(R.id.passport_field)).getText().toString(),
                fullName = ((EditText) findViewById(R.id.name_field)).getText().toString(),
                address = ((EditText) findViewById(R.id.address_field)).getText().toString(),
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
        
        if (email == null || email.isEmpty()) {
            putMessage("Email address is empty!");
            valid = 0;
        }
        
        try {
            dateOfBirthFormat.format(dateOfBirthFormat.parse(dateOfBirth));
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
        
        if (password != null && password.length() < RegisterManager.passwordMinLength) {
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
	
	public void onSMSNotificationClick (View v) {
		if (((CheckBox) v).isChecked()) {
			notificationType += SMS_NOTIFICATION;
		} else {
			notificationType -= SMS_NOTIFICATION;
		}
	}
	
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
