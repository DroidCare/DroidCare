package com.droidcare;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
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
	private LinearLayout register_messages;

	public void pickDateBtn(final View btn) {
		DialogFragment datePicker = new DatePickerFragment(){
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				((Button) btn).setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
			}
		};
		datePicker.show(getFragmentManager(), "datePicker");
	}
	
	public void doRegisterUser(View view) {
		view.setEnabled(false);
		register_messages.removeAllViews();
		
		TextView register_status = new TextView(this);
		register_status.setText("Registering user ...");
		register_messages.addView(register_status);
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		EditText passport_field = (EditText) findViewById(R.id.passport_field);
		EditText name_field = (EditText) findViewById(R.id.name_field);
		EditText address_field = (EditText) findViewById(R.id.address_field);
		EditText email_field = (EditText) findViewById(R.id.email_field);
		Button dob_field = (Button) findViewById(R.id.dob_field);
		Spinner gender_field = (Spinner) findViewById(R.id.gender_field);
		Spinner nationality_field = (Spinner) findViewById(R.id.nationality_field);
		
		EditText password_field = (EditText) findViewById(R.id.password_field);
		EditText confirm_field = (EditText) findViewById(R.id.confirm_field);

		String password = password_field.getText().toString();
		String confirm  = confirm_field.getText().toString();
		
		if(password.equals(confirm)) {
			data.put("passport_number", passport_field.getText().toString());
			data.put("full_name", name_field.getText().toString());
			data.put("email", email_field.getText().toString());
			data.put("password", password_field.getText().toString());
			data.put("address", address_field.getText().toString());
			data.put("gender", gender_field.getSelectedItem().toString());
			data.put("nationality", nationality_field.getSelectedItem().toString());
			data.put("date_of_birth", dob_field.getText().toString());
			
			String responseText = new HttpPostRequest(data).send(Global.USER_REGISTER_URL);
			
			int status = -1;
			try {
				JSONObject response = new JSONObject(responseText);
				
				status = response.getInt("status");
				JSONArray messages = response.getJSONArray("message");
				
				switch(status) {
				
				case 0:
					Intent result = new Intent();
					setResult(Activity.RESULT_OK, result);
					finish();
					
					break;
				default:
					register_messages.removeAllViews();
					for(int i = 0, size = messages.length(); i < size; ++i){
						TextView message = new TextView(this);
						message.setText("\uu2022 " + messages.getString(i));
						message.setLayoutParams(new LayoutParams
								(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
						register_messages.addView(message);
					}
					break;
				}
			} catch (JSONException e) {
			}
		} else {
			register_status.setText("Password and confirm password mismatch!");
		}
		
		view.setEnabled(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		register_messages = (LinearLayout) findViewById(R.id.register_messages);
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
