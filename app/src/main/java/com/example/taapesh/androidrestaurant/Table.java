package com.example.taapesh.androidrestaurant;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import java.util.HashMap;

public class Table implements Parcelable {
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
    private HashMap<Integer, Integer> members = new HashMap<>(); // Map members to their portion of the bill

    public Table(Integer userId, String email, String firstName, String lastName, CardView cardView, int viewIdx) {
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

    public void serveRequest() {
        requestMade = false;
        timeOfRequest = -1;
    }

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

    public CardView getView() {
        return cardView;
    }

    public long getTimeSinceRequest() {
        long timeNow = System.currentTimeMillis();
        long secondsElapsed = (timeNow - timeOfRequest)/1000;
        return secondsElapsed;
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
