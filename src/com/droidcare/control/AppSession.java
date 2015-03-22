package com.droidcare.control;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author Edwin Candinegara
 * Handles and manages the user's current app session.
 *
 */

public class AppSession {
	
	/**
	 * An instance of {@link AppSession} class. This applies the Singleton design pattern
	 */
	private static AppSession instance = new AppSession();
	
	/**
	 * The Android shared preferences which stores the application session
	 */
	private SharedPreferences settings;
	
	/**
	 * Returns {@link #instance}
	 * @return {@link #instance}
	 */
	public static AppSession getInstance() {
		return instance;
	}
	
	/**
	 * Initializes the {@link #settings}
	 * @param context the context of the application
	 */
	public static void init(Context context) {
		getInstance().settings = context.getSharedPreferences(Global.APP_NAME, Context.MODE_PRIVATE);
	}
	
	/**
	 * Stores the current session ID
	 * @param value	the session ID
	 * @return		{@code true} if successful, {@code false} otherwise
	 */
	public boolean storeSessionId(String value) {
		return settings.edit().putString("session_id", value).commit();
	}
	
	/**
	 * Retrieves the stored session ID
	 * @return the session ID
	 */
	public String retrieveSessionId() {
		return settings.getString("session_id", null);
	}
	
	/**
	 * Clears all preferences in {@link #settings}
	 * @return {@code true} if successful, {@code false} otherwise
	 */
	public boolean clearAll() {
		return settings.edit().clear().commit();
	}
}