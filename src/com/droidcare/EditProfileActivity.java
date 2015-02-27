package com.droidcare;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditProfileActivity extends Activity {
	private LinearLayout messagesHolder;

	public void doUpdateProfile(View view) {
		String
			old_pw = ((EditText) findViewById(R.id.old_password_field)).getText().toString(),
			new_pw = ((EditText) findViewById(R.id.password_field)).getText().toString(),
			new_cpw = ((EditText) findViewById(R.id.confirm_field)).getText().toString();
		
		if(!old_pw.isEmpty() || (!old_pw.isEmpty() && !new_pw.isEmpty()
				&& !new_cpw.isEmpty())){
			
			int id = Global.getUserManager().getUser().getId();
			
			String
				passportNumber = ((EditText) findViewById(R.id.passport_field)).getText().toString(),
				fullName = ((EditText) findViewById(R.id.name_field)).getText().toString(),
				address = ((EditText) findViewById(R.id.address_field)).getText().toString(),
				email = ((EditText) findViewById(R.id.email_field)).getText().toString(),
				dateOfBirth = ((EditText) findViewById(R.id.dob_field)).getText().toString(),
				gender = ((EditText) findViewById(R.id.gender_field)).getText().toString(),
				nationality = ((EditText) findViewById(R.id.nationality_field)).getText().toString();
			
			HashMap<String, String> data = new HashMap<String, String>();
			
			data.put("id", "" + id);
			data.put("email", email);
			data.put("password", new_pw.isEmpty() ? old_pw : new_pw); // If user does not change his old password
			data.put("full_name", fullName);
			data.put("address", address);
			data.put("gender", gender);
			data.put("passport_number", passportNumber);
			data.put("nationality", nationality);
			data.put("date_of_birth", dateOfBirth);
			data.put("session_id", Global.getAppSession().getString("session_id"));
			
			view.setEnabled(false);
			ProgressDialog pd = ProgressDialog
					.show(this, "Updating profile ...", "Please wait!", true);
			messagesHolder.removeAllViews();
			
			new AsyncTask<Void, Void, String>(){
				private View view;
				private ProgressDialog pd;
				private LinearLayout mh;
				private HashMap<String, String> data;
				
				public AsyncTask<Void, Void, String> init(View view, ProgressDialog pd
						, LinearLayout mh, HashMap<String, String> data){
					this.view = view;
					this.pd = pd;
					this.mh = mh;
					this.data = data;
					
					return this;
				}
				
				@Override
				protected String doInBackground(Void... params) {
					return new HttpPostRequest(data).send(Global.USER_UPDATE_URL);
				}
				
				@Override
				protected void onPostExecute(String result) {
					
					int status = -1;
					try {
						JSONObject response = new JSONObject(result);
						JSONArray messages = response.getJSONArray("message");
						
						status = response.getInt("status") == 0							// Server response OK
								&& Global.getUserManager().fetchUserDetails() ? 0 : -1;	// Successfully fetch user's details
						
						switch(status){
						
						case 0:							
							pd.dismiss();
							new AlertDialog.Builder(EditProfileActivity.this)
									.setIcon(android.R.drawable.ic_dialog_info)
									.setMessage("Profile successfully updated!")
									.setTitle("Profile Update")
									.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											EditProfileActivity.this.finish();
										}
									}).show();
							break;
						default:
							pd.dismiss();
							for(int i = 0, size = messages.length(); i < size; ++i){
								TextView textView = new TextView(EditProfileActivity.this);
								textView.setLayoutParams(
										new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
								textView.setText(messages.getString(i));
								mh.addView(textView);
							}
							break;
						}
					} catch (JSONException e) {
					}

					pd.dismiss();
					view.setEnabled(true);
				}
				
			}.init(view, pd, messagesHolder, data)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		// Disable all immutable attributes
		for(int id: (new int[] {
				R.id.passport_field,
				R.id.name_field,
				R.id.email_field,
				R.id.dob_field,
				R.id.gender_field,
				R.id.nationality_field
		})){
			((EditText) findViewById(id)).setKeyListener(null);
		}
		
		User user = Global.getUserManager().getUser();
		
		((EditText) findViewById(R.id.passport_field)).setText(user.getPassportNumber());
		((EditText) findViewById(R.id.name_field)).setText(user.getFullName());
		((EditText) findViewById(R.id.address_field)).setText(user.getAddress());
		((EditText) findViewById(R.id.email_field)).setText(user.getEmail());
		((EditText) findViewById(R.id.dob_field)).setText(user.getDateOfBirth());
		((EditText) findViewById(R.id.nationality_field)).setText(user.getNationality());

		switch(user.getGender()){
		
		case 'M': case 'm':
			((EditText) findViewById(R.id.gender_field)).setText("Male");
			break;
		
		case 'F': case 'f':
			((EditText) findViewById(R.id.gender_field)).setText("Female");
			break;
		}
		
		messagesHolder = (LinearLayout) findViewById(R.id.messages_holder);
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
