package com.example.taapesh.androidrestaurant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.ActivityCode;
import com.example.taapesh.androidrestaurant.util.NavManager;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;


public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.PAYMENT_ACTIVITY_TITLE, ActivityCode.PAYMENT_ACTIVITY);

        final Button addPaymentBtn = (Button) findViewById(R.id.addPaymentBtn);
        addPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NavManager(PaymentActivity.this).goToAddPayment();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new NavManager(this).goToUserHome();
    }
}
