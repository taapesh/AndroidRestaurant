package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.PreferencesManager;

import com.taapesh.tablemate.R;


public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

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
