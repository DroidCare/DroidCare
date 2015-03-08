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
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.droidcare.R;
import com.droidcare.R.id;
import com.droidcare.R.layout;
import com.droidcare.R.menu;
import com.droidcare.R.string;
import com.droidcare.control.Global;
import com.droidcare.control.LoginManager;

public class ForgetPasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forget_password, menu);
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
	
	public void forgetPassword (View v) {
		// @pciang : SEND REQUEST TO PHP SERVER
		String emailAddress = ((EditText) findViewById(R.id.emailAddress_field)).getText().toString();

        ProgressDialog pd = ProgressDialog.show(this, null, "Sending email...", true);
        Global.getLoginManager().forgetPasswordRequest(emailAddress, new LoginManager.OnFinishTaskListener() {
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
                            new AlertDialog.Builder(ForgetPasswordActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(null)
                                    .setMessage("Reset password successful!")
                                    .setNeutralButton(R.string.Button_OK, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ForgetPasswordActivity.this.finish();
                                        }
                                    })
                                    .show();
                            break;
                        default:
                            new AlertDialog.Builder(ForgetPasswordActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(null)
                                    .setMessage(response.getJSONArray("message").getString(0))
                                    .setNeutralButton(R.string.Button_OK, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                    e.printStackTrace();
                    ForgetPasswordActivity.this.finish();
                }
            }
        }.init(pd));
	}
}
