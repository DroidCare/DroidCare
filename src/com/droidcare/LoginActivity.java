package com.droidcare;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private LinearLayout login_messages;
	
	public void doLoginRequest(View view) {
		view.setEnabled(false);
		login_messages.removeAllViews();
		
		TextView login_status = new TextView(this);
		login_status.setLayoutParams(new LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		login_status.setText("Authenticating user ...");
		login_messages.addView(login_status);
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		EditText email_field = (EditText) findViewById(R.id.email_field);
		EditText password_field = (EditText) findViewById(R.id.password_field);
		
		data.put("email",  email_field.getText().toString());
		data.put("password",  password_field.getText().toString());
		
		String responseText = new HttpPostRequest(data).send(Global.USER_LOGIN_URL);
		Log.d("responseText", "=" + responseText);
		
		int status = -1;
		try {
			JSONObject response = new JSONObject(responseText);
			
			status = response.getInt("status");
			JSONArray messages = response.getJSONArray("message");
			
			switch(status) {
			
			case 0:				
				Global.putStringPrefs("session_id", messages.getString(0));
				
				Intent intent = new Intent(this, HomeActivity.class);
				startActivity(intent);
				break;
			default:
				login_messages.removeAllViews();
				for(int i = 0, size = messages.length(); i < size; ++i){
					TextView login_message = new TextView(this);
					login_message.setText("\u2022 " + messages.getString(i));
					login_message.setLayoutParams(new LayoutParams
							(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					login_messages.addView(login_message);
				}
				break;
			}
		} catch (JSONException e) {
		}
		
		view.setEnabled(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// If user is logged in before AND session_id hasn't expired
		Global.fetchUserDetails();
		if(Global.getUser() != null){
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		
		login_messages = (LinearLayout) findViewById(R.id.login_messages);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
