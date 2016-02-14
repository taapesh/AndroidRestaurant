package com.example.taapesh.androidrestaurant.activity;

import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.Endpoint;
import com.example.taapesh.androidrestaurant.util.PreferencesManager;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;

import com.pubnub.api.Pubnub;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ServerActivity extends AppCompatActivity {

    private static final String PUBLISH_KEY = "pub-c-2344ebc7-3daf-4ffd-8c58-a8f809e85a2e";
    private static final String SUBSCRIBE_KEY = "sub-c-01429274-6938-11e5-a5be-02ee2ddab7fe";
    private static final int MAX_TABLES = 3;

    private String serverId;
    private Pubnub pubnub;
    private String authToken;

    private LinearLayout[] tableViews = new LinearLayout[MAX_TABLES];

    @Override
    protected void onResume() {
        super.onResume();
        getServerTables();
        connectToChannel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serving);
        new ToolbarManager(this).setupServerToolbar(ToolbarManager.SERVER_ACTIVITY_TITLE);
        initUserInfo();

        final LinearLayout serverTablesRoot = (LinearLayout) findViewById(R.id.serverTablesRoot);
        for (int i = 0; i < MAX_TABLES; i++) {
            tableViews[i] = (LinearLayout) serverTablesRoot.getChildAt(i);
        }
        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
    }

    @Override
    public void onPause() {
        super.onPause();
        pubnub.unsubscribe(serverId);
    }

    private void initUserInfo() {
        final PreferencesManager prefs = new PreferencesManager(this);
        authToken = prefs.getToken();
        serverId = prefs.getIdAsString();
    }

    private void getServerTables() {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Endpoint.SERVER_TABLES_ENDPOINT)
                .addHeader("Authorization", "Token " + authToken)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
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
                    JSONArray json = new JSONArray(response.body().string());
                    runOnUiThread(new DisplayTables(json));
                } catch (JSONException e) {
                    Log.d("DEBUG", "Unable to parse table data");
                }
            }
        });
    }

    class DisplayTables implements Runnable {
        JSONArray data;

        DisplayTables(JSONArray d) {
            data = d;
        }

        @Override
        public void run() {
            int numTables = data.length();
            for (int i = 0; i < numTables; i++) {
                try {
                    JSONObject table = data.getJSONObject(i);
                    TextView tableTitle = (TextView) tableViews[i].getChildAt(0);
                    tableTitle.setText(table.getString("party_size") + " tablemates");
                } catch (JSONException e) {
                    Log.d("DEBUG", "Unable to read table data");
                }
            }
        }
    }

    private void connectToChannel() {
        try {
            pubnub.subscribe(serverId, new Callback() {
                public void successCallback(String channel, Object message) {
                    JSONObject msg;
                    try {
                        msg = new JSONObject(message.toString());
                        processMessage(msg);
                        runOnUiThread(new MakeToast(msg.toString()));
                    }
                    catch (JSONException e) {
                        Log.d("DEBUG", "Unable to parse pubnub message");
                    }
                }

                public void errorCallback(String channel, PubnubError error) {
                    // Handle PubNub message error
                }
            });
        }
        catch (PubnubException e) {
            // Handle PubNub initialization error
        }
    }

    private void processMessage(JSONObject msg) {
        Log.d("DEBUG", msg.toString());
    }

    class MakeToast implements Runnable {
        String msg;

        MakeToast(String m) {
            msg = m;
        }

        @Override
        public void run() {
            Toast.makeText(ServerActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}

