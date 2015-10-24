package com.example.taapesh.androidrestaurant.util;

import android.view.View;
import android.widget.TextView;
import com.example.taapesh.androidrestaurant.R;

public class CustomActionBar {
    public static void setupActionBar(final android.support.v7.app.ActionBar actionBar, int title, int layout) {
        if (actionBar != null) {
            actionBar.setCustomView(layout);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);

            if (title != -1) {
                View v = actionBar.getCustomView();
                TextView actionBarText = (TextView) v.findViewById(R.id.actionBarTitle);
                actionBarText.setText(title);
            }
        }
    }
}
