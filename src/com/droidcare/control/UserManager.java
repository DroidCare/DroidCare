package com.droidcare.control;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import java.util.HashMap;

/**
 * Manages the current user of the application.
 * @author Edwin Candinegara
 */

public class UserManager {
	/**
	 * An instance of {@link UserManager}. This class applies the Singleton design pattern
	 */
	private static UserManager instance = new UserManager();
	
	/**
	 * An instance of {@link User} class which stores information of the current application user
	 */
	private User user;
	
	/**
	 * Constructs a {@link UserManager} object
	 */
	private UserManager() {
		this.user = null;
	}
	
	/**
	 * Returns {@link #instance}
	 * @return {@link #instance}
	 */
	public static UserManager getInstance() {
		return instance;
	}
	
	/**
	 * Returns {@link #user}
	 * @return {@link #user}
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Removes the current {@link User} instance in {@link #user}
	 */
	public void removeUser() {
		user = null;
	}

    public interface OnFinishListener {
        public abstract void onFinish(String responseText);
    }
    
    /**
     * Creates a new {@link User} object based on the given data
     * @param id				the user's ID
     * @param email				the user's email
     * @param fullName			the user's full name
     * @param address			the user's address
     * @param phoneNumber		the user's phone number
     * @param country			the user's country
     * @param gender			the user's gender
     * @param passportNumber	the user's passport number
     * @param nationality		the user's nationality
     * @param dateOfBirth		the user's date of birth
     * @param type				the user's type
     * @param notification		the user's notification means preferences
     * @return					the created {@link User} object
     */
    public User createUser(int id, String email, String fullName, String address, String phoneNumber, String country
            , String gender, String passportNumber, String nationality
            , String dateOfBirth, String type, String notification) {

        return user = new User(id, email, fullName, address, phoneNumber, country
                , gender, passportNumber, nationality, dateOfBirth, type
                , notification);
    }
    
    /**
     * Edit the profile information of the current application user
     * @param password					the user's new password
     * @param address					the user's new address
     * @param phoneNumber				the user's phone number
     * @param country					the user's new country
     * @param passportNumber			the user's new passport number
     * @param nationality				the user's new nationality
     * @param notificationTypeString	the user's new notification means preferences
     * @param onFinishListener			an instance of OnFinishListener determining what to do after the user's profile is updated
     */
    public void editProfile(String password, String address, String phoneNumber, String country, String passportNumber
            , String nationality, String notificationTypeString, OnFinishListener onFinishListener) {
        new SimpleHttpPost(new Pair<String, String>("id", "" + user.getId())
                , new Pair<String, String>("email", user.getEmail())
                , new Pair<String, String>("password", password)
                , new Pair<String, String>("full_name", user.getFullName())
                , new Pair<String, String>("address", address)
                , new Pair<String, String>("phone_number", phoneNumber)
                , new Pair<String, String>("location", country)
                , new Pair<String, String>("gender", user.getGender())
                , new Pair<String, String>("passport_number", passportNumber)
                , new Pair<String, String>("nationality", nationality)
                , new Pair<String, String>("date_of_birth", user.getDateOfBirth())
                , new Pair<String, String>("notification", notificationTypeString)
                , new Pair<String, String>("session_id", user.getSessionId())) {
            private OnFinishListener listener;
            private String  address,
            				phoneNumber,
            				country,
                            passportNumber,
                            nationality,
                            notification;

            public SimpleHttpPost init(OnFinishListener listener
                    , String address
                    , String phoneNumber
                    , String country
                    , String passportNumber
                    , String nationality
                    , String notification) {
            	
                this.listener = listener;
                this.address = address;
                this.phoneNumber = phoneNumber;
                this.country = country;
                this.passportNumber = passportNumber;
                this.nationality = nationality;
                this.notification = notification;

                return this;
            }

            @Override
            public void onFinish(String responseText) {
                Log.d("DEBUGGING", "UserManager.OnFinishListener responseText = " + responseText);
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            user.setAddress(address);
                            user.setPhoneNumbr(phoneNumber);
                            user.setNationality(nationality);
                            user.setPassportNumber(passportNumber);
                            user.setNotification(notification);
                            user.setCountry(country);
                            break;
                    }

                } catch (JSONException e) {
                }

                listener.onFinish(responseText);
            }
        }.init(onFinishListener, address, phoneNumber, country, passportNumber
                , nationality, notificationTypeString).send(Global.USER_UPDATE_URL);
    }
}