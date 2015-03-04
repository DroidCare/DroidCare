package com.droidcare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfileActivity extends Activity {
    private LinearLayout updateMessages;
    private HashMap<String, Integer> nationalitySpinner;

    public void clearMessages() {
        updateMessages.removeAllViews();
    }

    public void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateMessages.addView(textView);
    }

    public void doUpdateProfile(View view) {
        clearMessages();

        // Mutable
        String  old_pw = (old_pw = ((EditText) findViewById(R.id.old_password_field)).getText().toString()) == null ? "" : old_pw,
                new_pw = (new_pw = ((EditText) findViewById(R.id.passport_field)).getText().toString()) == null ? "" : new_pw,
                new_cpw = (new_cpw = ((EditText) findViewById(R.id.confirm_field)).getText().toString()) == null ? "" : new_cpw,
                passportNumber = (passportNumber = ((EditText) findViewById(R.id.passport_field)).getText().toString()) == null ? "" : passportNumber,
                address = (address = ((EditText) findViewById(R.id.address_field)).getText().toString()) == null ? "" : address,
                nationality = (nationality = ((Spinner) findViewById(R.id.nationality_field)).getSelectedItem().toString()) == null ? "" : nationality,
                password = old_pw;

        int valid = 1;
        if(old_pw.isEmpty()) {
            valid = 0;
            putMessage("You must provide your old password!");
        } if(!new_pw.isEmpty() && !new_cpw.isEmpty()) {
            if(!new_pw.equals(new_cpw)) {
                valid = 0;
                putMessage("New password and confirm password mismatch!");
            } else {
                password = new_pw;
            }
        } if(passportNumber.isEmpty()) {
            valid = 0;
            putMessage("Passport number must not be empty!");
        } if(address.isEmpty()) {
            valid = 0;
            putMessage("Address field must not be empty!");
        } if(nationality.isEmpty()) {
            valid = 0;
            putMessage("Nationality field must not be empty!");
        }

        ProgressDialog pd = ProgressDialog.show(this, null, "Updating profile ...", true);

        User user = Global.getUserManager().getUser();

        new SimpleHttpPost(new Pair<String, String>("id", "" + user.getId())
                , new Pair<String, String>("email", user.getEmail())
                , new Pair<String, String>("password", password)
                , new Pair<String, String>("full_name", user.getFullName())
                , new Pair<String, String>("address", address)
                , new Pair<String, String>("gender", user.getGender())
                , new Pair<String, String>("passport_number", passportNumber)
                , new Pair<String, String>("nationality", nationality)
                , new Pair<String, String>("date_of_birth", user.getDateOfBirth())
                , new Pair<String, String>("session_id", user.getSessionId())) {
            private ProgressDialog pd;
            public SimpleHttpPost init(ProgressDialog pd) {
                this.pd = pd;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                pd.dismiss();
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            finish();
                            break;
                        default:
                            JSONArray messages = response.getJSONArray("message");
                            for(int i = 0, size = messages.length(); i < size; ++i) {
                                putMessage(messages.getString(i));
                            }
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }
            }
        }.init(pd).send(Global.USER_UPDATE_URL);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

        updateMessages = (LinearLayout) findViewById(R.id.update_messages);

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
