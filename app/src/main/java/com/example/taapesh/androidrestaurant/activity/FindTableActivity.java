package com.example.taapesh.androidrestaurant.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.object.Table;
import com.example.taapesh.androidrestaurant.util.CustomActionBar;
import com.example.taapesh.androidrestaurant.util.RestHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FindTableActivity extends AppCompatActivity {

    private static String[] sampleServers = {"Bob", "Alice", "John"};
    private Button startTableBtn;
    private static final int testUserId = 2;
    private static final String addr = "1234 Restaurant St.";
    private static final int tableNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_table);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_find_table, R.layout.custom_action_bar);
        startTableBtn = (Button)findViewById(R.id.startTableBtn);
        startTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Connect to waiter now
                // If table at this restaurant already exists, join it. If not, create it
                // Go to table activity
                new CreateOrJoinTable().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_table, menu);
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

    class CreateOrJoinTable extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... fields) {
            return RestHelper.createOrJoinTable(addr, tableNum);
        }

        protected void onPostExecute(JSONObject tableResult) {
            try {
                Intent goToTable = new Intent(FindTableActivity.this, TableActivity.class);
                startActivity(goToTable);
            }
            catch (Exception e) {
                Log.i("JSONException", e.toString());
            }
        }
    }
}
