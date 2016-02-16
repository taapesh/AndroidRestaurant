package com.taapesh.tablemate.activity;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taapesh.tablemate.R;
import com.taapesh.tablemate.util.Endpoint;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.NetworkStateReceiver;
import com.taapesh.tablemate.util.NetworkStateReceiver.NetworkStateReceiverListener;
import com.taapesh.tablemate.util.NetworkStatus;
import com.taapesh.tablemate.util.PreferencesManager;
import com.taapesh.tablemate.util.ToolbarManager;

import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TableActivity extends AppCompatActivity implements NetworkStateReceiverListener {

    private static final int TABLE_REQUEST_CODE = 1;
    private static final int TABLE_SERVICE_CODE = 2;

    private static final String SERVICE_REQUESTED_TEXT = "Requested";
    private static final String REQUEST_SERVICE_TEXT = "Table Service";

    private static final String PUBLISH_KEY = "pub-c-2344ebc7-3daf-4ffd-8c58-a8f809e85a2e";
    private static final String SUBSCRIBE_KEY = "sub-c-01429274-6938-11e5-a5be-02ee2ddab7fe";
    private Pubnub pubnub;

    // From shared preferences
    private PreferencesManager prefs;

    private int userId;
    private String authToken;
    private String serverId;
    private String addressTableCombo;
    private String serverName;
    private String restaurantName;
    private String restaurantAddress;

    private TextView restaurantNameText;
    private TextView serverNameText;
    private View tableServiceBtn;
    private TextView tableServiceText;
    private ImageView tableServiceIcon;
    private View placeOrderBtn;
    private View progressWheel;

    private NetworkStateReceiver networkStateReceiver;
    private NetworkStatus networkStatus;
    private boolean isNetworkAvailable;
    private boolean allowRequests = true;
    private AlertDialog noNetworkDialog;

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvailable = networkStatus.isOnline();
        if (isNetworkAvailable) {
            getServerId();
        }
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (serverId != null) {
            pubnub.unsubscribe(serverId);
        }
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        new ToolbarManager(this).setupTableToolbar(ToolbarManager.TABLE_ACTIVITY_TITLE);

        prefs = new PreferencesManager(this);
        userId = prefs.getUserId();
        authToken = prefs.getToken();
        serverName = prefs.getServerName();
        restaurantName = prefs.getRestaurantName();
        restaurantAddress = prefs.getRestaurantAddress();
        addressTableCombo = prefs.getAddrTableCombo();

        setupWidgets();

        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);

        networkStatus = new NetworkStatus(this);
        networkStateReceiver = new NetworkStateReceiver(this);
        networkStateReceiver.addListener(this);
    }

    private void getServerId() {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Endpoint.SERVER_ID_ENDPOINT)
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
                } else {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        serverId = json.getString("server_id");
                        Log.d("TABLE_ACTIVITY", "Server id obtained");
                        connectWithServer();
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Unable to parse server id");
                    }
                }
            }
        });
    }

    private void connectWithServer() {
        try {
            pubnub.subscribe(serverId, new com.pubnub.api.Callback() {

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("TABLE_ACTIVITY", "Successfully connected with server");
                    getActiveTable();
                }

                @Override
                public void successCallback(String channel, Object message) {
                    JSONObject msg;
                    try {
                        msg = new JSONObject(message.toString());
                        processMessage(msg);
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Could not parse pubnub message");
                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    // Handle PubNub message error
                }
            });
        }
        catch (PubnubException e) {
            // Handle PubNub initialization error
        }
    }

    private void getActiveTable() {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Endpoint.TABLE_ENDPOINT)
                .addHeader("Authorization", "Token " + authToken)
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
                        Log.d("DEBUG", "Table not found");
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } else {
                    Log.d("TABLE_ACTIVITY", "Obtained table information");
                    processTableData(response.body().string());
                }
            }
        });
    }

    private void processTableData(String data) {
        try {
            JSONObject json = new JSONObject(data);

            serverName = json.getString("server_name");
            addressTableCombo = json.getString("address_table_combo");

            prefs.setServerName(serverName);
            prefs.setAddrTableCombo(addressTableCombo);

            runOnUiThread(new DisplayTableInfo(json));
        } catch (JSONException e) {
            Log.d("DEBUG", "Unable to parse table data");
        }
    }

    class DisplayTableInfo implements Runnable {
        JSONObject data;

        DisplayTableInfo(JSONObject data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                String serverName = "Your server is " + data.getString("server_name");
                serverNameText.setText(serverName);

                boolean requestMade = data.getBoolean("request_made");

                if (requestMade) {
                    allowRequests = false;
                    tableServiceIcon.setImageResource(R.drawable.ic_waiting);
                    tableServiceText.setText(SERVICE_REQUESTED_TEXT);
                } else {
                    allowRequests = true;
                    tableServiceIcon.setImageResource(R.drawable.ic_table_service);
                    tableServiceText.setText(REQUEST_SERVICE_TEXT);
                }
                runOnUiThread(new UnhideButtons());
                Log.d("TABLE_ACTIVITY", "Updated table view");
            } catch (JSONException e) {
                Log.d("DEBUG", "Could not read table data");
            }
        }
    }

    private void processMessage(JSONObject msg) {
        try {
            final int code = msg.getInt("CODE");
            final int id = msg.getInt("USER_ID");
            final String tableId = msg.getString("ADDRESS_TABLE_COMBO");

            switch (code) {
                case TABLE_REQUEST_CODE:
                    if (id != userId && tableId.equals(addressTableCombo)) {
                        processRequestCode();
                    }
                    break;

                case TABLE_SERVICE_CODE:
                    if (tableId.equals(addressTableCombo)) {
                        processServiceCode();
                    }
                    break;

                default:
                    Log.d("DEBUG", "Unknown code");
                    break;
            }
        } catch (JSONException e) {
            Log.d("DEBUG", "Unable to parse pubnub message");
        }
    }

    private void processRequestCode() {
        allowRequests = false;
        runOnUiThread(new DisplayServiceRequested());
    }

    private void processServiceCode() {
        allowRequests = true;
        runOnUiThread(new DisplayRequestServiced());
    }

    class MakeToast implements Runnable {
        String msg;

        MakeToast(String m) {
            msg = m;
        }

        @Override
        public void run() {
            Toast.makeText(TableActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void requestTableService() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("address_table_combo", addressTableCombo)
                .build();

        Request request = new Request.Builder()
                .url(Endpoint.TABLE_REQUEST_ENDPOINT)
                .addHeader("Authorization", "Token " + authToken)
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
                    if (response.code() == 409) {
                        Log.d("DEBUG", "Table request already made");
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } else {
                    Log.d("DEBUG", "Table request made");
                    onRequestSuccessful();
                }
            }
        });
    }

    private void onRequestSuccessful() {
        // Notify channel of successful table request
        runOnUiThread(new DisplayServiceRequested());

        try {
            JSONObject data = new JSONObject();
            data.put("CODE", TABLE_REQUEST_CODE);
            data.put("USER_ID", userId);
            data.put("ADDRESS_TABLE_COMBO", addressTableCombo);

            com.pubnub.api.Callback callback = new com.pubnub.api.Callback() {
                public void successCallback(String channel, Object response) {
                    System.out.println(response.toString());
                }
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("DEBUG", "Failed to send pubnub table request message");
                }
            };

            pubnub.publish(serverId, data, callback);
        } catch (JSONException e) {
            Log.d("DEBUG", "Failed to create request data for pubnub");
        }
    }

    private void setupWidgets() {
        progressWheel = findViewById(R.id.progressWheel);
        tableServiceText = (TextView) findViewById(R.id.tableServiceText);
        tableServiceIcon = (ImageView) findViewById(R.id.tableServiceIcon);
        restaurantNameText = (TextView) findViewById(R.id.restaurantNameText);
        serverNameText = (TextView) findViewById(R.id.serverNameText);

        restaurantNameText.setText(restaurantName);
        serverNameText.setText("Your server is " + serverName);

        tableServiceBtn = findViewById(R.id.tableServiceBtn);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        tableServiceBtn.setVisibility(View.INVISIBLE);
        placeOrderBtn.setVisibility(View.INVISIBLE);
        tableServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowRequests) {
                    requestTableService();
                }
            }
        });
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final Button viewCheckBtn = (Button) findViewById(R.id.viewCheckBtn);
        viewCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NavManager(TableActivity.this).goToViewCheck();
            }
        });
    }

    class DisplayServiceRequested implements Runnable {
        @Override
        public void run() {
            tableServiceIcon.setImageResource(R.drawable.ic_waiting);
            tableServiceText.setText(SERVICE_REQUESTED_TEXT);
        }
    }
    class DisplayRequestServiced implements Runnable {
        @Override
        public void run() {
            tableServiceIcon.setImageResource(R.drawable.ic_table_service);
            tableServiceText.setText(REQUEST_SERVICE_TEXT);
        }
    }

    class UnhideButtons implements Runnable {
        @Override
        public void run() {
            progressWheel.setVisibility(View.INVISIBLE);
            placeOrderBtn.setVisibility(View.VISIBLE);
            tableServiceBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNetworkAvailable() {
        if (noNetworkDialog != null) {
            Log.d("DEBUG", "Network now available");
            noNetworkDialog.dismiss();
            noNetworkDialog = null;
            getActiveTable();
        }
    }

    @Override
    public void onNetworkUnavailable() {
        Log.d("DEBUG", "No network is available");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("NO NETWORK AVAILABLE");
        alertDialogBuilder.setMessage("There was some trouble connecting").setCancelable(false);
        noNetworkDialog = alertDialogBuilder.create();
        noNetworkDialog.show();
    }
}
