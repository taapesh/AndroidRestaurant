package com.example.taapesh.androidrestaurant;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import java.util.HashMap;


public class Table implements Parcelable
{
    private int ownerId;
    private String ownerEmail;
    private String ownerFirstName;
    private String ownerLastName;
    private int partySize;
    private boolean requestMade;
    private long timeOfRequest;
    private boolean isFinished;
    private CardView cardView;
    private int viewIdx;
    // Map members at table to their percentage of the check, initially 0
    private HashMap<Integer, Integer> members = new HashMap<>();

    /* Table constructor */
    public Table(Integer userId, String email, String firstName, String lastName, CardView cardView, int viewIdx)
    {
        ownerId = userId;
        partySize = 1;
        members.put(userId, 100);
        ownerEmail = email;
        ownerFirstName = firstName;
        ownerLastName = lastName;
        requestMade = false;
        isFinished = false;
        timeOfRequest = -1;
        this.cardView = cardView;
        this.viewIdx = viewIdx;
    }

    public void addMember(int userId, String firstName, String lastName)
    {
        members.put(userId, 0);
        partySize += 1;
    }

    /* Update a member's percentage of the check and update other members accordingly */
    public void changePercentage(Integer userId, Integer percent) {

    }

    /* Request was made by this table */
    public void makeRequest()
    {
        requestMade = true;
        timeOfRequest = System.currentTimeMillis();
    }

    public void serveRequest()
    {
        requestMade = false;
        timeOfRequest = -1;
    }

    /* Table is finished eating now, set flag to prepare for payment */
    public void closeTable() {
        isFinished = true;
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

    public boolean getRequestStatus() {
        return requestMade;
    }

    public CardView getView()
    {
        return cardView;
    }

    public long getTimeSinceRequest()
    {
        long timeNow = System.currentTimeMillis();
        long secondsElapsed = (timeNow - timeOfRequest)/1000;
        return secondsElapsed;
    }

    public int getViewIdx()
    {
        return viewIdx;
    }

    public void setViewIdx(int idx)
    {
        viewIdx = idx;
    }

    /* Implementing Parcelable */

    public int describeContents() {
        return 0;
    }

    // Write table data to parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ownerId);
        out.writeString(ownerEmail);
        out.writeString(ownerFirstName);
        out.writeString(ownerLastName);
        out.writeInt(partySize);
    }

    // Recreate table from parcel
    public static final Parcelable.Creator<Table> CREATOR = new Parcelable.Creator<Table>() {
        public Table createFromParcel(Parcel in) {
            return new Table(in);
        }

        public Table[] newArray(int size) {
            return new Table[size];
        }
    };

    // Table constructor using Parcel
    private Table(Parcel in) {
        this.ownerId = in.readInt();
        this.ownerEmail = in.readString();
        this.ownerFirstName = in.readString();
        this.ownerLastName = in.readString();
        this.partySize = in.readInt();
    }
}
