package com.droidcare;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
		
		final HashMap<String, String> data = new HashMap<String, String>();
		
		EditText email_field = (EditText) findViewById(R.id.email_field);
		EditText password_field = (EditText) findViewById(R.id.password_field);
		
		data.put("email",  email_field.getText().toString());
		data.put("password",  password_field.getText().toString());
		
		// Not a good nor bad practice
		// nevertheless, a clever workaround
		new AsyncTask<Void, Void, String>(){
			private View view;
			private LinearLayout login_messages;
			
			public AsyncTask<Void, Void, String> init(View view, LinearLayout login_messages) {
				this.view = view;
				this.login_messages = login_messages;
				return this;
			}
			
			@Override
			protected String doInBackground(Void... params) {
				return new HttpPostRequest(data).send(Global.USER_LOGIN_URL);
			}
			
			@Override
			protected void onPostExecute(String result) {
				int status = -1;
				try {
					JSONObject response = new JSONObject(result);
					JSONArray messages = response.getJSONArray("message");
					
					status = response.getInt("status");
					Log.d("status", "=" + status);
					
					switch(status){
					
					case 0:
						Global.putStringPrefs("session_id", messages.getString(0));
						
						Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
						startActivity(intent);
						return;
					default:
						login_messages.removeAllViews();
						for(int i = 0, size = messages.length(); i < size; ++i){
							TextView message = new TextView(LoginActivity.this);
							message.setText("\uu2022 " + messages.getString(i));
							message.setLayoutParams(new LayoutParams
									(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
							login_messages.addView(message);
						}
						
						break;
					}
				// Do nothing on exception
				} catch (JSONException e) {
				}
				
				view.setEnabled(true);
			}
		}.init(view, login_messages).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// FOR THE SAKE OF DEBUGGING, PLS
		((EditText) findViewById(R.id.email_field)).setText("kenrick95@gmail.com");
		((EditText) findViewById(R.id.password_field)).setText("123456");
		
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
