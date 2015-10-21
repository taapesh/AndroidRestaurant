package com.example.taapesh.androidrestaurant;

// Android imports
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// PubNub imports
import com.pubnub.api.Pubnub;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

// Java imports
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;




public class ServingActivity extends AppCompatActivity
{
    private Pubnub pubnub;
    private static final String PUBLISH_KEY = "pub-c-2344ebc7-3daf-4ffd-8c58-a8f809e85a2e";
    private static final String SUBSCRIBE_KEY = "sub-c-01429274-6938-11e5-a5be-02ee2ddab7fe";

    // Card view components
    private static final int PARTY_TITLE = 0;
    private static final int PARTY_SIZE = 1;

    // Message types
    private static final int CONNECT = 1;
    private static final int JOIN = 2;
    private static final int REQUEST = 3;
    private static final int FINISHED = 4;

    // Max allotted tables
    private final int MAX_TABLES = 3;

    ArrayList<Table> activeTableStack = new ArrayList<>();
    ArrayList<Table> requestTableStack = new ArrayList<>();
    ArrayList<Table> finishedTableStack = new ArrayList<>();

    // Table card views and availability
    CardView[] activeTableCardViews = new CardView[MAX_TABLES];
    CardView[] finishedTableCardViews = new CardView[MAX_TABLES];
    boolean[] isActiveTableViewUsed = new boolean[MAX_TABLES];
    boolean[] isFinishedTableViewUsed = new boolean[MAX_TABLES];

    private LinearLayout activeTablesRoot;
    private LinearLayout finishedTablesRoot;

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("RESUME", "Resuming activity");

        // Initialize Pubnub
        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        try
        {
            // Waiter subscribes to own channel
            pubnub.subscribe("woodhouse", new Callback()
            {
                public void successCallback(String channel, Object message)
                {
                    JSONObject msg;
                    try
                    {
                        msg = new JSONObject(message.toString());
                        processMessage(msg);
                    }
                    catch (JSONException e)
                    {
                        // pass
                    }
                }

                public void errorCallback(String channel, PubnubError error)
                {
                    // Handle PubNub message error
                }
            });
        }
        catch (PubnubException e)
        {
            // Handle PubNub initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i("CREATE", "Creating activity");
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

        activeTablesRoot = (LinearLayout) findViewById(R.id.activeTablesRoot);
        finishedTablesRoot = (LinearLayout) findViewById(R.id.finishedTablesRoot);

        for(int i = 0; i < MAX_TABLES; ++i)
        {
            activeTableCardViews[i] = (CardView) activeTablesRoot.getChildAt(i);
            finishedTableCardViews[i] = (CardView) finishedTablesRoot.getChildAt(i);
            finishedTableCardViews[i].setVisibility(View.GONE);
            activeTableCardViews[i].setVisibility(View.GONE);
            isFinishedTableViewUsed[i] = false;
            isActiveTableViewUsed[i] = false;
        }
    }

