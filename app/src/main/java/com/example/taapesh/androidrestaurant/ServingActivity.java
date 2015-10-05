package com.example.taapesh.androidrestaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pubnub.api.Pubnub;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class ServingActivity extends AppCompatActivity {
    private static Pubnub pubnub;
    private static final String PUBLISH_KEY = "pub-c-2344ebc7-3daf-4ffd-8c58-a8f809e85a2e";
    private static final String SUBSCRIBE_KEY = "sub-c-01429274-6938-11e5-a5be-02ee2ddab7fe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serving);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        View v = actionBar.getCustomView();
        TextView actionBarText = (TextView) v.findViewById(R.id.actionBarTitle);
        actionBarText.setText("Serving");

        // Initialize Pubnub
        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        /* Waiter connects to own channel, a combination of his email and other characters */
        try {
            // Test waiter: woodhouse
            pubnub.subscribe("woodhouse", new Callback() {
                public void successCallback(String channel, Object message) {
                    Log.i("PUBNUB_MESSAGE", channel + ": " + message.toString());
                }

                public void errorCallback(String channel, PubnubError error) {
                    Log.i("PUBNUB_ERROR", error.getErrorString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
            Log.i("PUBNUB_EXCEPTION", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serving, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
