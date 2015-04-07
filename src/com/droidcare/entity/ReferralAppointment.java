package com.droidcare.entity;

import java.util.Date;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Edwin Candinegara
 * Stores detailed information of a referral appointment
 */

public class ReferralAppointment extends Appointment {
	/**
	 * The name of the appointment's referrer
	 */
	private String referrerName;
	
	/**
	 * The clinic name of the appointment's referrer 
	 */
	private String referrerClinic;
	
	/**
	 * Constructs a {@link ReferralAppointment} object.
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
	 * @param referrerName		the name of the appointment's referrer
	 * @param referrerClinic	the clinic name of the appointment's referrer
	 */
	public ReferralAppointment (int id, int patientId, int consultantId, String dateTime
			, String patientName, String consultantName, String healthIssue
			, String type, String remarks, String status, String referrerName
			, String referrerClinic) {
		super(id, patientId, consultantId, dateTime, patientName, consultantName, healthIssue, type, remarks, status);
		this.referrerName = referrerName;
		this.referrerClinic = referrerClinic;
	}
	
	/**
	 * Returns the name of the appointment's referrer
	 * @return the name of the appointment's referrer
	 */
	public String getReferrerName () {
		return this.referrerName;
	}
	
	/**
	 * Returns the clinic name of the appointment's referrer
	 * @return the clinic name of the appointment's referrer
	 */
	public String getReferrerClinic () {
		return this.referrerClinic;
	}
	
	/**
	 * Sets the name of the appointment's referrer
	 * @param referrerName the name of the appointment's referrer
	 */
	public void setReferrerName (String referrerName) {
		this.referrerName = referrerName;
	}
	
	/**
	 * Sets the clinic name of the appointment's referrer
	 * @param referrerClinic the clinic name of the appointment's referrer
	 */
	public void setReferrerClinic (String referrerClinic) {
		this.referrerClinic = referrerClinic;
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public ReferralAppointment (Parcel in) {
		super(in.readInt(), in.readInt(), in.readInt(), Global.dateFormat.format(new Date(in.readLong())), 
				in.readString(), in.readString(), in.readString() ,in.readString(),
				in.readString(), in.readString());
	
		this.referrerName = in.readString();
		this.referrerClinic = in.readString();
	}
	
	@Override
	public int describeContents () {
		return 0;
	}
	
	@Override
	public void writeToParcel (Parcel dest, int flags) {
		dest.writeInt(getId());
		dest.writeInt(getPatientId());
		dest.writeInt(getConsultantId());
		dest.writeLong(getDateTimeMillis());
		dest.writeString(getPatientName());
		dest.writeString(getConsultantName());
		dest.writeString(getHealthIssue());
		dest.writeString(getType());
		dest.writeString(getRemarks());
		dest.writeString(getStatus());
		dest.writeString(this.referrerName);
		dest.writeString(this.referrerClinic);
	}
	
	public static final Parcelable.Creator<ReferralAppointment> CREATOR = new Parcelable.Creator<ReferralAppointment> () {
		public ReferralAppointment createFromParcel (Parcel in) {
			return new ReferralAppointment(in);
		}
		
		public ReferralAppointment[] newArray (int size) {
			return new ReferralAppointment[size];
		}
	};
}
