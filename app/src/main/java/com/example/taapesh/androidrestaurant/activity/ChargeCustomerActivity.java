package com.example.taapesh.androidrestaurant.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.object.Table;
import com.example.taapesh.androidrestaurant.util.CustomActionBar;

public class ChargeCustomerActivity extends AppCompatActivity {

    private Table table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_customer);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_charge_customer, R.layout.custom_action_bar);

        Intent i = getIntent();
        table = (Table) i.getParcelableExtra("table");

        // Test get Table parcel
        Toast.makeText(ChargeCustomerActivity.this, "Charge for " + table.getOwnerFirstName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_charge_customer, menu);
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
