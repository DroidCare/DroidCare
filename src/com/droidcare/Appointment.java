package com.droidcare;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Appointment implements Parcelable{
	/**
	 * Appointment status.
	 */
	public static int	PENDING = 0,
						ACCEPTED = 1,
						REJECTED = 2,
						FINISHED = 3;
	
	/**
	 * Appointment type.
	 */
	public static int	FOLLOW_UP = 0xA0000000,
						REFERRAL = 0xA0000001,
						NORMAL = 0xA0000002;
	
	private int	id,
				patientId,
				consultantId,
				previousId,
				status,
				type;
	
	// To make it easier to "parcelize"
	private long dateTimeMillis;

	private String	healthIssue,
					attachmentPath,
					referrerName,
					referrerClinic,
					remarks;
	
	// public Appointment(){} // An empty constructor
	
	public Appointment(int id, int patientId, int consultantId, String dateTime
			, String healthIssue, String attachmentPath, String type, String referrerName
			, String referrerClinic, int previousId, String remarks, String status){
		this.id				= id;
		this.patientId		= patientId;
		this.consultantId	= consultantId;
		this.previousId		= previousId;
		
		this.healthIssue	= healthIssue;
		this.attachmentPath = attachmentPath;
		this.referrerName	= referrerName;
		this.referrerClinic = referrerClinic;
		
		this.type = type.equals("follow-up") ? FOLLOW_UP
				: type.equals("referrral") ? REFERRAL : NORMAL;
		
		this.status = status.equals("pending") ? PENDING
				: status.equals("accepted") ? ACCEPTED
				: status.equals("rejected") ? REJECTED : FINISHED;
		
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
	
	public int getStatus() {
		return status;
	}
	
	public long getDateTimeMillis() {
		return dateTimeMillis;
	}
	
	public String getHealthIssue() {
		return healthIssue;
	}
	
	public String getAttachmentPath() {
		return attachmentPath;
	}
	
	public String getReferrerName() {
		return referrerName;
	}
	
	public String getReferrerClinic() {
		return referrerClinic;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public int getType() {
		return type;
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public Appointment (Parcel in) {
		this.id = in.readInt();
		this.patientId = in.readInt();
		this.consultantId = in.readInt();
		this.previousId = in.readInt();
		this.dateTimeMillis = in.readLong();
		this.healthIssue = in.readString();
		this.referrerName = in.readString();
		this.referrerClinic = in.readString();		
		this.type = in.readInt();
		this.attachmentPath = in.readString();
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
		dest.writeString(this.healthIssue);
		dest.writeString(this.referrerName);
		dest.writeString(this.referrerClinic);		
		dest.writeInt(this.type);
		dest.writeString(this.attachmentPath);
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
