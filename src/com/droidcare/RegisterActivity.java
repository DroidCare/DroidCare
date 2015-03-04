package com.droidcare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	private LinearLayout registerMessages;
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy/MM/dd");

	public void pickDateBtn(View btn) {
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

		EditText passport_field = (EditText) findViewById(R.id.passport_field);
		EditText name_field = (EditText) findViewById(R.id.name_field);
		EditText address_field = (EditText) findViewById(R.id.address_field);
		EditText email_field = (EditText) findViewById(R.id.email_field);
		Button dob_field = (Button) findViewById(R.id.dob_field);
		Spinner gender_field = (Spinner) findViewById(R.id.gender_field);
		Spinner nationality_field = (Spinner) findViewById(R.id.nationality_field);

		EditText password_field = (EditText) findViewById(R.id.password_field);
		EditText confirm_field = (EditText) findViewById(R.id.confirm_field);

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
        } if(fullName == null || fullName.isEmpty()) {
            putMessage("Full name is empty!");
            valid = 0;
        } if(address == null || address.isEmpty()) {
            putMessage("Address field is empty!");
            valid = 0;
        } if(email == null || email.isEmpty()) {
            putMessage("Email address is empty!");
            valid = 0;
        }
        try {
            dateOfBirthFormat.format(dateOfBirthFormat.parse(dateOfBirth)).equals(dateOfBirth);
        } catch (ParseException e) {
            putMessage("Please select your date of birth!");
            valid = 0;
        }

        if(gender == null || gender.isEmpty()) {
            putMessage("Please select your gender!");
            valid = 0;
        } if(nationality == null || nationality.isEmpty()) {
            putMessage("Please select your nationality!");
            valid = 0;
        } if(password == null || password.isEmpty()) {
            putMessage("Password field is empty!");
            valid = 0;
        } if(confirm == null || confirm.isEmpty()) {
            putMessage("Confirm password field is empty!");
            valid = 0;
        } if(password != null
                && password.length() < Global.getRegisterManager().passwordMinLength) {
            putMessage("Password is too short!");
            valid = 0;
        } if(password != null && confirm != null
                && password.equals(confirm)) {
            putMessage("Password and confirm password mismatch!");
            valid = 0;
        }

        switch(valid) {
            // Do nothing
            case 0:
                break;

            default:
                ProgressDialog pd = ProgressDialog.show(this, null, "Registering user ...", true);
                Global.getRegisterManager().registerUser(new OnFinishRegisterListener() {
                    private View btn;
                    private ProgressDialog pd;

                    public OnFinishRegisterListener init(View btn, ProgressDialog pd) {
                        this.btn = btn;
                        this.pd = pd;
                        return this;
                    }

                    @Override
                    public void onFinishRegister(String responseText) {
                        pd.dismiss();
                        btn.setEnabled(true);
                        RegisterActivity.this.setResult(Activity.RESULT_OK);
                        RegisterActivity.this.finish();
                    }
                }.init(view, pd));
                break;
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		registerMessages = (LinearLayout) findViewById(R.id.register_messages);
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
