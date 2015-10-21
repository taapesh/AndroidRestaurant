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


public class UserLoginActivity extends AppCompatActivity
{
    private static EditText loginEmailField;
    private static EditText loginPasswordField;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

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
        actionBarText.setText(R.string.title_login);

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

    class LoginInBackground extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... fields) {
            // create connection
            String email = fields[0];
            String password = fields[1];

            LoginRegisterHelper loginHelper = new LoginRegisterHelper();
            return loginHelper.tryLogin(email, password);
        }

        protected void onPostExecute(JSONObject loginResult)
        {
            try
            {
                int result = (int)loginResult.get("result");
                if (result == LoginRegisterHelper.LOGIN_SUCCESS)
                {
                    // Login success, set user details in preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceManager.TOKEN, loginResult.getString("token"));
                    editor.putInt(PreferenceManager.USER_ID, loginResult.getInt("id"));
                    editor.putString(PreferenceManager.FIRST_NAME, loginResult.getString("firstName"));
                    editor.putString(PreferenceManager.LAST_NAME, loginResult.getString("lastName"));
                    editor.putString(PreferenceManager.EMAIL, loginResult.getString("email"));
                    editor.putString(PreferenceManager.PHONE, loginResult.getString("phoneNumber"));
                    editor.commit();

                    Intent goToUserHome = new Intent(UserLoginActivity.this, UserHomeActivity.class);
                    finish();
                    startActivity(goToUserHome);
                } else {
                    // handle some login error
                    Log.e("LoginError", "Something went wrong");
                }
            }
            catch (JSONException e)
            {
                // pass
            }
        }
    }
}

