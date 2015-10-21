package com.example.taapesh.androidrestaurant;

// Android imports
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Java imports
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


public class UserLoginActivity extends AppCompatActivity {
    private static final String LOGIN_ENDPOINT = "http://taapesh.pythonanywhere.com/auth/login/";
    private static final String USER_ENDPOINT = "http://taapesh.pythonanywhere.com/auth/me/";
    private static final int CONNECTION_TIMEOUT = 7;
    private static final int DATARETRIEVAL_TIMEOUT = 7;

    private static EditText loginEmailField;
    private static EditText loginPasswordField;

    private static SharedPreferences sharedPreferences;
    private static final String MY_PREFERENCES = "Preferences";
    private static final String userIdPref = "user_id";
    private static final String firstNamePref = "first_name";
    private static final String lastNamePref = "last_name";
    private static final String phonePref = "phone_number";
    private static final String emailPref = "email";
    private static final String tokenPref = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        View v = actionBar.getCustomView();
        TextView actionBarText = (TextView) v.findViewById(R.id.actionBarTitle);
        actionBarText.setText("Sign In");

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
        TextView mTextView = (TextView) findViewById(R.id.forgotPasswordText);
        mTextView.setText(Html.fromHtml(htmlString));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
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

    class LoginInBackground extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... fields) {
            HttpURLConnection conn = null;
            HttpURLConnection loginConn = null;

            try {
                // create connection
                String email = fields[0];
                String password = fields[1];

                // Encode data
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                // Setup Http POST request with data
                URL url = new URL(LOGIN_ENDPOINT);
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

                // Process result
                JSONObject result = new JSONObject(sb.toString());
                String token = result.getString("auth_token");
                if (token != null) {
                    url = new URL(USER_ENDPOINT);
                    loginConn = (HttpURLConnection) url.openConnection();
                    loginConn.setRequestMethod("GET");
                    loginConn.setRequestProperty("Authorization", "Token " + token);
                    loginConn.connect();

                    // Create JSON object from response content
                    InputStream login_is;
                    if (loginConn.getResponseCode() / 100 == 2) {
                        login_is = loginConn.getInputStream();
                    } else {
                        login_is = loginConn.getErrorStream();
                    }

                    BufferedReader login_rd = new BufferedReader(new InputStreamReader(login_is));
                    StringBuilder login_sb = new StringBuilder();
                    while ((inputLine = login_rd.readLine()) != null) {
                        login_sb.append(inputLine + "\n");
                    }
                    login_rd.close();
                    Log.i("CHECKPOINT", "HERE 2");
                    Log.i("LOGIN_RESULT", login_sb.toString());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    JSONObject loginResult = new JSONObject(login_sb.toString());
                    String firstName = loginResult.getString("first_name");
                    if (firstName != null) {
                        editor.putString(tokenPref, token);
                        editor.putInt(userIdPref, loginResult.getInt("id"));
                        editor.putString(firstNamePref, firstName);
                        editor.putString(lastNamePref, loginResult.getString("last_name"));
                        editor.putString(emailPref, loginResult.getString("email"));
                        editor.putString(phonePref, loginResult.getString("phone_number"));
                        editor.commit();

                        return "1";
                    } else {
                        // call failed
                    }
                }
                else {
                    // token was null
                }
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
                } if (loginConn != null) {
                    loginConn.disconnect();
                }
            }
            return "";
        }

        protected void onPostExecute(String result) {
            // TODO: do something with the result

            if (result.equals("1")) {
                Intent goToUserHome = new Intent(UserLoginActivity.this, UserHomeActivity.class);
                finish();
                startActivity(goToUserHome);
            }
            else if (result.equals("")) {
                // handle some error
                Log.i("ERROR", "Something went wrong");
            }
        }
    }
}

