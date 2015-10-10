package com.example.taapesh.androidrestaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.Pubnub;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Card imports
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;


public class ServingActivity extends AppCompatActivity {
    private static Pubnub pubnub;
    private static final String PUBLISH_KEY = "pub-c-2344ebc7-3daf-4ffd-8c58-a8f809e85a2e";
    private static final String SUBSCRIBE_KEY = "sub-c-01429274-6938-11e5-a5be-02ee2ddab7fe";

    // Message types
    private static final int CONNECT = 1;
    private static final int JOIN = 2;
    private static final int REQUEST = 3;
    private static final int FINISHED = 4;

    private static int numTables = 0;
    // Map userID to their table
    Map<Integer, Table> parties = new HashMap<Integer, Table>();

    // Map the cardview to the user id that the view is assigned,
    // If not assigned, then that cardview is free to be used
    Map<CardView, Integer> activeTableViews = new HashMap<CardView, Integer>();
    Map<CardView, Integer> finishedTableViews = new HashMap<CardView, Integer>();

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
                    JSONObject msg;
                    try {
                        msg = new JSONObject(message.toString());
                        processMessage(msg);
                    } catch (JSONException e) {
                        // pass
                    }
                }

                public void errorCallback(String channel, PubnubError error) {
                    Log.i("PUBNUB_ERROR", error.getErrorString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
            Log.i("PUBNUB_EXCEPTION", e.toString());
        }

        // Create a Card
        Card card = new Card(this, R.layout.row_card);

        // Create a CardHeader
        CardHeader header = new CardHeader(this);
        header.setTitle("Hello world");

        card.setTitle("Simple card demo");
        CardThumbnail thumb = new CardThumbnail(this);
        thumb.setDrawableResource(R.drawable.user_xhdpi);

        card.addCardThumbnail(thumb);

        // Add Header to card
        card.addCardHeader(header);

        // Set card in the cardView
        CardView cardView = (CardView) findViewById(R.id.activeTable1);
        cardView.setCard(card);
    }


    private void processMessage(JSONObject msg) {
        int typeCode = -1;
        String type;
        try {
            type = msg.getString("type");

            if (type != null) {
                typeCode = Integer.parseInt(type);
                Log.i("TYPE", type);
            }
            switch (typeCode) {
                case CONNECT:
                    // client is connecting for the first time
                    Integer userId = Integer.valueOf(msg.getString("user_id"));
                    String firstName = msg.getString("first_name");
                    String lastName = msg.getString("last_name");
                    String email = msg.getString("email");

                    // Create new table and map it to userId
                    Table table = new Table(userId, email, firstName, lastName);
                    parties.put(userId, table);
                    displayTables();

                    /* Test: request to connect was received */
                    ServingActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ServingActivity.this, "Customer connected", Toast.LENGTH_LONG).show();
                        }
                    });
                    /* End test receive connection */
                    break;
                case JOIN:
                    // another client has joined a table
                    Integer joiningId = Integer.valueOf(msg.getString("join_id"));
                    Integer ownerId = Integer.valueOf(msg.getString("owner_id"));
                    String joinFirstName = msg.getString("first_name");
                    String joinLastName = msg.getString("last_name");
                    parties.get(ownerId).addMember(joiningId, joinFirstName, joinLastName);
                    break;
                case REQUEST:
                    // a client has made a request
                    Integer requestId = Integer.valueOf(msg.getString("request_id"));
                    parties.get(requestId).makeRequest();
                    // TODO: visual cues when party makes request, modify request stack
                    break;
                case FINISHED:
                    // client is finished eating
                    // TODO: visual cues when party is finished, make necessary changes to data structures
                    Integer finishId = Integer.valueOf(msg.getString("finish_id"));
                    parties.get(finishId).closeTable();
                    break;
            }
        } catch (JSONException e) {
            // pass
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

    private void displayTables() {
        for(Map.Entry<Integer, Table> tableEntry : parties.entrySet()) {
            Table t = tableEntry.getValue();
            Log.i("TABLE", t.getOwnerFirstName() + " : " + t.getSize());


        }
    }
}
