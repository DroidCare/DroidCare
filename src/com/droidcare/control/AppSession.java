package com.droidcare.control;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {
	
	// Force singleton
	private static AppSession instance = new AppSession();
	
	private SharedPreferences settings;
	
	private AppSession() {}
	
	public static AppSession getInstance() {
		return instance;
	}
	
	/**
	 * This method should only be called once.
	 * @param context Android context.
	 */
	public static void init(Context context) {
		getInstance().settings = context.getSharedPreferences(Global.APP_NAME, Context.MODE_PRIVATE);
	}

	public boolean storeSessionId(String value) {
		return settings.edit().putString("session_id", value).commit();
	}
	
	/**
	 * @param key The key.
	 * @return Returns the value if key exists. Else, <i>null</i>.
	 */
	public String retrieveSessionId() {
		return settings.getString("session_id", null);
	}
	
	/**
	 * @return Returns <i>true</i> if successful.
	 */
	public boolean clearAll() {
		return settings.edit().clear().commit();
	}
	
	/**
	 * @param key The key.
	 * @return Returns <i>true</i> if successful.
	 */
	public boolean clear(String key) {
		return settings.edit().remove(key).commit();
	}
	
	public boolean contains(String key) {
		return settings.contains(key);
	}
}