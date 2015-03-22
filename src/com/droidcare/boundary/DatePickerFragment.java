package com.droidcare.boundary;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.DatePickerDialog;

/**
 * 
 * @author Edwin Candinegara
 * Defines the date picker fragment.
 *
 */

public abstract class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	@Override
	public Dialog onCreateDialog(Bundle SavedInstance) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
}