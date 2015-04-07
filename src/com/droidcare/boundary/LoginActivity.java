package com.droidcare.boundary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.droidcare.*;
import com.droidcare.control.*;
import android.app.Activity;
import android.app.ProgressDialog;
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

/**
 * Activity handling login.
 * @author Edwin Candinegara
 */

public class LoginActivity extends Activity {
	/**
	 * {@link LinearLayout} holding error messages
	 */
	private LinearLayout loginMessages;

	/**
	 * Clears all messages in {@link #loginMessages}
	 */
    private void clearMessages() {
        loginMessages.removeAllViews();
    }

    /**
     * Adds a message in {@link #loginMessages}
     * @param message
     */
    private void putMessage(String message) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textView.setText("\u2022 " + message);
        loginMessages.addView(textView);
    }
	
    /**
     * Handles login request submission
     * @param view	the View firing the event
     */
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
                loginManager.doLoginRequest(email, password, new LoginManager.OnFinishTaskListener() {
                    private ProgressDialog pd;
                    private View btn;
                    public LoginManager.OnFinishTaskListener init(View btn, ProgressDialog pd) {
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
                            switch(response.getInt("status")) {
                                case 0:
                                    LoginActivity.this.finish();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    JSONArray messages = response.getJSONArray("message");
                                    clearMessages();
                                    for(int i = 0, size = messages.length(); i < size; ++i) {
                                        putMessage(messages.getString(i));
                                    }
                                    break;
                            }

                        } catch (JSONException e) {
                            Log.d("DEBUGGING", "Not HTTP Status 400, responseText = " + responseText);
                        }
                    }
                }.init(view, pd));
                break;
            case LoginManager.PASSWORD_EMPTY:
                putMessage("Password field is empty!");
            case LoginManager.EMAIL_EMPTY:
                putMessage("Email field is empty!");
            case LoginManager.PASSWORD_TOO_SHORT:
                putMessage("Password should be at least 6 characters!");
            default:
                view.setEnabled(true);
                pd.dismiss();
                break;
        }
	}
	
	/**
	 * An event handler opening {@link ForgetPasswordActivity}
	 * @param v		the View object firing the event
	 */
	public void forgetPassword (View v) {
		Intent intent = new Intent (this, ForgetPasswordActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        loginMessages = (LinearLayout) findViewById(R.id.login_messages);

        //((EditText) findViewById(R.id.email_field)).setText("kenrick95@gmail.com");
        //((EditText) findViewById(R.id.password_field)).setText("123456");

        ProgressDialog pd = ProgressDialog.show(this, null, "Loading ...", true);
        Global.getLoginManager().checkLogin(new LoginManager.OnFinishTaskListener() {
            private ProgressDialog pd;
            public LoginManager.OnFinishTaskListener init(ProgressDialog pd) {
                this.pd = pd;
                return this;
            }
            @Override
            public void onFinishTask(String responseText) {
                pd.dismiss();
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            // Log.d("DEBUGGING", "YES USER IS LOGGED IN!");
                            finish();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            pd.dismiss();
                            break;
                    }
                } catch (JSONException e) {
                    pd.dismiss();
                }
            }
        }.init(pd));
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
		
		return super.onOptionsItemSelected(item);
	}
}
