package com.example.taapesh.androidrestaurant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.ActivityCode;
import com.example.taapesh.androidrestaurant.util.NavManager;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;


public class ReceiptsActivity extends AppCompatActivity {

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
