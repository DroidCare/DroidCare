package com.droidcare.control;

import android.util.Pair;

/**
 * Contains the logic for registering a new patient user.
 * @author Peter
 */

public class RegisterManager {
	/**
	 * An instance of {@link RegisterManager} class. This is based on the Singleton design pattern.
	 */
    private static RegisterManager instance = new RegisterManager();
    
    /**
     * A constant defining the minimum length of a password
     */
    public static final int PASSWORD_MINIMUM_LENGTH = 6;

    /**
     * Returns {@link #instance}
     * @return {@link #instance}
     */
    public static RegisterManager getInstance() {
        return instance;
    }
    
    /**
     * Interface used to allow the {@link SimpleHttpPost}
     * to run some code when it has finished executing.
     */
    public interface OnFinishTaskListener {
        public abstract void onFinishTask(String responseText);
    }
    
    /**
     * Registers a new patient user and creates a new entry in the database
     * @param passportNumber			the new user's passport number
     * @param fullName					the new user's full name
     * @param address					the new user's address
     * @param phoneNumber				the new user's phone number
     * @param country					the new user's country
     * @param email						the new user's email
     * @param dateOfBirth				the new user's date of birth
     * @param gender					the new user's gender
     * @param nationality				the new user's nationality
     * @param password					the new user's password
     * @param notificationTypeString	the new user's preferences of notification means
     * @param onFinishTaskListener		an instance of OnFinishTaskListener determining what to do after the new user has been registered
     */
    public void registerUser(	String passportNumber
    							, String fullName
    							, String address
    							, String phoneNumber
    							, String country
    							, String email
    							, String dateOfBirth
    							, String gender
    							, String nationality
    							, String password
    							, String notificationTypeString
    							, OnFinishTaskListener onFinishTaskListener) {
    	
        new SimpleHttpPost(new Pair<String, String>("passport_number", passportNumber)
                , new Pair<String, String>("full_name", fullName)
                , new Pair<String, String>("address", address)
                , new Pair<String, String>("phone_number", phoneNumber)
                , new Pair<String, String>("location", country)
                , new Pair<String, String>("email", email)
                , new Pair<String, String>("date_of_birth", dateOfBirth)
                , new Pair<String, String>("gender", gender)
                , new Pair<String, String>("nationality", nationality)
                , new Pair<String, String>("password", password)
                , new Pair<String, String>("notification", notificationTypeString)) {
        	
            private OnFinishTaskListener listener;
            public SimpleHttpPost init(OnFinishTaskListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                listener.onFinishTask(responseText);
            }
        }.init(onFinishTaskListener).send(Global.USER_REGISTER_URL);
    }
}
