package com.droidcare.control;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import com.droidcare.*;
import com.droidcare.control.*;
import com.droidcare.boundary.*;
import com.droidcare.entity.*;

import java.util.HashMap;

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

    public interface OnFinishListener {
        public abstract void onFinish(String responseText);
    }
	
	/**
	 * This method is blocking. Call this method in a new AsyncTask.
	 * @return Returns {@code true} if manager successfully obtained user's details, {@code false} otherwise.
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
						country = params.getString("country"),
						passportNumber = params.getString("passport_number"),
						nationality = params.getString("nationality"),
						dateOfBirth = params.getString("date_of_birth"),
						type = params.getString("type"),
                        gender = params.getString("gender");
				
				user = new User(id, email, fullName, address, country
						, gender, passportNumber, nationality,
						dateOfBirth, type, params.getString("notification"));
				
				return true;
		
		// Do nothing on exception and status != 0
			default:
				break;
			}
		} catch (JSONException e) {
		}
		
		return false;
	}

    public User createUser(int id, String email, String fullName, String address, String country
            , String gender, String passportNumber, String nationality
            , String dateOfBirth, String type, String notification) {

        return user = new User(id, email, fullName, address, country
                , gender, passportNumber, nationality, dateOfBirth, type
                , notification);
    }

    public User createUser() {
        return user = new User();
    }

    public void editProfile(String password, String address, String country, String passportNumber
            , String nationality, String notificationTypeString, OnFinishListener onFinishListener) {
        new SimpleHttpPost(new Pair<String, String>("id", "" + user.getId())
                , new Pair<String, String>("email", user.getEmail())
                , new Pair<String, String>("password", password)
                , new Pair<String, String>("full_name", user.getFullName())
                , new Pair<String, String>("address", address)
                , new Pair<String, String>("country", country)
                , new Pair<String, String>("gender", user.getGender())
                , new Pair<String, String>("passport_number", passportNumber)
                , new Pair<String, String>("nationality", nationality)
                , new Pair<String, String>("date_of_birth", user.getDateOfBirth())
                , new Pair<String, String>("notification", notificationTypeString)
                , new Pair<String, String>("session_id", user.getSessionId())) {
            private OnFinishListener listener;
            private String  address,
            				country,
                            passportNumber,
                            nationality,
                            notification;

            public SimpleHttpPost init(OnFinishListener listener
                    , String address
                    , String country
                    , String passportNumber
                    , String nationality
                    , String notification) {
            	
                this.listener = listener;
                this.address = address;
                this.country = country;
                this.passportNumber = passportNumber;
                this.nationality = nationality;
                this.notification = notification;

                return this;
            }

            @Override
            public void onFinish(String responseText) {
                Log.d("DEBUGGING", "UserManager.OnFinishListener responseText = " + responseText);
                try {
                    JSONObject response = new JSONObject(responseText);
                    switch(response.getInt("status")) {
                        case 0:
                            user.setAddress(address);
                            user.setNationality(nationality);
                            user.setPassportNumber(passportNumber);
                            user.setNotification(notification);
                            break;
                    }

                } catch (JSONException e) {
                }

                listener.onFinish(responseText);
            }
        }.init(onFinishListener, address, country, passportNumber
                , nationality, notificationTypeString).send(Global.USER_UPDATE_URL);
    }
}