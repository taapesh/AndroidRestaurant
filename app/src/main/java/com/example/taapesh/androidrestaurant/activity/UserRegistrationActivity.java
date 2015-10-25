package com.example.taapesh.androidrestaurant.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.taapesh.androidrestaurant.util.CustomActionBar;
import com.example.taapesh.androidrestaurant.util.LoginRegisterHelper;
import com.example.taapesh.androidrestaurant.util.PreferenceManager;
import com.example.taapesh.androidrestaurant.R;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistrationActivity extends AppCompatActivity {

    private static EditText firstNameField;
    private static EditText lastNameField;
    private static EditText emailField;
    private static EditText passwordField;
    private static EditText phoneField;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_register, R.layout.custom_action_bar);

        sharedPreferences = getSharedPreferences(PreferenceManager.MY_PREFERENCES, Context.MODE_PRIVATE);

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

    class RegisterInBackground extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... fields) {
            // Get registration data
            String firstName = fields[0];
            String lastName = fields[1];
            String phoneNumber = fields[2];
            String email = fields[3];
            String password = fields[4];

            return LoginRegisterHelper.tryRegister(firstName, lastName, phoneNumber, email, password);
        }

        protected void onPostExecute(JSONObject registerResult) {
            // check if user was returned and if so, log the user in and go to user home page
            // otherwise, determine the error e.g. email/phone is already in use, and display it
            try {
                int result = (int) registerResult.get("result");
                if (result == LoginRegisterHelper.REGISTER_SUCCESS) {
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
                else {
                    // handle some registration error
                    Log.e("RegistrationError", "Something went wrong");
                }
            }
            catch (JSONException e) {
                // pass
            }
        }
    }
}