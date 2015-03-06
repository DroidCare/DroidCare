package com.droidcare;

public class User{
	private int		id;
	
	private String	email,
					fullName,
					address,
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
	public User(int id, String email, String fullName, String address
			, String gender, String passportNumber, String nationality
			, String dateOfBirth, String type, String notification){
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.address = address;
		this.gender = gender;
		this.passportNumber = passportNumber;
		this.nationality = nationality;
		this.dateOfBirth = dateOfBirth;
		this.type = type;
        this.notification = notification;
	}
	
	public void setData(int id, String email, String fullName, String address
			, String gender, String passportNumber, String nationality
			, String dateOfBirth, String type, String notification){
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.address = address;
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
	
	public void setPassportNumber(String passportNumber){
		this.passportNumber = passportNumber;
	}
	
	public void setNationality(String nationality){
		this.nationality = nationality;
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
	
	public int getId(){
		return id;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return sessionId;
	}

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}