package com.droidcare;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ReferralAppointment extends Appointment {
	private String referrerName;
	private String referrerClinic;
	
	public ReferralAppointment (int id, int patientId, int consultantId, String dateTime
			, String patientName, String consultantName, String healthIssue
			, String type, int previousId, String remarks, String status, String referrerName
			, String referrerClinic) {
		super(id, patientId, consultantId, dateTime, patientName, consultantName, healthIssue, type, previousId, remarks, status);
		this.referrerName = referrerName;
		this.referrerClinic = referrerClinic;
	}
	
	public String getReferrerName () {
		return this.referrerName;
	}
	
	public String getReferrerClinic () {
		return this.referrerClinic;
	}
	
	public void setReferrerName (String referrerName) {
		this.referrerName = referrerName;
	}
	
	public void setReferrerClinic (String referrerClinic) {
		this.referrerClinic = referrerClinic;
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public ReferralAppointment (Parcel in) {
		super(in.readInt(), in.readInt(), in.readInt(), Global.dateFormat.format(new Date(in.readLong())), 
				in.readString(), in.readString(), in.readString() ,in.readString(),
				in.readInt(), in.readString(), in.readString());
		
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
		dest.writeInt(getPreviousId());
		dest.writeLong(getDateTimeMillis());
		dest.writeString(getPatientName());
		dest.writeString(getConsultantName());
		dest.writeString(getHealthIssue());
		dest.writeString(getType());
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
