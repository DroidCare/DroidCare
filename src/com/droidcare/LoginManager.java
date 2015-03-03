package com.droidcare;

import android.util.Pair;

public class LoginManager {
    public final int    EMAIL_EMPTY = 0xA0000001,
                        PASSWORD_EMPTY = 0xA0000002,
                        PASSWORD_TOO_SHORT = 0xA0000003,
                        VALID_FIELDS = 0xA0000000;

    public final int    minLength = 4;

    private static LoginManager instance = new LoginManager();

    private LoginManager() {}

    public static LoginManager getInstance() {
        return instance;
    }

    public static abstract class Request {
        public String email;
        public String password;

        public Request(String email, String password) {
            this.email      = email;
            this.password   = password;
        }

        public abstract void onFinish(String responseText);
    }

    public int verifyFields(String email, String password) {
        return email == null || email.isEmpty() ? EMAIL_EMPTY
                : password == null || password.isEmpty() ? PASSWORD_EMPTY
                : password.length() < minLength ? PASSWORD_TOO_SHORT
                : VALID_FIELDS;
    }

    public void prepareLoginRequest(String url, Request loginRequest) {
        new SimpleHttpPost(new Pair<String, String>("email", loginRequest.email)
                , new Pair<String , String>("password", loginRequest.password)) {
            private Request loginRequest;

            public SimpleHttpPost init(Request loginRequest) {
                this.loginRequest = loginRequest;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                loginRequest.onFinish(responseText);
            }
        }.init(loginRequest).send(url);
    }
}
