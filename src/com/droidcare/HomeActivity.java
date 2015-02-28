package com.droidcare;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {
	private ViewPager viewPager;
	private HomeTabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private String[] tabs = {"Upcoming", "Pending"};
	
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		user = Global.getUserManager().getUser();
		Log.d("session_id", "=" + Global.getAppSession().getString("session_id"));
		
		// List Fragment Initialization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new HomeTabsPagerAdapter(getSupportFragmentManager());
				
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				
		// Adding tabs
		for (String n: tabs) {
			Tab t = actionBar.newTab().setText(n).setTabListener(this);
			actionBar.addTab(t);
		}
			
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	
	// Swipe view listener
	@Override
	public void onTabSelected (Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabReselected (Tab tab, FragmentTransaction ft) {}
	
	@Override
	public void onTabUnselected (Tab tab, FragmentTransaction ft) {}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		
		case R.id.action_logout:
			ProgressDialog progressDialog = ProgressDialog.show(this, "Logging out ...", "Please wait!", true);
			
			// Do this in async task
			// to prevent blocking in main UI thread
			new AsyncTask<Void, Void, Void>(){
				private ProgressDialog progressDialog;
				
				// Clever workaround
				// http://stackoverflow.com/questions/5107158/how-to-pass-parameters-to-anonymous-class/12206542#12206542
				private AsyncTask<Void, Void, Void> init(ProgressDialog progressDialog) {
					this.progressDialog = progressDialog;
					return this;
				}
				
				@Override
				protected Void doInBackground(Void... params) {
					doLogout();
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					progressDialog.dismiss();
				}
			}.init(progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			return true;
			
		case R.id.action_settings:
			return true;
			
		case R.id.action_EditProfile:
			Intent intent = new Intent(this, EditProfileActivity.class);
			startActivity(intent);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// Logout mechanism
	private void doLogout() {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("session_id", Global.getAppSession().getString("session_id"));
		
		int status = -1;
		String responseText = new HttpPostRequest(data).send(Global.USER_LOGOUT_URL);
		try {
			JSONObject response = new JSONObject(responseText);
			
			status = response.getInt("status");
			JSONArray messages = response.getJSONArray("message");
			
			switch(status){
			
			case 0:
				Log.d("Log out", "Successful!");
				break;
			default:
				Log.d("Log out", "Unsuccessful!");
				break;
			}
		
		// Always do nothing on exception
		} catch (JSONException e) {
		}
		
		// Either user successfully or unsuccessfully logout,
		// clear all session data
		Global.getUserManager().removeUser();
		Global.getAppSession().clearAll();
		
		// This need some work
		// Global.clearAppointmentManager();
		
		HomeActivity.this.finish();
	}
}
