package com.example.taapesh.androidrestaurant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.NavManager;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;


public class ServerHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_home);
        new ToolbarManager(this).setupServerHomeToolbar();

        Button startServingButton = (Button) findViewById(R.id.startServingBtn);
        startServingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NavManager(ServerHomeActivity.this).goToServer();
            }
        });
    }
}
