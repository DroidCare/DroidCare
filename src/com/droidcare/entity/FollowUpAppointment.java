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
 * Stores detailed information of a follow-up appointment
 */

public class FollowUpAppointment extends Appointment {
	/**
	 * Base64 encoded String of the proof image
	 */
	private String attachment;
	
	/**
	 * The ID of the previous {@link Appointment} object.
	 */
	private int previousId;

	/**
	 * Constructs a {@link FollowUpAppointment} object.
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
	 * @param attachment		Base64 encoded String of the proof image
	 * @param previousId		the ID of the previous {@link Appointment} object
	 */
	public FollowUpAppointment (int id, int patientId, int consultantId, String dateTime
			, String patientName, String consultantName, String healthIssue
			, String type, String remarks, String status, String attachment, int previousId) {
		super(id, patientId, consultantId, dateTime, patientName, consultantName, healthIssue, type, remarks, status);
		this.attachment = attachment;
		this.previousId = previousId;
	}
	
	/**
	 * Returns a Base64 encoded String of the proof image
	 * @return Base64 encoded String of the proof image
	 */
	public String getAttachment () {
		if (this.attachment.isEmpty()) {
			Global.getAppointmentManager().fetchAttachment(this, new AppointmentManager.OnFinishListener() {
                @Override
                public void onFinish(String responseText) {
                    // Do nothing
                }
            });
		}
		
		return this.attachment;
	}
	
	/**
	 * Returns the ID of the previous {@link Appointment} object
	 * @return the ID of the previous {@link Appointment} object
	 */
	public int getPreviousId () {
		return this.previousId;
	}
	
	/**
	 * Sets the Base64 encoded String of the proof image
	 * @param attachment the Base64 encoded String of the proof image
	 */
	public void setAttachment (String attachment) {
		this.attachment = attachment;
	}
	
	/**
	 * Sets the ID of the previous {@link Appointment} object
	 * @param id the ID of the previous {@link Appointment} object
	 */
	public void setPreviousId (int id) {
		this.previousId = id;
	}
	
	// PARCELABLE IMPLEMENTATION -> provide a way to send an object from one activity / fragment to another
	public FollowUpAppointment (Parcel in) {
		super(in.readInt(), in.readInt(), in.readInt(), Global.dateFormat.format(new Date(in.readLong())), 
				in.readString(), in.readString(), in.readString() ,in.readString(),
				in.readString(), in.readString());

		this.attachment = in.readString();
		this.previousId = in.readInt();
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
		dest.writeString(this.attachment);
		dest.writeInt(this.previousId);
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
