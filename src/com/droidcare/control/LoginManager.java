package com.droidcare.control;

import android.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This singleton class handles all user login and logout request.
 * @author Peter
 */

public class LoginManager {
    public static final int EMAIL_EMPTY = 0xA0000001,
                            PASSWORD_EMPTY = 0xA0000002,
                            PASSWORD_TOO_SHORT = 0xA0000003,
                            VALID_FORM = 0xA0000000,
    						PASSWORD_MINIMUM_LENGTH = 6;
    
    /**
     * An instance of {@link LoginManager}.
     */
    private static LoginManager instance = new LoginManager();
    
    /**
     * Obtains the singleton manager.
     * @return LoginManager.
     */
    public static LoginManager getInstance() {
        return instance;
    }
    
    /**
     * Validates login form which contains email and password.
     * @param email Email.
     * @param password Password.
     * @return Returns the validation status in integer
     * (either {@link #EMAIL_EMPTY}, {@link #PASSWORD_EMPTY}, {@link #PASSWORD_MINIMUM_LENGTH}, {@link #PASSWORD_TOO_SHORT}, or {@link #VALID_FORM}
     */
    public int validateForm(String email, String password) {
        return email == null || email.isEmpty() ? EMAIL_EMPTY
                : password == null || password.isEmpty() ? PASSWORD_EMPTY
                : password.length() < PASSWORD_MINIMUM_LENGTH ? PASSWORD_TOO_SHORT
                : VALID_FORM;
    }
    
    /**
     * Interface used to allow the {@link SimpleHttpPost}
     * to run some code when it has finished executing.
     */
    public interface OnFinishTaskListener {
    	/**
         * This method will be invoked when a {@link SimpleHttpPost} has finished.
         * @param responseText Response text from HTTP Response.
         */
        public abstract void onFinishTask(String responseText);
    }
    
    /**
     * Send a login request to the server and process the response from the server.
     * @param email User's email.
     * @param password User's password.
     * @param onFinishTaskListener Interface.
     */
    public void doLoginRequest(String email, String password, OnFinishTaskListener onFinishTaskListener) {
        new SimpleHttpPost(new Pair<String, String>("email", email)
                , new Pair<String, String>("password", password)) {
            private OnFinishTaskListener listener;
            public SimpleHttpPost init(OnFinishTaskListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                try {
                    JSONObject response = new JSONObject(responseText);
                    JSONArray messages = response.getJSONArray("message");
                    switch(response.getInt("status")) {
                        case 0:
                            Global.getAppSessionManager()
                                    .storeSessionId(messages.getString(0));

                            new SimpleHttpPost(new Pair<String, String>("session_id", Global.getAppSessionManager().retrieveSessionId())) {
                                private OnFinishTaskListener listener;
                                public SimpleHttpPost init(OnFinishTaskListener listener) {
                                    this.listener = listener;
                                    return this;
                                }
                                @Override
                                public void onFinish(String responseText) {
//                                    Log.d("DEBUGGING", "sessionId = " + Global.getAppSession().getString("session_id"));
                                    try {
                                        JSONObject response = new JSONObject(responseText);
                                        switch(response.getInt("status")) {
                                            case 0:
                                                JSONObject params = response.getJSONObject("message");

                                                int id = params.getInt("id");

                                                String email = params.getString("email"),
                                                        fullName = params.getString("full_name"),
                                                        address = params.getString("address"),
                                                        phoneNumber = params.getString("phone_number"),
                                                        country = params.getString("location"),
                                                        passportNumber = params.getString("passport_number"),
                                                        nationality = params.getString("nationality"),
                                                        dateOfBirth = params.getString("date_of_birth"),
                                                        type = params.getString("type"),
                                                        gender = params.getString("gender");

                                                Global.getUserManager().createUser(id, email, fullName, address, phoneNumber, country, gender
                                                        , passportNumber, nationality, dateOfBirth, type, params.getString("notification"))
                                                        .setSessionId(Global.getAppSessionManager().retrieveSessionId());
                                                break;
                                            default:
                                                break;
                                        }
                                    // Do nothing on exception
                                    } catch (JSONException e) {
                                    }

                                    listener.onFinishTask(responseText);
                                }
                            }.init(listener).send(Global.USER_URL);

                            return;

                        default:
                            listener.onFinishTask(responseText);
                            return;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }

                listener.onFinishTask("error");
            }
        }.init(onFinishTaskListener).send(Global.USER_LOGIN_URL);
    }
    
    /**
     * Send a logout request to the server and process the response from the server.
     * @param onFinishTaskListener Interface {@link OnFinishTaskListener OnFinishTaskListener}.
     */
    public void doLogoutRequest(OnFinishTaskListener onFinishTaskListener) {
        new SimpleHttpPost(new Pair<String, String>("session_id"
                , Global.getUserManager().getUser().getSessionId())) {
            private OnFinishTaskListener listener;
            public SimpleHttpPost init(OnFinishTaskListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                Global.getUserManager().removeUser();
                Global.getAppSessionManager().clearSession();
                Global.firstInitialization = false;
                AppointmentManager.removeManager();

                listener.onFinishTask(responseText);
            }
        }.init(onFinishTaskListener).send(Global.USER_LOGOUT_URL);
    }
    
    /**
     * Check if a login session exists.
     * @param onFinishTaskListener Interface.
     */
    public void checkLogin(OnFinishTaskListener onFinishTaskListener) {
        new SimpleHttpPost(new Pair<String, String>("session_id"
                , Global.getAppSessionManager().retrieveSessionId())) {
            private OnFinishTaskListener listener;
            public SimpleHttpPost init(OnFinishTaskListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            JSONObject params = response.getJSONObject("message");

                            int		id = params.getInt("id");

                            String	email = params.getString("email"),
                                    fullName = params.getString("full_name"),
                                    address = params.getString("address"),
                                    phoneNumber = params.getString("phone_number"),
                                    country = params.getString("location"),
                                    passportNumber = params.getString("passport_number"),
                                    nationality = params.getString("nationality"),
                                    dateOfBirth = params.getString("date_of_birth"),
                                    type = params.getString("type"),
                                    gender = params.getString("gender");

                            Global.getUserManager().createUser(id, email, fullName, address, phoneNumber, country
                                    , gender, passportNumber, nationality, dateOfBirth, type, params.getString("notification"))
                                    .setSessionId(Global.getAppSessionManager().retrieveSessionId());
                            break;
                        default:
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }
                listener.onFinishTask(responseText);
            }
        }.init(onFinishTaskListener).send(Global.USER_URL);
    }
    
    /**
     * Send a forget password request to the server and process the response from the server.
     * @param email Email.
     * @param onFinishTaskListener Listener.
     */
    public void forgetPasswordRequest(String email, OnFinishTaskListener onFinishTaskListener) {
        new SimpleHttpPost(new Pair<String, String> ("email", email)) {
            private OnFinishTaskListener listener;
            public SimpleHttpPost init(OnFinishTaskListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                listener.onFinishTask(responseText);
            }
        }.init(onFinishTaskListener).send(Global.USER_FORGET_URL);
    }
}
