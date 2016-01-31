package com.example.taapesh.androidrestaurant.activity;

// Android imports
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Html;
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


public class UserLoginActivity extends AppCompatActivity {
    private static final String LOGIN_ENDPOINT = "http://safe-springs-46272.herokuapp.com/login/";
    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_PASSWORD = "12345";
    private static EditText loginEmailField;
    private static EditText loginPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_login, R.layout.custom_action_bar);
        loginEmailField = (EditText) findViewById(R.id.loginEmailField);
        loginPasswordField = (EditText) findViewById(R.id.loginPasswordField);

        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: validate input data before making HTTP request
                String email = loginEmailField.getText().toString();
                String password = loginPasswordField.getText().toString();

                // Attempt login
                tryLogin(TEST_EMAIL, TEST_PASSWORD);
            }
        });

        Button waiterTestButton = (Button) findViewById(R.id.waiterTestButton);
        waiterTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToServerHome = new Intent(UserLoginActivity.this, ServingActivity.class);
                finish();
                startActivity(goToServerHome);
            }
        });

        String htmlString = "<u>Forgot Password?</u>";
        TextView forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        forgotPasswordText.setText(Html.fromHtml(htmlString));
    }

    private void tryLogin(String email, String password) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_ENDPOINT)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    SharedPreferences sharedPreferences = getSharedPreferences(PreferenceManager.MY_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceManager.TOKEN,  json.getString("auth_token"));
                    editor.putString(PreferenceManager.USER_ID, json.getString("user_id"));
                    editor.putString(PreferenceManager.EMAIL, json.getString("email"));
                    editor.putString(PreferenceManager.FIRST_NAME, json.getString("first_name"));
                    editor.putString(PreferenceManager.LAST_NAME, json.getString("last_name"));
                    editor.apply();

                    Intent goToUserHome = new Intent(UserLoginActivity.this, UserHomeActivity.class);
                    finish();
                    startActivity(goToUserHome);
                } catch (JSONException e) {
                    // pass
                }
            }
        });
    }
}

