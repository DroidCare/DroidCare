package com.droidcare;

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

	public boolean putString(String key, String value) {
		return settings.edit().putString(key, value).commit();
	}
	
	public boolean putInt(String key, int value) {
		return settings.edit().putInt(key, value).commit();
	}
	
	/**
	 * @param key The key.
	 * @return Returns the value if key exists. Else, <i>null</i>.
	 */
	public String getString(String key) {
		return settings.getString(key, null);
	}
	
	/**
	 * @param key The key.
	 * @return Returns the value if key exists. Else, <i>0</i>.
	 */
	public int getInt(String key) {
		return settings.getInt(key, 0);
	}
}