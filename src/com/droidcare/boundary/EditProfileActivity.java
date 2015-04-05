package com.droidcare.boundary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import java.util.HashMap;

/**
 * 
 * @author Edwin Candinegara
 * Activity handling edit profile.
 *
 */

public class EditProfileActivity extends Activity {
	/**
	 * Constants defining the user notification preferences.
	 * SMS_NOTIFICATION = user prefers SMS
	 * EMAIL_NOTIFICATION = user prefers Email 
	 */
	private static final int SMS_NOTIFICATION = 1, EMAIL_NOTIFICATION = 2;
	
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
    private LinearLayout updateMessages;
    
    /**
     * A data structure storing nationalities options
     */
    private HashMap<String, Integer> nationalitySpinner;
    
    /**
     * A data structure storing countries options
     */
    private HashMap<String, Integer> countrySpinner;

    /**
     * Clear all messages held in {@link #updateMessages}
     */
    public void clearMessages() {
        updateMessages.removeAllViews();
    }

    /**
     * Add a message to {@link #updateMessages}
     * @param message	the message to be added into {@link #updateMessages}
     */
    public void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateMessages.addView(textView);
    }

    /**
     * An event listener handling the profile update
     * @param view
     */
    public void doUpdateProfile(View view) {
        clearMessages();

        // Mutable
        String  old_pw = ((EditText) findViewById(R.id.old_password_field)).getText().toString(),
                new_pw = ((EditText) findViewById(R.id.password_field)).getText().toString(),
                new_cpw = ((EditText) findViewById(R.id.confirm_field)).getText().toString(),
                passportNumber = ((EditText) findViewById(R.id.passport_field)).getText().toString(),
                address = ((EditText) findViewById(R.id.address_field)).getText().toString(),
                phoneNumber = ((EditText) findViewById(R.id.phone_number_field)).getText().toString(),
                country = ((Spinner) findViewById(R.id.country_field)).getSelectedItem().toString(),
                nationality = ((Spinner) findViewById(R.id.nationality_field)).getSelectedItem().toString(),
                password = old_pw;

        int valid = 1;
        if (old_pw.isEmpty()) {
            valid = 0;
            putMessage("You must provide your old password!");
        }
        
        if (!new_pw.isEmpty() && !new_cpw.isEmpty()) {
            if(!new_pw.equals(new_cpw)) {
                valid = 0;
                putMessage("New password and confirm password mismatch!");
            } else {
                password = new_pw;
            }
        } else if (new_pw.isEmpty() && new_cpw.isEmpty()) {
        	valid = 1;
        } else {
        	if (!new_pw.equals(new_cpw)) {
        		valid = 0;
        		putMessage("New password and confirm password mismatch!");
        	}
        }
        
        if (!new_pw.isEmpty() && new_pw.length() < RegisterManager.PASSWORD_MINIMUM_LENGTH) {
        	valid = 0;
        	putMessage("The password must be at least 6 characters!");
        }
        
        if (passportNumber.isEmpty()) {
            valid = 0;
            putMessage("Passport number must not be empty!");
        }
        
        if (phoneNumber.isEmpty()) {
        	valid = 0;
        	putMessage("Phone number must not be empty!");
        }
        
        if (address.isEmpty()) {
            valid = 0;
            putMessage("Address field must not be empty!");
        } 
        
        if (country.isEmpty()) {
            valid = 0;
            putMessage("Country field must not be empty!");
        } 
        
        if (nationality.isEmpty()) {
            valid = 0;
            putMessage("Nationality field must not be empty!");
        }

        switch(valid) {
            case 0:
                break;
            default:
            	// @pciang: INCLUDE THIS IN YOUR SWITCH (VALID) case 1!!
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
            	
                Log.d("DEBUGGING", "notification = " + notificationTypeString);
                ProgressDialog pd = ProgressDialog.show(this, null, "Updating profile ...", true);

                Global.getUserManager().editProfile(password, address, phoneNumber, country, passportNumber
                        , nationality, notificationTypeString, new UserManager.OnFinishListener() {
                    private ProgressDialog pd;
                    public UserManager.OnFinishListener init(ProgressDialog pd) {
                        this.pd = pd;
                        return this;
                    }

                    @Override
                    public void onFinish(String responseText) {
                        pd.dismiss();

                        try {
                            JSONObject response = new JSONObject(responseText);
                            switch(response.getInt("status")){
                                case 0:
                                    new AlertDialog.Builder(EditProfileActivity.this)
                                            .setNeutralButton(R.string.Button_OK, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    EditProfileActivity.this.finish();
                                                }
                                            })
                                            .setTitle(null)
                                            .setMessage("Profile successfully updated!")
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .show();
                                    break;
                                default:
                                    JSONArray messages = response.getJSONArray("message");
                                    for(int i = 0, size = messages.length(); i < size; ++i) {
                                        putMessage(messages.getString(i));
                                    }
                                    break;
                            }
                        } catch (JSONException e) {
                        }
                    }
                }.init(pd));
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
		setContentView(R.layout.activity_edit_profile);
		
        updateMessages = (LinearLayout) findViewById(R.id.LL_EditProfileMessages);

        nationalitySpinner = new HashMap<String, Integer>();
        nationalitySpinner.put("Indonesian", 1);
        nationalitySpinner.put("Singaporean", 2);
        nationalitySpinner.put("Thailand", 3);
        nationalitySpinner.put("Malaysian", 4);
        nationalitySpinner.put("Vietnamese", 5);

        countrySpinner = new HashMap<String, Integer>();
        countrySpinner.put("Singapore", 1);
        countrySpinner.put("Thailand", 2);
        countrySpinner.put("Malaysia", 3);

        User user = Global.getUserManager().getUser();

        ((EditText) findViewById(R.id.passport_field)).setText(user.getPassportNumber());
        ((TextView) findViewById(R.id.name_field)).setText(user.getFullName());
        ((EditText) findViewById(R.id.address_field)).setText(user.getAddress());
        ((EditText) findViewById(R.id.phone_number_field)).setText(user.getPhoneNumber());
        ((TextView) findViewById(R.id.email_field)).setText(user.getEmail());
        ((TextView) findViewById(R.id.dob_field)).setText(user.getDateOfBirth());
        ((TextView) findViewById(R.id.gender_field)).setText(user.getGender());
        ((Spinner) findViewById(R.id.nationality_field))
                .setSelection(nationalitySpinner.get(user.getNationality()));
        ((Spinner) findViewById(R.id.country_field))
                .setSelection(countrySpinner.get(user.getCountry()));

        String notification = user.getNotification();
        if(notification.equals("all")) {
            notificationType = 3;
        } else if(notification.equals("email")) {
            notificationType = 2;
        } else if(notification.equals("sms")) {
            notificationType = 1;
        } else {
            notificationType = 0;
        }

        ((CheckBox) findViewById(R.id.email_notification_checkbox))
                .setChecked(user.getNotification().equals("all") || user.getNotification().equals("email"));
        ((CheckBox) findViewById(R.id.sms_notification_checkbox))
                .setChecked(user.getNotification().equals("all") || user.getNotification().equals("sms"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
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
