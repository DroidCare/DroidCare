package com.droidcare;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

interface OnFinishLoginListener {
    public abstract void onFinishTask(String responseText);
}

public class LoginManager {
    public static final int EMAIL_EMPTY = 0xA0000001,
                            PASSWORD_EMPTY = 0xA0000002,
                            PASSWORD_TOO_SHORT = 0xA0000003,
                            VALID_FORM = 0xA0000000;

    public final int minLength = 4;

    private static LoginManager instance = new LoginManager();

    private LoginManager() {}

    public static LoginManager getInstance() {
        return instance;
    }

    public int validateForm(String email, String password) {
        return email == null || email.isEmpty() ? EMAIL_EMPTY
                : password == null || password.isEmpty() ? PASSWORD_EMPTY
                : password.length() < minLength ? PASSWORD_TOO_SHORT
                : VALID_FORM;
    }

    public void doLoginRequest(String email, String password, OnFinishLoginListener onFinishLoginListener) {
        new SimpleHttpPost(new Pair<String, String>("email", email)
                , new Pair<String, String>("password", password)) {
            private OnFinishLoginListener listener;
            public SimpleHttpPost init(OnFinishLoginListener listener) {
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
                            Log.d("UNIT_TEST", "session_id = " + messages.getString(0));
                            Global.getAppSession()
                                    .putString("session_id", messages.getString(0));
                            break;
                        default:
                            break;
                    }
                // Do nothing on exception
                } catch (JSONException e) {
                }

                listener.onFinishTask(responseText);
            }
        }.init(onFinishLoginListener).send(Global.USER_LOGIN_URL);
    }

    public void doLogoutRequest(OnFinishLoginListener onFinishLoginListener) {
        new SimpleHttpPost(new Pair<String, String>("session_id"
//                , Global.getUserManager().getUser().getSessionId())) {
                , Global.getAppSession().getString("session_id"))) {
            private OnFinishLoginListener listener;
            public SimpleHttpPost init(OnFinishLoginListener listener) {
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
        }.init(onFinishLoginListener).send(Global.USER_LOGIN_URL);
    }
}
