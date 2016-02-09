package com.example.taapesh.androidrestaurant.util;

import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;


public class PreferencesManager {

    public static final String MY_PREFERENCES = "preferences";
    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void clearPreferences(Context context) {
        final SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear().apply();
    }

    public void setUserDetails(JSONObject details) {
        try {
            final SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putInt(USER_ID, details.getInt("user_id"));
            prefsEditor.putString(TOKEN, details.getString("auth_token"));
            prefsEditor.putString(FIRST_NAME, details.getString("first_name"));
            prefsEditor.putString(LAST_NAME, details.getString("last_name"));
            prefsEditor.putString(EMAIL, details.getString("email"));
            prefsEditor.apply();
        } catch (JSONException e) {
            Log.d("DEBUG", "Unable to read user details");
        }
    }

    public int getUserId() {
        return prefs.getInt(USER_ID, -1);
    }

    public String getIdAsString() {
        return prefs.getString(USER_ID, null);
    }

    public String getToken() {
        return prefs.getString(TOKEN, null);
    }

    public String getFirstName() {
        return prefs.getString(FIRST_NAME, null);
    }

    public String getLastName() {
        return prefs.getString(LAST_NAME, null);
    }

    public String getEmail() {
        return prefs.getString(EMAIL, null);
    }

    public String getFullName() {
        String firstName = prefs.getString(FIRST_NAME, null);
        String lastName = prefs.getString(LAST_NAME, null);
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }
}
