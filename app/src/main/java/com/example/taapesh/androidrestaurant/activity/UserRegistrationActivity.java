package com.example.taapesh.androidrestaurant.activity;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taapesh.androidrestaurant.util.CustomActionBar;
import com.example.taapesh.androidrestaurant.util.PreferenceManager;
import com.example.taapesh.androidrestaurant.R;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserRegistrationActivity extends AppCompatActivity {
    private static final String REGISTER_ENDPOINT = "http://safe-springs-46272.herokuapp.com/register/";
    private static EditText firstNameField;
    private static EditText lastNameField;
    private static EditText emailField;
    private static EditText passwordField;

    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_PASSWORD = "12345";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_register, R.layout.custom_action_bar);
        firstNameField = (EditText) findViewById(R.id.registerFirstName);
        lastNameField = (EditText) findViewById(R.id.registerLastName);
        emailField = (EditText) findViewById(R.id.registerEmail);
        passwordField = (EditText) findViewById(R.id.registerPassword);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: validate data before sending it for registration
                String firstName = firstNameField.getText().toString();
                String lastName = lastNameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                tryRegister(TEST_EMAIL, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);
            }
        });
    }

    private void tryRegister(String email, String password, String firstName, String lastName) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("first_name", firstName)
                .add("last_name", lastName)
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_ENDPOINT)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // TODO: validate response
                if (!response.isSuccessful()) {
                    if (response.code() == 409) {
                        UserRegistrationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                    UserRegistrationActivity.this, "Email already in use", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                    return;
                }
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);

                    SharedPreferences sharedPreferences = getSharedPreferences(PreferenceManager.MY_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceManager.TOKEN, json.getString("auth_token"));
                    editor.putString(PreferenceManager.USER_ID, json.getString("user_id"));
                    editor.putString(PreferenceManager.EMAIL, json.getString("email"));
                    editor.putString(PreferenceManager.FIRST_NAME, json.getString("first_name"));
                    editor.putString(PreferenceManager.LAST_NAME, json.getString("last_name"));
                    editor.apply();

                    Intent goToUserHome = new Intent(UserRegistrationActivity.this, UserHomeActivity.class);
                    finish();
                    startActivity(goToUserHome);
                } catch (JSONException e) {
                    // pass
                }
            }
        });

    }
}
