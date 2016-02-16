package com.taapesh.tablemate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.taapesh.tablemate.R;
import com.taapesh.tablemate.util.Endpoint;
import com.taapesh.tablemate.util.NavManager;
import com.taapesh.tablemate.util.NetworkStatus;
import com.taapesh.tablemate.util.PreferencesManager;
import com.taapesh.tablemate.util.ToolbarManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserHomeActivity extends AppCompatActivity {

    private NetworkStatus networkStatus;
    private boolean isNetworkAvailable;

    private Button startTableBtn;
    private View progressWheel;

    private  PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        new ToolbarManager(this).setupUserHomeToolbar();
        setupWidgets();

        networkStatus = new NetworkStatus(this);

        prefs = new PreferencesManager(this);
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
        final OkHttpClient client = new OkHttpClient();

        final String userId = prefs.getIdAsString();
        final String token = prefs.getToken();

        RequestBody formBody = new FormBody.Builder()
                .add("address_table_combo", Endpoint.TEST_TABLE_ADDR_COMBO)
                .add("restaurant_name", Endpoint.TEST_RESTAURANT_NAME)
                .add("restaurant_address", Endpoint.TEST_ADDRESS)
                .add("table_number", Endpoint.TEST_TABLE_NUM)
                .add("user_id", userId)
                .build();

        Request request = new Request.Builder()
                .url(Endpoint.CREATE_TABLE_ENDPOINT)
                .post(formBody)
                .addHeader("Authorization", "Token " + token)
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
        Log.d("DEBUG", "Table data: " + tableData);
        prefs.setRestaurantDetails(tableData);
        new NavManager(UserHomeActivity.this).goToTable();
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
