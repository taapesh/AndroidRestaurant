package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.taapesh.tablemate.util.ActivityCode;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.ToolbarManager;

import com.taapesh.tablemate.R;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.PROFILE_ACTIVITY_TITLE, ActivityCode.PROFILE_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        new NavManager(this).goToUserHome();
    }
}
