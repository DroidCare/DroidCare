package com.droidcare.boundary;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class TitleActivity extends Activity {
	private static final int REGISTER_ACTIVITY = 0;
	
	public void gotoRegisterActivity(View view) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivityForResult(intent, REGISTER_ACTIVITY);
	}
	
	public void gotoLoginActivity(View view) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        
        Global.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.title, menu);
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
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode) {
    	
    	case REGISTER_ACTIVITY:
    		if(resultCode == Activity.RESULT_OK) {
    			new AlertDialog.Builder(this)
						.setTitle("Registration Success")
						.setMessage("You have been successfully registered in DroidCare!")
						.setPositiveButton(R.string.Button_OK, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Close dialog
								dialog.dismiss();
							}
						})
						.setIcon(android.R.drawable.ic_dialog_info)
						.show();
    		}
    		break;
    	}
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    }
}
