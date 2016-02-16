package com.taapesh.tablemate.activity;

import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.pubnub.api.Pubnub;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.taapesh.tablemate.util.Constants;
import com.taapesh.tablemate.util.Endpoints;
import com.taapesh.tablemate.util.PreferencesManager;
import com.taapesh.tablemate.util.ToolbarManager;

import com.taapesh.tablemate.R;


public class ServerActivity extends AppCompatActivity {
    private static final String TAG = "ServerActivity";

    private String serverId;
    private Pubnub pubnub;
    private String authToken;

    private LinearLayout[] tableViews = new LinearLayout[Constants.MAX_TABLES];

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
        for (int i = 0; i < Constants.MAX_TABLES; i++) {
            tableViews[i] = (LinearLayout) serverTablesRoot.getChildAt(i);
        }
        pubnub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
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
                .url(Endpoints.SERVER_TABLES_ENDPOINT)
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
                    Log.d(TAG, "Unable to parse table data");
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
                    Log.d(TAG, "Unable to read table data");
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
                        Log.d(TAG, "Unable to parse pubnub message");
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
        Log.d(TAG, msg.toString());
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

