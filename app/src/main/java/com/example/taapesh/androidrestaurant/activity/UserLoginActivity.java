package com.example.taapesh.androidrestaurant.activity;

import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Html;

import com.example.taapesh.androidrestaurant.util.ActivityCode;
import com.example.taapesh.androidrestaurant.util.Endpoint;
import com.example.taapesh.androidrestaurant.util.NavManager;
import com.example.taapesh.androidrestaurant.util.PreferencesManager;
import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;

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

    private static EditText loginEmailField;
    private static EditText loginPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.LOGIN_ACTIVITY_TITLE, ActivityCode.LOGIN_ACTIVITY);

        loginEmailField = (EditText) findViewById(R.id.loginEmailField);
        loginPasswordField = (EditText) findViewById(R.id.loginPasswordField);

        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: validate input data before making request
                String email = loginEmailField.getText().toString();
                String password = loginPasswordField.getText().toString();

                // Attempt login
                tryLogin(Endpoint.TEST_EMAIL, Endpoint.TEST_PASSWORD);
            }
        });

        Button waiterTestButton = (Button) findViewById(R.id.waiterTestButton);
        waiterTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin("woodhouse@gmail.com", "12345");
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
                .url(Endpoint.LOGIN_ENDPOINT)
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
                    if (response.code() == 404) {
                        Log.d("DEBUG", "Unable to log in");
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } else {
                    try {
                        String responseData = response.body().string();
                        JSONObject userDetails = new JSONObject(responseData);
                        new PreferencesManager(UserLoginActivity.this).setUserDetails(userDetails);
                        new NavManager(UserLoginActivity.this).goToUserHome();
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Unable to parse user info");
                    }
                }
            }
        });
    }
}

