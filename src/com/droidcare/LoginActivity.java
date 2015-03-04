package com.droidcare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private LinearLayout loginMessages;

    private void clearMessages() {
        loginMessages.removeAllViews();
    }

    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textView.setText("\u2022 " + message);
        loginMessages.addView(textView);
    }
	
	public void doLogin(View view) {
        clearMessages();
		view.setEnabled(false);
		
		EditText emailField = (EditText) findViewById(R.id.email_field);
		EditText passwordField = (EditText) findViewById(R.id.password_field);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        LoginManager loginManager = Global.getLoginManager();

        ProgressDialog pd = ProgressDialog.show(this, null, "Authenticating user ...", true);

        switch(loginManager.validateForm(email, password)) {
            case LoginManager.VALID_FORM:

                loginManager.doLoginRequest(email, password, new OnFinishLoginListener() {
                    private ProgressDialog pd;
                    private View btn;

                    public OnFinishLoginListener init(View btn, ProgressDialog pd) {
                        this.btn = btn;
                        this.pd = pd;
                        return this;
                    }

                    @Override
                    public void onFinishTask(String responseText) {
                        btn.setEnabled(true);
                        pd.dismiss();

                        try {
                            JSONObject response = new JSONObject(responseText);
                            JSONArray messages = response.getJSONArray("message");

                            clearMessages();
                            for(int i = 0, size = messages.length(); i < size; ++i) {
                                putMessage(messages.getString(i));
                            }
                        } catch (JSONException e) {
                        }
                    }
                }.init(view, pd));
                break;
            case LoginManager.PASSWORD_EMPTY:
                putMessage("Password field is empty!");
            case LoginManager.EMAIL_EMPTY:
                putMessage("Email field is empty!");
            case LoginManager.PASSWORD_TOO_SHORT:
                putMessage("Password should be at least 4 characters!");
            default:
                view.setEnabled(true);
                break;
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ProgressDialog pd = ProgressDialog.show(this, null, "Loading ...", true);

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
