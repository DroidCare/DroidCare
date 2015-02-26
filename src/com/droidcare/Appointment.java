package com.droidcare;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class Appointment {
	private int	id,
				patientId,
				consultantId,
				previousId;
	
	private Calendar dateTime;

	private String	healthIssue,
					attachmentPath,
					type,
					referrerName,
					referrerClinic;
	
	public Appointment(){} // An empty constructor
	
	public Appointment(int id, int patientId, int consultantId, String dateTime
			, String healthIssue, String attachmentPath, String type, String referrerName
			, String referrerClinic, String previousId){
		this.id				= id;
		this.patientId		= patientId;
		this.consultantId	= consultantId;
		
		this.healthIssue	= healthIssue;
		this.attachmentPath = attachmentPath;
		this.type			= type;
		this.referrerName	= referrerName;
		this.referrerClinic = referrerClinic;
		
		try {
			this.dateTime.setTime(Global.dateFormat.parse(dateTime));
		} catch (ParseException e) {
			e.printStackTrace();
			
			// Set time date to now? for initialization
			// to prevent further error
			this.dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.ENGLISH);
		}
	}
}
