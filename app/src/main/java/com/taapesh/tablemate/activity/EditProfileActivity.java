package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.taapesh.tablemate.R;
import com.taapesh.tablemate.util.ActivityCode;
import com.taapesh.tablemate.util.ToolbarManager;


public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.EDIT_PROFILE_ACTIVITY_TITLE, ActivityCode.EDIT_PROFILE_ACTIVITY);
    }
}
