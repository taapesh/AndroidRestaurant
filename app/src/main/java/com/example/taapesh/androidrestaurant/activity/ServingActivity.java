package com.example.taapesh.androidrestaurant.activity;

// Android imports
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.taapesh.androidrestaurant.R;
import com.example.taapesh.androidrestaurant.object.Table;
import com.example.taapesh.androidrestaurant.util.CustomActionBar;
import com.example.taapesh.androidrestaurant.util.RestHelper;

// PubNub imports
import com.pubnub.api.Pubnub;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

// Java imports
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServingActivity extends AppCompatActivity {
    private static int testServerId = 4;
    private SharedPreferences sharedPreferences;

    private Pubnub pubnub;
    private static final String PUBLISH_KEY = "pub-c-2344ebc7-3daf-4ffd-8c58-a8f809e85a2e";
    private static final String SUBSCRIBE_KEY = "sub-c-01429274-6938-11e5-a5be-02ee2ddab7fe";
    private static final int MAX_TABLES = 3;

    // Card view components
    private static final int PARTY_TITLE = 0;
    private static final int PARTY_SIZE = 1;

    private ArrayList<Table> activeTableStack = new ArrayList<>();
    private ArrayList<Table> requestTableStack = new ArrayList<>();
    private ArrayList<Table> finishedTableStack = new ArrayList<>();

    CardView[] activeTableCardViews = new CardView[MAX_TABLES];
    CardView[] finishedTableCardViews = new CardView[MAX_TABLES];

    private LinearLayout activeTablesRoot;
    private LinearLayout finishedTablesRoot;

    @Override
    protected void onResume() {
        super.onResume();
        //String testServerId = PreferenceManager.getPreference(ServingActivity.this, PreferenceManager.USER_ID);

        // Initialize Pubnub
        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        try {
            // Waiter subscribes to own channel
            pubnub.subscribe("woodhouse", new Callback() {
                public void successCallback(String channel, Object message) {
                    JSONObject msg;
                    try {
                        msg = new JSONObject(message.toString());
                        //processMessage(msg);
                    }
                    catch (JSONException e) {
                        // pass
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

        new GetServerTables().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serving);
        CustomActionBar.setupActionBar(getSupportActionBar(), R.string.title_serving, R.layout.custom_action_bar);

        activeTablesRoot = (LinearLayout)findViewById(R.id.activeTablesRoot);
        finishedTablesRoot = (LinearLayout)findViewById(R.id.finishedTablesRoot);

        for(int i = 0; i < MAX_TABLES; ++i) {
            activeTableCardViews[i] = (CardView) activeTablesRoot.getChildAt(i);
            finishedTableCardViews[i] = (CardView) finishedTablesRoot.getChildAt(i);
            activeTableCardViews[i].setVisibility(View.GONE);
            finishedTableCardViews[i].setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        pubnub.unsubscribe("woodhouse");
    }

    class GetServerTables extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... fields) {
            return RestHelper.getServerTables(testServerId);
        }

        protected void onPostExecute(JSONArray tablesResult) {
            // active tables ordered by earliest creation time first
            // request tables ordered by earliest request first
            // finished tables ordered by earliest finish time first
            try {
                int len = tablesResult.length();
                resetTables();

                for (int i = 0; i < len; i++) {
                    JSONObject tableData = tablesResult.getJSONObject(i);
                    //todo: insert ordered into the table stacks
                    Table table = new Table(tableData);
                    if (table.hasMadeRequest()) {
                        requestTableStack.add(table);
                    }
                    else if (table.hasFinished()) {
                        finishedTableStack.add(table);
                    }
                    else {
                        activeTableStack.add(table);
                    }
                }

                runOnUiThread(new DisplayTables());
            }
            catch (JSONException e) {
                Log.i("JSONException", e.toString());
            }
        }
    }

    private void resetTables() {
        for(int i = 0; i < MAX_TABLES; ++i) {
            activeTableStack.clear();
            requestTableStack.clear();
            finishedTableStack.clear();
        }
    }

    private class DisplayTables implements Runnable {
        public void run() {
            int activeSlotsUsed = 0;
            int finishedSlotsUsed = 0;

            for(Table t: requestTableStack) {
                fillActiveTableView(activeTableCardViews[activeSlotsUsed], t);
                activeTableCardViews[activeSlotsUsed].setVisibility(View.VISIBLE);
                activeSlotsUsed++;
            }
            for(Table t: activeTableStack) {
                fillActiveTableView(activeTableCardViews[activeSlotsUsed], t);
                activeTableCardViews[activeSlotsUsed].setVisibility(View.VISIBLE);
                activeSlotsUsed++;
            }
            for(Table t: finishedTableStack) {
                fillFinishedTableView(finishedTableCardViews[finishedSlotsUsed], t);
                finishedTableCardViews[finishedSlotsUsed].setVisibility(View.VISIBLE);
                setOnClick(finishedTableCardViews[finishedSlotsUsed], t);
                finishedSlotsUsed++;
            }
        }
    }

    private void fillActiveTableView(CardView cardView, Table t) {
        // Fill active table card information
        LinearLayout cardLayout = (LinearLayout) cardView.getChildAt(0);
        TextView nameText = (TextView) cardLayout.getChildAt(PARTY_TITLE);
        TextView partySizeText = (TextView) cardLayout.getChildAt(PARTY_SIZE);
        nameText.setText(t.getOwnerFirstName());
        partySizeText.setText(t.getPartySize() + " in party");
    }

    private void fillFinishedTableView(CardView cardView, Table t) {
        // Fill finished table card information
        LinearLayout cardLayout = (LinearLayout) cardView.getChildAt(0);
        TextView tv = (TextView) cardLayout.getChildAt(PARTY_TITLE);
        tv.setText(t.getOwnerFirstName() + " is finished");
    }

    private void setOnClick(final View v, final Table table) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToChargeCustomer = new Intent(ServingActivity.this, ChargeCustomerActivity.class);
                goToChargeCustomer.putExtra("table", table);
                startActivity(goToChargeCustomer);
            }
        });
    }
}

