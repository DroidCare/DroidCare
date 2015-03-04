package com.droidcare;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This class is responsible for handling switching between tabs and determines what
 * to be displayed inside the selected tabs
 *
 */

/*
 * @pciang: Hi, can I be your co-author? :)
 */

public class HomeTabsPagerAdapter extends FragmentPagerAdapter {
	private final int NO_OF_TABS = 3;
	
	public HomeTabsPagerAdapter (FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem (int index) {
		switch (index) {
		
		case 0:
			return new AcceptedAppointmentList();
		case 1:
			return new PendingAppointmentList();
		case 2:
			return new FinishedAppointmentList();
		case 3:
			return new RejectedAppointmentList();
		}
		
		return null;
	}
	
	@Override
	public int getCount () {
		return NO_OF_TABS;
	}
}
