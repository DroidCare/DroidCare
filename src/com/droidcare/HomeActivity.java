package com.droidcare;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// If user is logged in before AND session_id hasn't expired
		Global.fetchUserDetails();
		if(Global.getUser() == null){
			Log.d("Global.getUser()", "== null");
			
			// User's session_id has expired 
			finish();
			return;
		}
		
		// Else, welcome user
		//TextView welcomeLabel = (TextView) findViewById(R.id.welcome_label);
		//welcomeLabel.setText("Hello, " + Global.getUser().getFullName());
		
		// Initialization
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
			doLogout();
			return true;
		case R.id.action_settings:
			return true;
		case R.id.action_EditProfile:
			Log.d("onOptionsItemSelected()", "action_EditProfile selected");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onTabSelected (Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabReselected (Tab tab, FragmentTransaction ft) {}
	
	@Override
	public void onTabUnselected (Tab tab, FragmentTransaction ft) {}
	
	private void doLogout() {
		Global.clearPrefs();
		Global.clearUser();
		
		finish();
	}
}
