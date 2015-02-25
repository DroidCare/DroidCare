package com.droidcare;

import java.util.ArrayList;

public class AppointmentManager {
	private ArrayList<Appointment> allAppointments;
	private ArrayList<Appointment> upcomingAppointments;
	private ArrayList<Appointment> pendingAppointments;
	
	public AppointmentManager () {
		this.allAppointments = new ArrayList<Appointment> ();
		this.upcomingAppointments = new ArrayList<Appointment> ();
		this.pendingAppointments = new ArrayList<Appointment> ();
	}
	
	public void addAppointment (Appointment appointment) {
		this.allAppointments.add(appointment);
	}
	
	public ArrayList<Appointment> getAllAppointments () {
		return this.allAppointments;
	}
	
	public void addUpcomingAppointment (Appointment appointment) {
		this.upcomingAppointments.add(appointment);
	}
	
	public ArrayList<Appointment> getUpcomingAppointments () {
		return this.upcomingAppointments;
	}
	
	public void addPendingAppointment (Appointment appointment) {
		this.pendingAppointments.add(appointment);
	}
	
	public ArrayList<Appointment> getPendingAppointments () {
		return this.pendingAppointments;
	}
	
	public void clearAllAppointments () {
		this.allAppointments.clear();
	}
	
	public void clearUpcomingAppointments () {
		this.upcomingAppointments.clear();
	}
	
	public void clearPendingAppointments () {
		this.pendingAppointments.clear();
	}
}
