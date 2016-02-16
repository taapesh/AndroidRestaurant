package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.taapesh.tablemate.util.ActivityCode;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.ToolbarManager;

import com.taapesh.tablemate.R;


public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";

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
