package com.droidcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfileActivity extends Activity {
	private static final int SMS_NOTIFICATION = 1, EMAIL_NOTIFICATION = 2;
    private int notificationType = 0;
    private String notificationTypeString;
	
    private LinearLayout updateMessages;
    private HashMap<String, Integer> nationalitySpinner;

    public void clearMessages() {
        updateMessages.removeAllViews();
    }

    public void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("\u2022 " + message);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateMessages.addView(textView);
    }

    public void doUpdateProfile(View view) {
        clearMessages();

        // Mutable
        String  old_pw = ((EditText) findViewById(R.id.old_password_field)).getText().toString(),
                new_pw = ((EditText) findViewById(R.id.password_field)).getText().toString(),
                new_cpw = ((EditText) findViewById(R.id.confirm_field)).getText().toString(),
                passportNumber = ((EditText) findViewById(R.id.passport_field)).getText().toString(),
                address = ((EditText) findViewById(R.id.address_field)).getText().toString(),
                nationality = ((Spinner) findViewById(R.id.nationality_field)).getSelectedItem().toString(),
                password = old_pw;

        int valid = 1;
        if (old_pw.isEmpty()) {
            valid = 0;
            putMessage("You must provide your old password!");
        } 
        
        if (!new_pw.isEmpty() && !new_cpw.isEmpty()) {
            if(!new_pw.equals(new_cpw)) {
                Log.d("DEBUGGING", "" + new_pw + ", " + new_cpw);
                valid = 0;
                putMessage("New password and confirm password mismatch!");
            } else {
                password = new_pw;
            }
        } 
        
        if (passportNumber.isEmpty()) {
            valid = 0;
            putMessage("Passport number must not be empty!");
        } 
        
        if (address.isEmpty()) {
            valid = 0;
            putMessage("Address field must not be empty!");
        } 
        
        if (nationality.isEmpty()) {
            valid = 0;
            putMessage("Nationality field must not be empty!");
        }
        
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


        switch(valid) {
            case 0:
                break;
            default:
                Log.d("DEBUGGING", "notification = " + notificationTypeString);
                ProgressDialog pd = ProgressDialog.show(this, null, "Updating profile ...", true);

                Global.getUserManager().editProfile(password, address, passportNumber
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
		setContentView(R.layout.activity_edit_profile);

        updateMessages = (LinearLayout) findViewById(R.id.update_messages);

        nationalitySpinner = new HashMap<String, Integer>();
        nationalitySpinner.put("Indonesian", 1);
        nationalitySpinner.put("Singaporean", 2);
        nationalitySpinner.put("Thailand", 3);
        nationalitySpinner.put("Malaysian", 4);
        nationalitySpinner.put("Vietnamese", 5);

        User user = Global.getUserManager().getUser();

        ((EditText) findViewById(R.id.passport_field)).setText(user.getPassportNumber());
        ((TextView) findViewById(R.id.name_field)).setText(user.getFullName());
        ((EditText) findViewById(R.id.address_field)).setText(user.getAddress());
        ((TextView) findViewById(R.id.email_field)).setText(user.getEmail());
        ((TextView) findViewById(R.id.dob_field)).setText(user.getDateOfBirth());
        ((TextView) findViewById(R.id.gender_field)).setText(user.getGender());
        ((Spinner) findViewById(R.id.nationality_field))
                .setSelection(nationalitySpinner.get(user.getNationality()));

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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
