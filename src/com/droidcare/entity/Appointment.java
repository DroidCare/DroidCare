package com.droidcare.entity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.droidcare.control.Global;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/*
 * ATTENTION!!!
 * THIS CLASS IS ONLY FOR POLYMORPHISM (AS A BASE CLASS) OR NORMAL APPOINTMENT ONLY!!!
 */

/**
 * Stores a normal appointment detailed information. This class is also a base class for {@link FollowUpAppointment} and
 * {@link ReferralAppointment} class.
 * @author Edwin Candinegara
 */

public class Appointment implements Parcelable, Comparable<Appointment> {
	/**
	 * Constant defining "PENDING" status.
	 */
	public static final String PENDING = "pending";
	
	/**
	 * Constant defining "ACCEPTED" status.
	 */
	public static final String ACCEPTED = "accepted";
	
	/**
	 * Constant defining "REJECTED" status.
	 */
	public static final String REJECTED = "rejected";
	
	/**
	 * Constant defining "FINISHED" status.
	 */
	public static final String FINISHED = "finished";
	
	/**
	 * Constant defining "FOLLOW-UP" appointment type
	 */
	public static final String FOLLOW_UP = "follow-up";
	
	/**
	 * Constant defining "REFERRAL" appointment type
	 */
	public static final String REFERRAL = "referral";
	
	/**
	 * Constant defining "NORMAL" appointment type
	 */
	public static final String NORMAL = "normal";
	/**
	 * ID of this {@link Appointment} object.
	 */
	private int	id;
	
	/**
	 * ID of the current {@link User} object.
	 */
	private int	patientId;
	
	/**
	 * ID of the consultant. 
	 */
	private int	consultantId;
				
	/**
	 * The appointment's date and time in milliseconds.
	 */
	private long dateTimeMillis;

	/**
	 * name of the patient.
	 */
	private String patientName;
	
	/**
	 * name of the consultant.
	 */
	private String consultantName;
	
	/**
	 * the patient's health issue.
	 */
	private String healthIssue;
	
	/**
	 * any additional information
	 */
	private String remarks;
	
	/**
	 * the appointment's type.
	 */
	private String type;
	
	/**
	 * the current status of this {@link Appointment} object.
	 */
	private String status;
	
	/**
	 * Constructs an {@link Appointment} object.
	 * @param id				the ID of this {@link Appointment} object.
	 * @param patientId			the ID of the patient
	 * @param consultantId		the ID of the consultant
	 * @param dateTime			the date and time of the appointment
	 * @param patientName		the name of the patient
	 * @param consultantName	the name of the consultant
	 * @param healthIssue		the patient's health issue
	 * @param type				the type of this appointment
	 * @param remarks			additional information for this appointment
	 * @param status			the current status of the appointment
	 */
	public Appointment(int id, int patientId, int consultantId, String dateTime
			, String patientName, String consultantName, String healthIssue
			, String type, String remarks, String status) {
		this.id				= id;
		this.patientId		= patientId;
		this.consultantId	= consultantId;
		this.patientName 	= patientName;
		this.consultantName = consultantName;
		this.healthIssue	= healthIssue;
		this.type 			= type;
		this.status			= status;
		this.remarks 		= remarks;
		
		try {
			Calendar c = new GregorianCalendar();
			c.setTime(Global.dateFormat.parse(dateTime));
			c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			
			this.dateTimeMillis = c.getTimeInMillis();
			Log.d("APPOINTMENT DATE TIME", this.dateTimeMillis + "    " + dateTime);
			//this.dateTimeMillis = Global.dateFormat.parse(dateTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			
			// Set time date to now? for initialization
			// to prevent further error
			this.dateTimeMillis = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.ENGLISH).getTimeInMillis();
		}
	}
	
	/**
	 * Returns this {@link Appointment} object ID
	 * @return the ID of this {@link Appointment} object
	 */
	public int getId () {
		return id;
	}
	
	/**
	 * Returns the patient's ID
	 * @return the patient's ID
	 */
	public int getPatientId() {
		return patientId;
	}
	
	/**
	 * Returns the consultant's ID
	 * @return the consultant's ID
	 */
	public int getConsultantId() {
		return consultantId;
	}

	/**
	 * Returns this {@link Appointment} object status
	 * @return the status of this {@link Appointment} object
	 */
    public String getStatus() {
        return status;
    }
	
    /**
     * Returns this {@link Appointment} object date and time in millisecond
     * @return the date and time of this {@link Appointment} object in millisecond
     */
	public long getDateTimeMillis() {
		return dateTimeMillis;
	}
	
	/**
	 * Returns the patient's name
	 * @return the patient's name
	 */
	public String getPatientName() {
		return patientName;
	}
	
	/**
	 * Returns the consultant's name
	 * @return the consultant's name
	 */
	public String getConsultantName() {
		return consultantName;
	}
	
	/**
	 * Returns the patient's health issue
	 * @return the patient's health issue
	 */
	public String getHealthIssue() {
		return healthIssue;
	}
	
	/**
	 * Returns the additional information
	 * @return the additional information
	 */
	public String getRemarks() {
		return remarks;
	}
	
	/**
	 * Returns the type of this {@link Appointment} object
	 * @return the type of this {@link Appointment} object
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets this {@link Appointment} object date and time in millisecond 
	 * @param dateTimeMillis the date and time in millisecond
	 */
	public void setDateTimeMillis (long dateTimeMillis) {
		this.dateTimeMillis = dateTimeMillis;
	}
	
	/**
	 * Sets the consultant's ID of this {@link Appointment} object
	 * @param id the consultant's ID
	 */
	public void setConsultantId (int id) {
		this.consultantId = id;
	}
	
	/**
	 * Sets the consultant's name of this {@link Appointment} object
	 * @param name the consultant's name
	 */
	public void setConsultantName (String name) {
		this.consultantName = name;
	}
	
	/**
	 * Sets the status of this {@link Appointment} object
	 * @param status the new status of this {@link Appointment} object
	 */
	public void setStatus (String status) {
		this.status = status;
	}
	
	/**
	 * Sets the additional information
	 * @param remarks the additional information
	 */
	public void setRemarks (String remarks) {
		this.remarks = remarks;
	}
	
	// COMPARABLE IMPLEMENTATION
	@Override
	public int compareTo(Appointment a) {
		return new Long(this.dateTimeMillis).compareTo(new Long(a.getDateTimeMillis()));
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public Appointment (Parcel in) {
		this.id = in.readInt();
		this.patientId = in.readInt();
		this.consultantId = in.readInt();
		this.dateTimeMillis = in.readLong();
		this.patientName = in.readString();
		this.consultantName = in.readString();
		this.healthIssue = in.readString();
		this.type = in.readString();
		this.remarks = in.readString();
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
		dest.writeLong(this.dateTimeMillis);
		dest.writeString(this.patientName);
		dest.writeString(this.consultantName);
		dest.writeString(this.healthIssue);
		dest.writeString(this.type);
		dest.writeString(this.remarks);
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
