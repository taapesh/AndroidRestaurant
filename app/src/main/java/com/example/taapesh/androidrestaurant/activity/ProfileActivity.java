package com.example.taapesh.androidrestaurant.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.CustomActionBar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_profile, R.layout.custom_action_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.editProfileMenu:
                Intent goToEditProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(goToEditProfile);
                break;
            case R.id.signOutMenu:
                // Log the user out and go to start activity
                Intent goToStart = new Intent(ProfileActivity.this, StartActivity.class);
                startActivity(goToStart);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
