package com.droidcare;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class FollowUpAppointment extends Appointment {
	private String attachment;

	public FollowUpAppointment (int id, int patientId, int consultantId, String dateTime
			, String patientName, String consultantName, String healthIssue
			, String type, int previousId, String remarks, String status, String attachment) {
		super(id, patientId, consultantId, dateTime, patientName, consultantName, healthIssue, type, previousId, remarks, status);
		this.attachment = attachment;
	}
	
	public String getAttachment () {
		return this.attachment;
	}
	
	public void setAttachment (String attachment) {
		this.attachment = attachment;
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public FollowUpAppointment (Parcel in) {
		super(in.readInt(), in.readInt(), in.readInt(), Global.dateFormat.format(new Date(in.readLong())), 
				in.readString(), in.readString(), in.readString() ,in.readString(),
				in.readInt(), in.readString(), in.readString());
		
		this.attachment = in.readString();
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
		dest.writeString(this.attachment);
	}
	
	public static final Parcelable.Creator<FollowUpAppointment> CREATOR = new Parcelable.Creator<FollowUpAppointment> () {
		public FollowUpAppointment createFromParcel (Parcel in) {
			return new FollowUpAppointment(in);
		}
		
		public FollowUpAppointment[] newArray (int size) {
			return new FollowUpAppointment[size];
		}
	};
}
