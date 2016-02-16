package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.taapesh.tablemate.util.ActivityCode;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.ToolbarManager;

import com.taapesh.tablemate.R;


public class ReceiptsActivity extends AppCompatActivity {
    private static final String TAG = "ReceiptsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.RECEIPTS_ACTIVITY_TITLE, ActivityCode.RECEIPTS_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        new NavManager(this).goToUserHome();
    }
}
