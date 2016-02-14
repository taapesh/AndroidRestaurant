package com.example.taapesh.androidrestaurant.util;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taapesh.androidrestaurant.R;


public class ToolbarManager {

    public static final String LOGIN_ACTIVITY_TITLE = "Sign In";
    public static final String REGISTRATION_ACTIVITY_TITLE = "Create an Account";
    public static final String EDIT_PROFILE_ACTIVITY_TITLE = "Edit Account";
    public static final String PROFILE_ACTIVITY_TITLE = "Account";
    public static final String RECEIPTS_ACTIVITY_TITLE = "Receipts";
    public static final String PAYMENT_ACTIVITY_TITLE = "Payment";
    public static final String ADD_PAYMENT_ACTIVITY_TITLE = "Add Payment";
    public static final String TABLE_ACTIVITY_TITLE = "Dining";
    public static final String SERVER_ACTIVITY_TITLE = "Serving";
    public static final String FINISH_ACTIVITY_TITLE = "Viewing Bill";

    private AppCompatActivity activity;
    private Toolbar toolbar;
    private DrawerLayout sideMenuLayout;

    public ToolbarManager(AppCompatActivity activity) {
        this.activity = activity;
        this.toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
    }

    public void setupGoBackToolbar(String title, final int activityCode) {
        // Set toolbar title
        TextView toolbarTitle = (TextView) activity.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);

        //  Setup go back icon
        ImageView backIcon = (ImageView) activity.findViewById(R.id.goBackIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NavManager(activity).goBack(activityCode);
            }
        });
    }

    public void setupTableToolbar(String title) {
        // Set toolbar title
        TextView toolbarTitle = (TextView) activity.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);

        // Setup menu toggle icon
        ImageView menuToggleIcon = (ImageView) activity.findViewById(R.id.menuToggleIcon);
        menuToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        // Setup dining side menu
        sideMenuLayout = (DrawerLayout) activity.findViewById(R.id.sideMenuLayout);

        TextView nameText = (TextView) activity.findViewById(R.id.myNameText);
        SharedPreferences prefs = activity.getSharedPreferences(PreferencesManager.MY_PREFERENCES, Context.MODE_PRIVATE);
        String firstName = prefs.getString(PreferencesManager.FIRST_NAME, null);
        String lastName = prefs.getString(PreferencesManager.LAST_NAME, null);

        if (firstName != null && lastName != null) {
            nameText.setText(firstName + " " + lastName);
        }
    }

    public void setupServerToolbar(String title) {
        // Set toolbar title
        TextView toolbarTitle = (TextView) activity.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);

        // Setup menu toggle icon
        ImageView menuToggleIcon = (ImageView) activity.findViewById(R.id.menuToggleIcon);
        menuToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        // Setup dining side menu
        sideMenuLayout = (DrawerLayout) activity.findViewById(R.id.sideMenuLayout);

        TextView nameText = (TextView) activity.findViewById(R.id.myNameText);
        SharedPreferences prefs = activity.getSharedPreferences(PreferencesManager.MY_PREFERENCES, Context.MODE_PRIVATE);
        String firstName = prefs.getString(PreferencesManager.FIRST_NAME, null);
        String lastName = prefs.getString(PreferencesManager.LAST_NAME, null);

        if (firstName != null && lastName != null) {
            nameText.setText(firstName + " " + lastName);
        }
    }

    public void setupServerHomeToolbar() {
        // Setup menu toggle icon
        final ImageButton menuToggleIcon = (ImageButton) activity.findViewById(R.id.menuToggleIcon);
        menuToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        // Setup default side menu
        sideMenuLayout = (DrawerLayout) activity.findViewById(R.id.sideMenuLayout);

        TextView nameText = (TextView) activity.findViewById(R.id.myNameText);
        SharedPreferences prefs = activity.getSharedPreferences(PreferencesManager.MY_PREFERENCES, Context.MODE_PRIVATE);
        String firstName = prefs.getString(PreferencesManager.FIRST_NAME, null);
        String lastName = prefs.getString(PreferencesManager.LAST_NAME, null);

        if (firstName != null && lastName != null) {
            nameText.setText(firstName + " " + lastName);
        }
    }

    public void setupUserHomeToolbar() {
        // Setup menu toggle icon
        final ImageButton menuToggleIcon = (ImageButton) activity.findViewById(R.id.menuToggleIcon);
        menuToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        // Setup default side menu
        sideMenuLayout = (DrawerLayout) activity.findViewById(R.id.sideMenuLayout);

        TextView nameText = (TextView) activity.findViewById(R.id.myNameText);
        SharedPreferences prefs = activity.getSharedPreferences(PreferencesManager.MY_PREFERENCES, Context.MODE_PRIVATE);
        String firstName = prefs.getString(PreferencesManager.FIRST_NAME, null);
        String lastName = prefs.getString(PreferencesManager.LAST_NAME, null);

        if (firstName != null && lastName != null) {
            nameText.setText(firstName + " " + lastName);
        }

        // Setup default side menu links
        final View paymentMenu = activity.findViewById(R.id.paymentMenuOption);
        paymentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                new NavManager(activity).goToPayment();
            }
        });

        final View receiptsMenu = activity.findViewById(R.id.receiptsMenuOption);
        receiptsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                new NavManager(activity).goToReceipts();
            }
        });
    }

    private void closeMenu() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                sideMenuLayout.closeDrawer(GravityCompat.START);
            }
        }, 200);
    }

    private void toggleDrawer() {
        if (sideMenuLayout.isDrawerOpen(GravityCompat.START)) {
            sideMenuLayout.closeDrawer(GravityCompat.START);
        } else {
            sideMenuLayout.openDrawer(GravityCompat.START);
        }
    }

}
