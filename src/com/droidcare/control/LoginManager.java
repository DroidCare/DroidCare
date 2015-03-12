package com.droidcare.control;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginManager {
    public static final int EMAIL_EMPTY = 0xA0000001,
                            PASSWORD_EMPTY = 0xA0000002,
                            PASSWORD_TOO_SHORT = 0xA0000003,
                            VALID_FORM = 0xA0000000;

    public static final int passwordMinLength = 6;

    private static LoginManager instance = new LoginManager();

    private LoginManager() {}

    public static LoginManager getInstance() {
        return instance;
    }

    public int validateForm(String email, String password) {
        return email == null || email.isEmpty() ? EMAIL_EMPTY
                : password == null || password.isEmpty() ? PASSWORD_EMPTY
                : password.length() < passwordMinLength ? PASSWORD_TOO_SHORT
                : VALID_FORM;
    }

    public interface OnFinishTaskListener {
        public abstract void onFinishTask(String responseText);
    }

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
                            Global.getAppSession()
                                    .putString("session_id", messages.getString(0));

                            new SimpleHttpPost(new Pair<String, String>("session_id", Global.getAppSession().getString("session_id"))) {
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
                                                        country = params.getString("location"),
                                                        passportNumber = params.getString("passport_number"),
                                                        nationality = params.getString("nationality"),
                                                        dateOfBirth = params.getString("date_of_birth"),
                                                        type = params.getString("type"),
                                                        gender = params.getString("gender");

                                                Global.getUserManager().createUser(id, email, fullName, address, country, gender
                                                        , passportNumber, nationality, dateOfBirth, type, params.getString("notification"))
                                                        .setSessionId(Global.getAppSession().getString("session_id"));
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
                Global.getAppSession().clearAll();
//                Global.getAppointmentManager().clearRejectedAppointments();
//                Global.getAppointmentManager().clearPendingAppointments();
//                Global.getAppointmentManager().clearFinishedAppointments();
//                Global.getAppointmentManager().clearAcceptedAppointments();

                listener.onFinishTask(responseText);
            }
        }.init(onFinishTaskListener).send(Global.USER_LOGOUT_URL);
    }

    public void checkLogin(OnFinishTaskListener onFinishTaskListener) {
        new SimpleHttpPost(new Pair<String, String>("session_id"
                , Global.getAppSession().getString("session_id"))) {
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
                                    country = params.getString("location"),
                                    passportNumber = params.getString("passport_number"),
                                    nationality = params.getString("nationality"),
                                    dateOfBirth = params.getString("date_of_birth"),
                                    type = params.getString("type"),
                                    gender = params.getString("gender");

                            Global.getUserManager().createUser(id, email, fullName, address, country
                                    , gender, passportNumber, nationality, dateOfBirth, type, params.getString("notification"))
                                    .setSessionId(Global.getAppSession().getString("session_id"));
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
