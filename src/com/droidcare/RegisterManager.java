package com.droidcare;

import android.util.Pair;

interface OnFinishRegisterListener {
    public abstract void onFinishRegister(String responseText);
}

public class RegisterManager {
    private static RegisterManager instance = new RegisterManager();
    public static final int passwordMinLength = 6;

    private RegisterManager() {}

    public static RegisterManager getInstance() {
        return instance;
    }

    public void registerUser(OnFinishRegisterListener onFinishRegisterListener
            , Pair<String, String>... data) {
        new SimpleHttpPost(data) {
            private OnFinishRegisterListener listener;
            public SimpleHttpPost init(OnFinishRegisterListener listener) {
                this.listener = listener;
                return this;
            }

            @Override
            public void onFinish(String responseText) {
                listener.onFinishRegister(responseText);
            }
        }.init(onFinishRegisterListener).send(Global.USER_REGISTER_URL);
    }
}
