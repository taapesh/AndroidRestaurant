package com.example.taapesh.androidrestaurant;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// HTTP stuff
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;


public class UserLoginActivity extends AppCompatActivity {

    private static final int CONNECTION_TIMEOUT = 7;
    private static final int DATARETRIEVAL_TIMEOUT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

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

        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToUserHome = new Intent(UserLoginActivity.this, UserHomeActivity.class);
                startActivity(goToUserHome);
            }
        });

        String htmlString = "<u>Forgot Password?</u>";
        TextView mTextView = (TextView) findViewById(R.id.forgotPasswordText);
        mTextView.setText(Html.fromHtml(htmlString));

        Button restTestButton = (Button) findViewById(R.id.restTestButton);
        restTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the restful request
                new RequestWebService().execute("http://taapesh.pythonanywhere.com/users/");
            }
        });
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

    class RequestWebService extends AsyncTask<String, Void, JSONObject> {
        private String _url = "";
        String requestResult = "";
        Exception _e = null;

        protected JSONObject doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            try {
                // create connection
                URL url= new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                //urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

                // create JSON object from content
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine + "\n");
                }
                requestResult = sb.toString();
                makeToast();
                return new JSONObject(sb.toString());
            } catch (MalformedURLException e) {
                // URL is invalid
                _e = e;
                //makeToast();
            } catch (SocketTimeoutException e) {
                // data retrieval or connection timed out
                _e = e;
            } catch (IOException e) {
                // could not read response body
                // (could not create input stream)
                _e = e;
            } catch (JSONException e) {
                // response body is no valid JSON string
                _e = e;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            if (_e != null) {
                makeToastException();
            }
            return null;
        }

        protected void onPostExecute(JSONObject serviceResult) {
            // TODO: check this.exception
            // TODO: do something with the feed
            //makeToast(serviceResult.toString());
        }

        private void makeToastException() {
            UserLoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), "Exception: " + _e.toString(), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        private void makeToast() {
            UserLoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), "Result: " + requestResult, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }
}

