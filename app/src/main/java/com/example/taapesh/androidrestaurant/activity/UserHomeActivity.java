package com.example.taapesh.androidrestaurant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.util.NavManager;
import com.example.taapesh.androidrestaurant.util.NetworkStatus;
import com.example.taapesh.androidrestaurant.util.PreferencesManager;
import com.example.taapesh.androidrestaurant.util.ToolbarManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserHomeActivity extends AppCompatActivity {

    private static final String CREATE_TABLE_ENDPOINT = "http://safe-springs-46272.herokuapp.com/create_table/";
    private static final String TEST_ADDRESS = "1234 Restaurant St.";
    private static final String TEST_NAME = "Awesome Restaurant";
    private static final String TEST_TABLE_NUM = "1";
    private static final String TEST_TABLE_ADDR_COMBO = "1_1234 Restaurant St.";

    private NetworkStatus networkStatus;
    private boolean isNetworkAvailable;

    private Button startTableBtn;
    private View progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        new ToolbarManager(this).setupUserHomeToolbar();
        setupWidgets();

        networkStatus = new NetworkStatus(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isNetworkAvailable = networkStatus.isOnline();
        if (!isNetworkAvailable) {
            Log.d("DEBUG", "No network available");
        }
    }

    private void setupWidgets() {
        progressWheel = findViewById(R.id.progressWheel);
        progressWheel.setVisibility(View.INVISIBLE);
        startTableBtn = (Button) findViewById(R.id.startTableBtn);
        startTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start or join a table
                // TODO: Establish table number before attempting to create table
                startTableBtn.setVisibility(View.INVISIBLE);
                progressWheel.setVisibility(View.VISIBLE);
                createOrJoinTable();
            }
        });
    }

    private void createOrJoinTable() {
        Request request = null;
        final OkHttpClient client = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        PreferencesManager prefs = new PreferencesManager(this);
        String userId = prefs.getIdAsString();
        String token = prefs.getToken();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("address_table_combo", TEST_TABLE_ADDR_COMBO);
            jsonObject.put("restaurant_name", TEST_NAME);
            jsonObject.put("restaurant_address", TEST_ADDRESS);
            jsonObject.put("table_number", TEST_TABLE_NUM);
            jsonObject.put("user_id", userId);

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
            request = new Request.Builder()
                    .url(CREATE_TABLE_ENDPOINT)
                    .post(requestBody)
                    .addHeader("Authorization", "Token " + token)
                    .build();
        } catch (JSONException e) {
            Log.d("DEBUG", "Unable to create json table data");
        }

        if (request == null) {
            onFailedToCreateTable();
            return;
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (response.code() == 409) {
                        Log.d("DEBUG", "409 Conflict: Unable to create table");
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                    onFailedToCreateTable();
                } else {
                    onTableCreated(response.body().string());
                }
            }
        });
    }

    private void onTableCreated(String tableData) {
        // TODO: validate response and do something
        try {
            JSONObject json = new JSONObject(tableData);
            new NavManager(UserHomeActivity.this).goToTable(
                    json.getString("restaurant_name"), json.getString("server_name"));
        } catch (JSONException e) {
            Log.d("DEBUG", "Unable to parse table data");
        }
    }

    private void onFailedToCreateTable() {
        progressWheel.setVisibility(View.INVISIBLE);
        startTableBtn.setVisibility(View.VISIBLE);
        // TODO: Display error
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
