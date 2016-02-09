package com.example.taapesh.androidrestaurant.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.NavManager;
import com.example.taapesh.androidrestaurant.util.PreferencesManager;


public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferencesManager prefs = new PreferencesManager(this);
        final String authToken = prefs.getToken();
        if (authToken != null) {
            // TODO: Check if token is valid, determine where to go from there
            new NavManager(this).goToUserHome();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NavManager(StartActivity.this).goToUserLogin();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NavManager(StartActivity.this).goToUserRegistration();
            }
        });
    }
}
