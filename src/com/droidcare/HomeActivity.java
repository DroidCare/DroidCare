package com.droidcare;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {
	private ViewPager viewPager;
	private HomeTabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private String[] tabs = {"Upcoming", "Pending", "Rejected"};
	
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		user = Global.getUserManager().getUser();

        /*
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
		*/
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
        switch (item.getItemId()) {
            case R.id.action_logout:
                doLogout();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
		
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton(R.string.Button_Logout, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                doLogout();
                            }
                        })
                        .setNegativeButton(R.string.Button_Cancel, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    private void doLogout() {
        ProgressDialog pd = ProgressDialog.show(this, null, "Logging out ...", true);

        Global.getLoginManager().doLogoutRequest(new LoginManager.OnFinishTaskListener() {
            private ProgressDialog pd;

            public LoginManager.OnFinishTaskListener init(ProgressDialog pd) {
                this.pd = pd;
                return this;
            }

            @Override
            public void onFinishTask(String responseText) {
                pd.dismiss();
                HomeActivity.this.finish();
            }
        }.init(pd));
    }
}
