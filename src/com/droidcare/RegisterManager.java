package com.droidcare;

import android.util.Pair;

public class RegisterManager {
    private static RegisterManager instance = new RegisterManager();
    public static final int passwordMinLength = 6;

    private RegisterManager() {}

    public static RegisterManager getInstance() {
        return instance;
    }

    public interface OnFinishTaskListener {
        public abstract void onFinishTask(String responseText);
    }

    public void registerUser(	String passportNumber
    							, String fullName
    							, String address
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
