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

    public void registerUser(OnFinishTaskListener onFinishTaskListener
            , Pair<String, String>... data) {
        new SimpleHttpPost(data) {
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
