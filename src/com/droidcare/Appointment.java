package com.droidcare;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Appointment implements Parcelable{
	private int	id,
				patientId,
				consultantId,
				previousId;
	
	// To make it easier to "parcelize"
	private long dateTimeMillis;

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
		return this.id;
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
		this.type = in.readString();
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
		dest.writeString(this.type);
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
