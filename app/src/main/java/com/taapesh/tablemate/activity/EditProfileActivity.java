package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.taapesh.tablemate.util.ActivityCode;
import com.taapesh.tablemate.util.ToolbarManager;

import com.taapesh.tablemate.R;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.EDIT_PROFILE_ACTIVITY_TITLE, ActivityCode.EDIT_PROFILE_ACTIVITY);
    }
}
