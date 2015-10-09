package com.example.taapesh.androidrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UserHomeActivity extends AppCompatActivity {
    private static DrawerLayout mDrawerLayout;
    private static View menuToggleArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setCustomView(R.layout.user_home_action_bar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert mDrawerLayout != null;
        menuToggleArea = findViewById(R.id.menuToggleArea);

        menuToggleArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        // Setup menu click events
        View profileMenu = findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfile = new Intent(UserHomeActivity.this, ProfileActivity.class);
                startActivity(goToProfile);
            }
        });

        View paymentMenu = findViewById(R.id.paymentMenu);
        paymentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPayment = new Intent(UserHomeActivity.this, PaymentActivity.class);
                startActivity(goToPayment);
            }
        });

        View historyMenu = findViewById(R.id.historyMenu);
        historyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHistory = new Intent(UserHomeActivity.this, HistoryActivity.class);
                startActivity(goToHistory);
            }
        });

        View serverModeMenu = findViewById(R.id.switchToWaiterMenu);
        serverModeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToServerHome = new Intent(UserHomeActivity.this, WaiterHomeActivity.class);
                startActivity(goToServerHome);
            }
        });

        // Main action buttons
        final Button startTableButton = (Button) findViewById(R.id.startTableButton);
        startTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFindTable = new Intent(UserHomeActivity.this, FindTableActivity.class);
                startActivity(goToFindTable);
            }
        });

        Button joinTableButton = (Button) findViewById(R.id.joinTableButton);
        joinTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToJoinTable = new Intent(UserHomeActivity.this, JoinTableActivity.class);
                startActivity(goToJoinTable);
            }
        });

        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String name = preferences.getString("first_name", "User");
        Toast.makeText(getApplicationContext(), "Welcome, " + name, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
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

    private void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            //menuToggleIcon.setImageResource(R.drawable.menu_24);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
            //menuToggleIcon.setImageResource(R.drawable.left_arrow_24);
        }
    }
}
