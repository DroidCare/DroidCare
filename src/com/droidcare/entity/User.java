package com.droidcare.entity;

/**
 * An entity class which contains user details.
 */
public class User{
	private int		id;
	
	private String	email,
					fullName,
					address,
					phoneNumber,
					country,
					passportNumber,
					nationality,
					dateOfBirth,
					type,
					sessionId,
                    gender,
                    notification;

    /**
     * An empty constructor.
     */
	public User(){}

    /**
     * Constructor.
     * @param id Unique ID of user.
     * @param email Email.
     * @param fullName Full name.
     * @param address Full address.
     * @param phoneNumber the user's phone number.
     * @param country Current location (country).
     * @param gender Gender.
     * @param passportNumber Passport number.
     * @param nationality Nationality.
     * @param dateOfBirth Date of birth.
     * @param type Type.
     * @param notification Notification preferences.
     */
	public User(int id, String email, String fullName, String address, String phoneNumber, String country
			, String gender, String passportNumber, String nationality
			, String dateOfBirth, String type, String notification){
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.country = country;
		this.gender = gender;
		this.passportNumber = passportNumber;
		this.nationality = nationality;
		this.dateOfBirth = dateOfBirth;
		this.type = type;
        this.notification = notification;
	}

    /**
     * <span style="color:red;">DEPRECATED.</span>
     * This method is used for initialization in case if an empty constructor {@link #User()} is used instead.
     * @param id Unique ID of user.
     * @param email Email.
     * @param fullName Full name.
     * @param address Full address.
     * @param phoneNumber the user's phone number.
     * @param country Current location (country).
     * @param gender Gender.
     * @param passportNumber Passport number.
     * @param nationality Nationality.
     * @param dateOfBirth Date of birth.
     * @param type Type.
     * @param notification Notification preferences.
     */
	public void setData(int id, String email, String fullName, String address, String phoneNumber, String country
			, String gender, String passportNumber, String nationality
			, String dateOfBirth, String type, String notification){
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.address = address;
		this.phoneNumber = phoneNumber;
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

    /**
     * Change the address of the user.<br /><br />
     * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param address String. New address of the user.
     */
	public void setAddress(String address){
		this.address = address;
	}
	
	/**
	 * Change the phone number of the user. <br /><br />
	 * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param phoneNumber String. New phone number of the user.
	 */
	public void setPhoneNumbr(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

    /**
     * Change the location (country) of the user.<br /><br />
     * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param country String. New location (country) of the user.
     */
	public void setCountry(String country) {
		this.country = country;
	}

    /**
     * Change the passport number of the user.<br /><br />
     * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param passportNumber String. New passport number of the user.
     */
	public void setPassportNumber(String passportNumber){
		this.passportNumber = passportNumber;
	}

    /**
     * Change the nationality of the user.<br /><br />
     * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param nationality String. New nationality of the user.
     */
	public void setNationality(String nationality){
		this.nationality = nationality;
	}

    /**
     * Change the session ID of the user.<br /><br />
     * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param sessionId String. New session ID of the user.
     */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

    /**
     * Change the notification preferences of the user.<br /><br />
     * <p style="color:red;">Most attributes such as email, full name, gender, date of birth, type, are immutable!</p>
     * @param notification String. Notification preferences of the user.
     */
    public void setNotification(String notification) {
        this.notification = notification;
    }

    /**
     * Returns unique ID of the user.
     * @return int. Unique User ID.
     */
	public int getId(){
		return id;
	}

    /**
     * Returns email of the user.
     * @return String. Email.
     */
	public String getEmail(){
		return email;
	}

    /**
     * Returns full name of the user.
     * @return String. Full name.
     */
	public String getFullName(){
		return fullName;
	}

    /**
     * Returns the address of the user.
     * @return String. Address.
     */
	public String getAddress(){
		return address;
	}
	
	/**
	 * Returns the phone number of the user.
	 * @return String. Phone number.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

    /**
     * Returns the location (country) of the user.
     * @return String. Location (country).
     */
	public String getCountry(){
		return country;
	}

    /**
     * Returns passport number of the user.
     * @return String. Passport number.
     */
	public String getPassportNumber(){
		return passportNumber;
	}

    /**
     * Returns the nationality of the user.
     * @return String. Nationality.
     */
	public String getNationality(){
		return nationality;
	}

    /**
     * Returns user's date of birth.
     * @return String. Date of birth.
     */
	public String getDateOfBirth(){
		return dateOfBirth;
	}

    /**
     * Returns user's type.
     * @return String. Type.
     */
	public String getType(){
		return type;
	}

    /**
     * Returns user's gender.
     * @return String. Gender.
     */
	public String getGender(){
		return gender;
	}

    /**
     * Returns login session ID.
     * @return String. Session ID.
     */
	public String getSessionId() {
		return sessionId;
	}

    /**
     * Returns user's notification preferences.
     * @return String. Notification preferences.
     */
    public String getNotification() {
        return notification;
    }
}