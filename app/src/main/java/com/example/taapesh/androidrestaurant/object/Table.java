package com.example.taapesh.androidrestaurant.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Table implements Parcelable {

    private int ownerId;
    private int serverId;
    private int partySize;
    private String ownerEmail;
    private String ownerFirstName;
    private String ownerLastName;
    private boolean requestMade;
    private boolean isFinished;
    private long timeOfFinish;
    private long timeOfRequest;
    private CardView cardView;
    private int viewIdx;
    private HashMap<Integer, Integer> members = new HashMap<>(); // Map members to their portion of the bill

    public Table(JSONObject tableData) throws JSONException {
        ownerId = tableData.getInt("ownerId");
        serverId = tableData.getInt("serverId");
        partySize = tableData.getInt("partySize");
        ownerEmail = tableData.getString("ownerEmail");
        ownerFirstName = tableData.getString("ownerFirstName");
        ownerLastName = tableData.getString("ownerLastName");
        requestMade = tableData.getBoolean("requestMade");
        isFinished = tableData.getBoolean("isFinished");
        timeOfFinish = tableData.getLong("timeOfFinish");
        timeOfRequest = tableData.getLong("timeOfRequest");
    }

    public Table(JSONObject ownerDetails, CardView cardView, int viewIdx) throws JSONException {
        partySize = 1;
        members.put(ownerId, 100);
        timeOfRequest = -1;
        requestMade = false;
        isFinished = false;
        this.cardView = cardView;
        this.viewIdx = viewIdx;
        this.ownerId = ownerDetails.getInt("user_id");
        this.ownerEmail = ownerDetails.getString("email");
        this.ownerFirstName = ownerDetails.getString("first_name");
        this.ownerLastName = ownerDetails.getString("last_name");

    }

    public void addMember(int userId, String firstName, String lastName) {
        members.put(userId, 0);
        partySize += 1;
    }

    public void changePercentage(Integer userId, Integer percent) {

    }

    public void makeRequest() {
        requestMade = true;
        timeOfRequest = System.currentTimeMillis();
    }

    public void completeRequest() {
        requestMade = false;
        timeOfRequest = -1;
    }

    public void closeTable() {
        isFinished = true;
        timeOfFinish = System.currentTimeMillis();
    }

    public int getPartySize() {
        return partySize;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public boolean hasMadeRequest() {
        return requestMade;
    }

    public boolean hasFinished() {
        return isFinished;
    }

    public CardView getView() {
        return cardView;
    }

    // Returns time since request was made in seconds
    public long getTimeSinceRequest() {
        return (System.currentTimeMillis() - timeOfRequest)/1000;
    }

    public long getTimeSinceFinish() {
        return (System.currentTimeMillis() - timeOfFinish)/1000;
    }

    public int getViewIdx() {
        return viewIdx;
    }

    public void setViewIdx(int idx) {
        viewIdx = idx;
    }

    // Implementing Parcelable

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // Write table data to parcel
        out.writeInt(ownerId);
        out.writeString(ownerEmail);
        out.writeString(ownerFirstName);
        out.writeString(ownerLastName);
        out.writeInt(partySize);
    }

    public static final Parcelable.Creator<Table> CREATOR = new Parcelable.Creator<Table>() {
        // Recreate table from parcel
        public Table createFromParcel(Parcel in) {
            return new Table(in);
        }

        public Table[] newArray(int size) {
            return new Table[size];
        }
    };

    private Table(Parcel in) {
        // Table constructor using Parcel
        this.ownerId = in.readInt();
        this.ownerEmail = in.readString();
        this.ownerFirstName = in.readString();
        this.ownerLastName = in.readString();
        this.partySize = in.readInt();
    }
}
