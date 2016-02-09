package com.example.taapesh.androidrestaurant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.ActivityCode;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;


public class AddPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.ADD_PAYMENT_ACTIVITY_TITLE, ActivityCode.ADD_PAYMENT_ACTIVITY);
    }
}
