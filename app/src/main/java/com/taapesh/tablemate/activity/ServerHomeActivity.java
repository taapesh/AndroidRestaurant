package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.taapesh.tablemate.R;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.ToolbarManager;


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
