package com.example.taapesh.androidrestaurant.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.CustomActionBar;

public class WaiterHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_home);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.placeholder, R.layout.custom_action_bar);

        Button startServingButton = (Button) findViewById(R.id.startServingButton);
        startServingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToServing = new Intent(WaiterHomeActivity.this, ServingActivity.class);
                startActivity(goToServing);
            }
        });
    }
}