    // Process the received PubNub message
    private void processMessage(JSONObject msg)
    {
        int typeCode = -1;
        String type;
        try
        {
            type = msg.getString("type");

            if (type != null)
                typeCode = Integer.parseInt(type);

            switch (typeCode)
            {
                case CONNECT:
                {
                    // Customer is connecting for the first time
                    createAndDisplayTable(msg);
                    break;
                }

                case JOIN:
                {
                    // Another customer has joined a table
                    joinTable(msg);
                    break;
                }

                case REQUEST:
                {
                    // Table has made a request
                    Integer requestId = Integer.valueOf(msg.getString("request_id"));
                    makeTableRequest(requestId);
                    break;
                }

                case FINISHED:
                {
                    // Table is finished eating
                    Integer finishId = Integer.valueOf(msg.getString("finish_id"));
                    closeTable(finishId);
                    break;
                }
            }
        }
        catch (JSONException e)
        {
            // pass
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serving, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAndDisplayTable(JSONObject msg)
    {
        try
        {
            Integer userId = Integer.valueOf(msg.getString("user_id"));
            String firstName = msg.getString("first_name");
            String lastName = msg.getString("last_name");
            String email = msg.getString("email");

            int tableViewIdx = getFirstAvailableActiveView();
            isActiveTableViewUsed[tableViewIdx] = true;

            Table table = new Table(userId, email, firstName, lastName, activeTableCardViews[tableViewIdx], tableViewIdx);
            activeTableStack.add(table);

            // Inflate views
            runOnUiThread(new ActiveTableViewer(activeTableCardViews[tableViewIdx], table));
        }
        catch (JSONException e)
        {
            // pass
        }
    }

    private void makeTableRequest(int id)
    {
        for (Iterator<Table> iterator = activeTableStack.iterator(); iterator.hasNext(); )
        {
            Table t = iterator.next();
            if (t.getOwnerId() == id)
            {
                // Remove table from active stack and place in the request stack
                iterator.remove();
                requestTableStack.add(t);
                t.makeRequest();
                runOnUiThread(new ReorderTables());
                break;
            }
        }
    }

    private void closeTable(int id)
    {
        Table t = getTable(id);

        if (t != null)
        {
            activeTableStack.remove(t);
            finishedTableStack.add(t);
            t.closeTable();

            int tableViewIdx = getFirstAvailableFinishedView();
            isFinishedTableViewUsed[tableViewIdx] = true;

            runOnUiThread(new FinishedTableViewer(t, tableViewIdx, finishedTableCardViews[tableViewIdx]));

            setOnClick(finishedTableCardViews[tableViewIdx], t);
        }
    }

    private void joinTable(JSONObject msg)
    {
        try
        {
            Integer joiningId = Integer.valueOf(msg.getString("join_id"));
            Integer ownerId = Integer.valueOf(msg.getString("owner_id"));
            String joinFirstName = msg.getString("first_name");
            String joinLastName = msg.getString("last_name");

            Table t = getTable(ownerId);

            if (t != null)
                t.addMember(joiningId, joinFirstName, joinLastName);

            runOnUiThread(new IncreasePartyCount(t));

            // todo: update card view
        }
        catch (JSONException e)
        {
            // Invalid JSON format
        }
    }

    // Display a new active table on UI
    private class ActiveTableViewer implements Runnable
    {
        private final CardView cardView;
        private final Table table;

        ActiveTableViewer(CardView cardView, Table table)
        {
            this.cardView = cardView;
            this.table = table;
        }

        public void run()
        {
            fillActiveTableView(cardView, table);
            cardView.setVisibility(View.VISIBLE);

            // Remove and add to push card to bottom of linear layout
            activeTablesRoot.removeView(cardView);
            if (table.getView().getParent() != null)
                ((ViewGroup) table.getView().getParent()).removeView(table.getView());
            activeTablesRoot.addView(cardView);

            Toast.makeText(ServingActivity.this, "Customer connected", Toast.LENGTH_LONG).show();
        }
    }

    // Display a finished table on UI
    private class FinishedTableViewer implements Runnable
    {
        private final CardView cardView;
        private final Table table;
        private final int tableViewIdx;

        FinishedTableViewer(Table table, int tableViewIdx, CardView cardView)
        {
            this.cardView = cardView;
            this.table = table;
            this.tableViewIdx = tableViewIdx;
        }

        public void run()
        {
            // Free up previous card slot and hide it, set new view index
            isActiveTableViewUsed[table.getViewIdx()] = false;
            activeTableCardViews[table.getViewIdx()].setVisibility(View.GONE);
            table.setViewIdx(tableViewIdx);

            fillFinishedTableView(cardView, table);
            cardView.setVisibility(View.VISIBLE);

            if (cardView.getParent() != null)
                ((ViewGroup) cardView.getParent()).removeView(cardView);
            finishedTablesRoot.addView(cardView);

            Toast.makeText(ServingActivity.this, "Customer finished", Toast.LENGTH_LONG).show();
        }
    }

    // Display active tables in correct order
    private class ReorderTables implements Runnable
    {
        public void run()
        {
            activeTablesRoot.removeAllViews();

            // Display request tables first
            for(Table t: requestTableStack)
            {
                if (t.getView().getParent() != null)
                    ((ViewGroup) t.getView().getParent()).removeView(t.getView());
                activeTablesRoot.addView(t.getView());
            }

            for(Table t: activeTableStack)
            {
                if (t.getView().getParent() != null)
                    ((ViewGroup) t.getView().getParent()).removeView(t.getView());
                activeTablesRoot.addView(t.getView());
            }
        }
    }

    private class IncreasePartyCount implements Runnable
    {
        private final Table table;

        IncreasePartyCount(Table table)
        {
            this.table = table;
        }

        public void run()
        {
            LinearLayout cardLayout = (LinearLayout) table.getView().getChildAt(0);
            TextView partySizeText = (TextView) cardLayout.getChildAt(PARTY_SIZE);
            partySizeText.setText(table.getPartySize() + " in party");
        }
    }

    private int getFirstAvailableActiveView() {
        for (int i = 0; i < MAX_TABLES; ++i)
            if (!isActiveTableViewUsed[i]) return i;
        return -1;
    }

    private int getFirstAvailableFinishedView()
    {
        for (int i = 0; i < MAX_TABLES; ++i)
            if (!isFinishedTableViewUsed[i]) return i;
        return -1;
    }

    private Table getTable(int id)
    {
        for(Table t: activeTableStack)
            if (t.getOwnerId() == id) return t;

        for(Table t: requestTableStack)
            if(t.getOwnerId() == id) return t;

        for(Table t: finishedTableStack)
            if (t.getOwnerId() == id) return t;

        return null;
    }

    // Fill active table card information
    private void fillActiveTableView(CardView cardView, Table t)
    {
        LinearLayout cardLayout = (LinearLayout) cardView.getChildAt(0);
        TextView nameText = (TextView) cardLayout.getChildAt(PARTY_TITLE);
        TextView partySizeText = (TextView) cardLayout.getChildAt(PARTY_SIZE);

        nameText.setText(t.getOwnerFirstName());
        partySizeText.setText(t.getPartySize() + " in party");
    }

    // Fill finished table card information
    private void fillFinishedTableView(CardView cardView, Table t)
    {
        LinearLayout cardLayout = (LinearLayout) cardView.getChildAt(0);
        TextView tv = (TextView) cardLayout.getChildAt(PARTY_TITLE);
        tv.setText(t.getOwnerFirstName() + " is finished");
    }

    private void setOnClick(final View v, final Table table){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToChargeCustomer = new Intent(ServingActivity.this, ChargeCustomer.class);
                goToChargeCustomer.putExtra("table", table);
                startActivity(goToChargeCustomer);
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("STOP", "Stopping activity");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("PAUSE", "Pausing activity");
        pubnub.unsubscribe("woodhouse");
    }
}

