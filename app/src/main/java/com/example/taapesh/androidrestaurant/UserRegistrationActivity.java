package com.example.taapesh.androidrestaurant;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class UserRegistrationActivity extends AppCompatActivity {
    private static final String REGISTER_ENDPOINT = "http://taapesh.pythonanywhere.com/auth/register/";
    private static EditText firstNameField;
    private static EditText lastNameField;
    private static EditText emailField;
    private static EditText passwordField;
    private static EditText phoneField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        View v = actionBar.getCustomView();
        TextView actionBarText = (TextView) v.findViewById(R.id.actionBarTitle);
        actionBarText.setText("Register");

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

    class RegisterInBackground extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... fields) {
            HttpURLConnection conn = null;
            try {
                // Get registration data
                String firstName = fields[0];
                String lastName = fields[1];
                String phone_number = fields[2];
                String email = fields[3];
                String password = fields[4];

                // Encode data
                String data = URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
                data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(phone_number, "UTF-8");

                // Setup Http POST request with data
                URL url = new URL(REGISTER_ENDPOINT);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                conn.connect();

                // Create JSON object from response content
                InputStream is;
                if (conn.getResponseCode() / 100 == 2) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = rd.readLine()) != null) {
                    sb.append(inputLine + "\n");
                }
                wr.close();
                rd.close();

                return new JSONObject(sb.toString());
            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                // data retrieval or connection timed out
            } catch (IOException e) {
                // could not read response body
                // (could not create input stream)
            } catch (JSONException e) {
                // response body is no valid JSON string
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(JSONObject result) {
            // TODO: do something with the result
            // check if user was returned and if so, log the user in and go to user home page
            // otherwise, determine the error e.g. email/phone is already in use, and display it
        }
    }
}
