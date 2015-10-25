package com.example.taapesh.androidrestaurant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.CustomActionBar;

public class TableActivity extends AppCompatActivity {

    private Button orderBtn;
    private Button requestBtn;
    private Button finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eating);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_eating, R.layout.custom_action_bar);

        orderBtn = (Button)findViewById(R.id.orderBtn);
        requestBtn = (Button)findViewById(R.id.requestBtn);
        finishBtn = (Button)findViewById(R.id.finishBtn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
