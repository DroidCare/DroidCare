package com.droidcare.entity;

public class User{
	private int		id;
	
	private String	email,
					fullName,
					address,
					country,
					passportNumber,
					nationality,
					dateOfBirth,
					type,
					sessionId,
                    gender,
                    notification;
	
	// Empty constructor
	public User(){}
	
	// Lengthy constructor
	public User(int id, String email, String fullName, String address, String country
			, String gender, String passportNumber, String nationality
			, String dateOfBirth, String type, String notification){
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.address = address;
		this.country = country;
		this.gender = gender;
		this.passportNumber = passportNumber;
		this.nationality = nationality;
		this.dateOfBirth = dateOfBirth;
		this.type = type;
        this.notification = notification;
	}
	
	public void setData(int id, String email, String fullName, String address, String country
			, String gender, String passportNumber, String nationality
			, String dateOfBirth, String type, String notification){
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.address = address;
		this.country = country;
		this.gender = gender;
		this.passportNumber = passportNumber;
		this.nationality = nationality;
		this.dateOfBirth = dateOfBirth;
		this.type = type;
        this.notification = notification;
	}
	
	// Most attributes are immutable
	// such as email, full name, gender, date of birth, type
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public void setPassportNumber(String passportNumber){
		this.passportNumber = passportNumber;
	}
	
	public void setNationality(String nationality){
		this.nationality = nationality;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

    public void setNotification(String notification) {
        this.notification = notification;
    }
	
	public int getId(){
		return id;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getFullName(){
		return fullName;
	}
	
	public String getAddress(){
		return address;
	}
	
	public String getCountry(){
		return country;
	}
	
	public String getPassportNumber(){
		return passportNumber;
	}
	
	public String getNationality(){
		return nationality;
	}
	
	public String getDateOfBirth(){
		return dateOfBirth;
	}
	
	public String getType(){
		return type;
	}
	
	public String getGender(){
		return gender;
	}
	
	public String getSessionId() {
		return sessionId;
	}

    public String getNotification() {
        return notification;
    }
}