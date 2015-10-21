package com.example.taapesh.androidrestaurant.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    public static final String MY_PREFERENCES = "preferences";
    public static final String USER_ID = "user_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHONE = "phone_number";
    public static final String EMAIL = "email";
    public static final String TOKEN = "token";

    public static String getPreference(Context context, String pref) {
        SharedPreferences preferences = context.getSharedPreferences("Preferences", context.MODE_PRIVATE);
        return preferences.getString(pref, "");
    }
}
