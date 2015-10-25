package com.example.taapesh.androidrestaurant.activity;

// Android imports
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.taapesh.androidrestaurant.util.CustomActionBar;
import com.example.taapesh.androidrestaurant.util.LoginRegisterHelper;
import com.example.taapesh.androidrestaurant.util.PreferenceManager;
import com.example.taapesh.androidrestaurant.R;

import org.json.JSONException;
import org.json.JSONObject;


public class UserLoginActivity extends AppCompatActivity {

    private static EditText loginEmailField;
    private static EditText loginPasswordField;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_login, R.layout.custom_action_bar);

        sharedPreferences = getSharedPreferences(PreferenceManager.MY_PREFERENCES, Context.MODE_PRIVATE);
        loginEmailField = (EditText) findViewById(R.id.loginEmailField);
        loginPasswordField = (EditText) findViewById(R.id.loginPasswordField);

        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent goToUserHome = new Intent(UserLoginActivity.this, UserHomeActivity.class);
                startActivity(goToUserHome);
                */
                // TODO: validate input data before making HTTP request
                String email = loginEmailField.getText().toString();
                String password = loginPasswordField.getText().toString();
                new LoginInBackground().execute(email, password);
            }
        });

        Button waiterTestButton = (Button) findViewById(R.id.waiterTestButton);
        waiterTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToServing = new Intent(UserLoginActivity.this, ServingActivity.class);
                finish();
                startActivity(goToServing);
            }
        });

        String htmlString = "<u>Forgot Password?</u>";
        TextView forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        forgotPasswordText.setText(Html.fromHtml(htmlString));
    }

    class LoginInBackground extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... fields) {
            String email = fields[0];
            String password = fields[1];

            return LoginRegisterHelper.tryLogin(email, password);
        }

        protected void onPostExecute(JSONObject loginResult) {
            try {
                int result = (int)loginResult.get("result");
                if (result == LoginRegisterHelper.LOGIN_SUCCESS) {
                    // Login success, set user details in preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceManager.TOKEN, loginResult.getString("token"));
                    editor.putString(PreferenceManager.USER_ID, loginResult.getString("id"));
                    editor.putString(PreferenceManager.FIRST_NAME, loginResult.getString("firstName"));
                    editor.putString(PreferenceManager.LAST_NAME, loginResult.getString("lastName"));
                    editor.putString(PreferenceManager.EMAIL, loginResult.getString("email"));
                    editor.putString(PreferenceManager.PHONE, loginResult.getString("phoneNumber"));
                    editor.commit();

                    Intent goToUserHome = new Intent(UserLoginActivity.this, UserHomeActivity.class);
                    finish();
                    startActivity(goToUserHome);
                }
                else {
                    // handle some login error
                    Log.e("LoginError", "Something went wrong");
                }
            }
            catch (JSONException e) {
                // pass
            }
        }
    }
}

