package com.example.taapesh.androidrestaurant;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private static String ownerEmail;
    private static String ownerFirstName;
    private static String ownerLastName;
    private static int partySize;
    private static Integer timeSinceLastRequest;
    private static boolean requestMade;
    private static boolean isFinished;

    // Map members at table to their percentage of the check, initially 0
    private static Map<Integer, Integer> members = new HashMap<>();

    /* Table constructor */
    public Table(Integer userId, String email, String firstName, String lastName) {
        partySize = 1;
        members.put(userId, 100);
        ownerEmail = email;
        ownerFirstName = firstName;
        ownerLastName = lastName;
        requestMade = false;
        isFinished = false;
        timeSinceLastRequest = 0;
    }

    public int getSize() {
        return partySize;
    }

    public void addMember(int userId, String firstName, String lastName) {
        members.put(userId, 0);
        partySize += 1;
    }

    /* Update a member's percentage of the check and update other members accordingly */
    public void changePercentage(Integer userId, Integer percent) {

    }

    /* Request was made by this table */
    public void makeRequest() {
        requestMade = true;
    }

    /* Table is finished eating now, set flag to prepare for payment */
    public void closeTable() {
        isFinished = true;
    }
}
