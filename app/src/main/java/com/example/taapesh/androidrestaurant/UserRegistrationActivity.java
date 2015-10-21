package com.example.taapesh.androidrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistrationActivity extends AppCompatActivity
{
    private static EditText firstNameField;
    private static EditText lastNameField;
    private static EditText emailField;
    private static EditText passwordField;
    private static EditText phoneField;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        sharedPreferences = getSharedPreferences(PreferenceManager.MY_PREFERENCES, Context.MODE_PRIVATE);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        View v = actionBar.getCustomView();
        TextView actionBarText = (TextView) v.findViewById(R.id.actionBarTitle);
        actionBarText.setText(R.string.title_register);

        // Get registration fields
        firstNameField = (EditText) findViewById(R.id.registerFirstName);
        lastNameField = (EditText) findViewById(R.id.registerLastName);
        emailField = (EditText) findViewById(R.id.registerEmail);
        passwordField = (EditText) findViewById(R.id.registerPassword);
        phoneField = (EditText) findViewById(R.id.registerPhone);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: validate data before sending it for registration
                String firstName = firstNameField.getText().toString();
                String lastName = lastNameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String phone_number = phoneField.getText().toString();

                new RegisterInBackground().execute(firstName, lastName, phone_number, email, password);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_registration, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    class RegisterInBackground extends AsyncTask<String, Void, JSONObject>
    {
        protected JSONObject doInBackground(String... fields)
        {
            // Get registration data
            String firstName = fields[0];
            String lastName = fields[1];
            String phoneNumber = fields[2];
            String email = fields[3];
            String password = fields[4];

            LoginRegisterHelper loginRegisterHelper = new LoginRegisterHelper();
            return loginRegisterHelper.tryRegister(firstName, lastName, phoneNumber, email, password);
        }

        protected void onPostExecute(JSONObject registerResult)
        {
            // check if user was returned and if so, log the user in and go to user home page
            // otherwise, determine the error e.g. email/phone is already in use, and display it
            try
            {
                int result = (int) registerResult.get("result");
                if (result == LoginRegisterHelper.REGISTER_SUCCESS)
                {
                    Log.i("REGISTER_TEST", "LOL HELLO");
                    // Registration success, set user details in preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceManager.TOKEN, registerResult.getString("token"));
                    editor.putInt(PreferenceManager.USER_ID, registerResult.getInt("id"));
                    editor.putString(PreferenceManager.FIRST_NAME, registerResult.getString("firstName"));
                    editor.putString(PreferenceManager.LAST_NAME, registerResult.getString("lastName"));
                    editor.putString(PreferenceManager.EMAIL, registerResult.getString("email"));
                    editor.putString(PreferenceManager.PHONE, registerResult.getString("phoneNumber"));
                    editor.commit();

                    Intent goToUserHome = new Intent(UserRegistrationActivity.this, UserHomeActivity.class);
                    finish();
                    startActivity(goToUserHome);
                }
                else
                {
                    // handle some registration error
                    Log.e("RegistrationError", "Something went wrong");
                }
            }
            catch (JSONException e)
            {
                // pass
            }
        }
    }
}
