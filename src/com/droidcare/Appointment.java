package com.droidcare;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/*
 * ATTENTION!!!
 * THIS CLASS IS ONLY FOR POLYMORPHISM (AS A BASE CLASS) OR NORMAL APPOINTMENT ONLY!!!
 */

public class Appointment implements Parcelable{
	/**
	 * Appointment status.
	 */
	public static final String	PENDING 	= "pending",
								ACCEPTED 	= "accepted",
								REJECTED 	= "rejected",
								FINISHED 	= "finished";
	
	/**
	 * Appointment type.
	 */
	public static final String	FOLLOW_UP 	= "follow_up",
								REFERRAL 	= "referral",
								NORMAL 		= "normal";
	
	private int	id,
				patientId,
				consultantId,
				previousId;
	
	// To make it easier to "parcelize"
	private long dateTimeMillis;

	private String	patientName,
					consultantName,
					healthIssue,
					remarks,
					type,
					status;
	
	// public Appointment(){} // An empty constructor
	
	public Appointment(int id, int patientId, int consultantId, String dateTime
			, String patientName, String consultantName, String healthIssue
			, String type, int previousId, String remarks, String status) {
		this.id				= id;
		this.patientId		= patientId;
		this.consultantId	= consultantId;
		this.previousId		= previousId;
		this.patientName 	= patientName;
		this.consultantName = consultantName;
		this.healthIssue	= healthIssue;
		this.type 			= type;
		this.status			= status;
		this.remarks = remarks;
		
		try {
			this.dateTimeMillis = Global.dateFormat.parse(dateTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			
			// Set time date to now? for initialization
			// to prevent further error
			this.dateTimeMillis = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.ENGLISH).getTimeInMillis();
		}
	}
	
	// GETTER
	public int getId () {
		return id;
	}
	
	public int getPatientId() {
		return patientId;
	}
	
	public int getConsultantId() {
		return consultantId;
	}
	
	public int getPreviousId() {
		return previousId;
	}

    public String getStatus() {
        return status;
    }
	
	public long getDateTimeMillis() {
		return dateTimeMillis;
	}
	
	public String getPatientName() {
		return patientName;
	}
	
	public String getConsultantName() {
		return consultantName;
	}
	
	public String getHealthIssue() {
		return healthIssue;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public String getType() {
		return type;
	}
	
	// SETTER
	public void setDateTimeMillis (long dateTimeMillis) {
		this.dateTimeMillis = dateTimeMillis;
	}
	
	public void setConsultantId (int id) {
		this.consultantId = id;
	}
	
	public void setConsultantName (String name) {
		this.consultantName = name;
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
	public void setRemarks (String remarks) {
		this.remarks = remarks;
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public Appointment (Parcel in) {
		this.id = in.readInt();
		this.patientId = in.readInt();
		this.consultantId = in.readInt();
		this.previousId = in.readInt();
		this.dateTimeMillis = in.readLong();
		this.patientName = in.readString();
		this.consultantName = in.readString();
		this.healthIssue = in.readString();
		this.type = in.readString();
		this.status = in.readString();
	}
	
	@Override
	public int describeContents () {
		return 0;
	}
	
	@Override
	public void writeToParcel (Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeInt(this.patientId);
		dest.writeInt(this.consultantId);
		dest.writeInt(this.previousId);
		dest.writeLong(this.dateTimeMillis);
		dest.writeString(this.patientName);
		dest.writeString(this.consultantName);
		dest.writeString(this.healthIssue);
		dest.writeString(this.type);
		dest.writeString(this.status);
	}
	
	public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment> () {
		public Appointment createFromParcel (Parcel in) {
			return new Appointment(in);
		}
		
		public Appointment[] newArray (int size) {
			return new Appointment[size];
		}
	};
}
