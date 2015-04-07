package com.droidcare.control;

import com.droidcare.boundary.AcceptedAppointmentList;
import com.droidcare.boundary.FinishedAppointmentList;
import com.droidcare.boundary.PendingAppointmentList;
import com.droidcare.boundary.RejectedAppointmentList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * This class is responsible for handling switching between tabs and determines what
 * to be displayed inside the selected tabs
 * @author Stephanie
 */


public class HomeTabsPagerAdapter extends FragmentPagerAdapter {
	/**
	 * Number of tabs to be shown
	 */
	private final int NO_OF_TABS = 4;
	
	/**
	 * Constructs a {@link HomeTabsPagerAdapter} object
	 * @param fm
	 */
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
			return new RejectedAppointmentList();
		case 3:
			return new FinishedAppointmentList();
        default:
            Log.d("DEBUGGING", "track");
            break;
		}
		
		return null;
	}
	
	@Override
	public int getCount () {
		return NO_OF_TABS;
	}
}
