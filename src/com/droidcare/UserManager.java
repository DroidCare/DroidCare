package com.droidcare;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

public class UserManager {
	
	// Force singleton
	private static UserManager instance = new UserManager();
	
	private User user;
	
	private UserManager() {
		this.user = null;
	}
	
	public static UserManager getInstance() {
		return instance;
	}
	
	public User getUser() {
		return user;
	}
	
	/**
	 * Call this method when user is logging out from the app.
	 */
	public void removeUser() {
		user = null;
	}
	
	/**
	 * This method is blocking. Call this method in a new AsyncTask.
	 * @return Returns <i>true</i> if manager successfully obtained user's details.
	 */
	public boolean fetchUserDetails() {
		String sessionId = Global.getAppSession().getString("session_id");
		if(sessionId == null){
			// Unsuccessful
			return false;
		}

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("session_id", sessionId);
		
		String responseText = new HttpPostRequest(data).send(Global.USER_URL);
		
		int status = -1;
		try {
			JSONObject response = new JSONObject(responseText);
			
			status = response.getInt("status");
			switch(status) {
			
			case 0:
				JSONObject params = response.getJSONObject("message");
				
				int		id = params.getInt("id");
				
				String	email = params.getString("email"),
						fullName = params.getString("full_name"),
						address = params.getString("address"),
						passportNumber = params.getString("passport_number"),
						nationality = params.getString("nationality"),
						dateOfBirth = params.getString("date_of_birth"),
						type = params.getString("type"),
                        gender = params.getString("gender");
				
				user = new User(id, email, fullName, address
						, gender, passportNumber, nationality,
						dateOfBirth, type);
				
				return true;
		
		// Do nothing on exception and status != 0
			default:
				break;
			}
		} catch (JSONException e) {
		}
		
		return false;
	}

    public User createUser(int id, String email, String fullName, String address
            , String gender, String passportNumber, String nationality
            , String dateOfBirth, String type) {

        return user = new User(id, email, fullName, address
                , gender, passportNumber, nationality, dateOfBirth, type);
    }

    public User createUser() {
        return user = new User();
    }
}