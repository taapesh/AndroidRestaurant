package com.taapesh.tablemate.activity;

import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import com.taapesh.tablemate.util.ActivityCode;
import com.taapesh.tablemate.util.Endpoints;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.PreferencesManager;
import com.taapesh.tablemate.util.ToolbarManager;

import com.taapesh.tablemate.R;


public class UserRegistrationActivity extends AppCompatActivity {
    private static final String TAG = "UserRegisterActivity";
    private static EditText firstNameField;
    private static EditText lastNameField;
    private static EditText emailField;
    private static EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        new ToolbarManager(this).setupGoBackToolbar(
                ToolbarManager.REGISTRATION_ACTIVITY_TITLE, ActivityCode.REGISTRATION_ACTIVITY);

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

                tryRegister(
                        Endpoints.TEST_EMAIL_REGISTER,
                        Endpoints.TEST_PASSWORD_REGISTER,
                        Endpoints.TEST_FIRST_NAME,
                        Endpoints.TEST_LAST_NAME);
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
                .url(Endpoints.REGISTER_ENDPOINT)
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
                    JSONObject userDetails = new JSONObject(responseData);
                    new PreferencesManager(UserRegistrationActivity.this).setUserDetails(userDetails);
                    new NavManager(UserRegistrationActivity.this).goToUserHome();
                } catch (JSONException e) {
                    Log.d(TAG, "Unable to parse user info");
                }
            }
        });

    }
}
