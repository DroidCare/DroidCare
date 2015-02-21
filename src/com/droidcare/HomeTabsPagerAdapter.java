package com.droidcare;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeTabsPagerAdapter extends FragmentPagerAdapter {
	private final int NO_OF_TABS = 2;
	
	public HomeTabsPagerAdapter (FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem (int index) {
		switch (index) {
		case 0:
			return new UpcomingAppointmentsList();
		case 1:
			return new PendingAppointmentsList();
		}
		
		return null;
	}
	
	@Override
	public int getCount () {
		return NO_OF_TABS;
	}
}
