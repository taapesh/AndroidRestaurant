package com.taapesh.tablemate.util;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;

import com.taapesh.tablemate.activity.AddPaymentActivity;
import com.taapesh.tablemate.activity.EditProfileActivity;
import com.taapesh.tablemate.activity.FinishAndPayActivity;
import com.taapesh.tablemate.activity.PaymentActivity;
import com.taapesh.tablemate.activity.ProfileActivity;
import com.taapesh.tablemate.activity.ReceiptsActivity;
import com.taapesh.tablemate.activity.ServerActivity;
import com.taapesh.tablemate.activity.ServerHomeActivity;
import com.taapesh.tablemate.activity.StartActivity;
import com.taapesh.tablemate.activity.TableActivity;
import com.taapesh.tablemate.activity.UserHomeActivity;
import com.taapesh.tablemate.activity.UserLoginActivity;
import com.taapesh.tablemate.activity.UserRegistrationActivity;


public class NavManager {

    private Activity previousActivity;

    public NavManager(Activity previousActivity) {
        this.previousActivity = previousActivity;
    }

    public void goBack(final int fromActivityCode) {
        Intent nextActivity = null;

        switch(fromActivityCode) {
            case ActivityCode.LOGIN_ACTIVITY:
                nextActivity = new Intent(previousActivity, StartActivity.class);
                break;
            case ActivityCode.REGISTRATION_ACTIVITY:
                nextActivity = new Intent(previousActivity, StartActivity.class);
                break;
            case ActivityCode.ADD_PAYMENT_ACTIVITY:
                nextActivity = new Intent(previousActivity, PaymentActivity.class);
                break;
            case ActivityCode.EDIT_PROFILE_ACTIVITY:
                nextActivity = new Intent(previousActivity, ProfileActivity.class);
                break;
            case ActivityCode.RECEIPTS_ACTIVITY:
                nextActivity = new Intent(previousActivity, UserHomeActivity.class);
                break;
            case ActivityCode.PROFILE_ACTIVITY:
                nextActivity = new Intent(previousActivity, UserHomeActivity.class);
                break;
            case ActivityCode.PAYMENT_ACTIVITY:
                nextActivity = new Intent(previousActivity, UserHomeActivity.class);
                break;
            case ActivityCode.FINISH_ACTIVITY:
                nextActivity = new Intent(previousActivity, TableActivity.class);
                break;
            case ActivityCode.USER_HOME_ACTIVITY:
                // Exit app
                break;
            case ActivityCode.SERVER_HOME_ACTIVITY:
                // Exit app
                break;
            case ActivityCode.SERVER_ACTIVITY:
                // Exit app
                break;
            case ActivityCode.TABLE_ACTIVITY:
                // Exit app
                break;
            case ActivityCode.START_ACTIVITY:
                // Exit app
                break;
            default:
                Log.d("DEBUG", "Invalid activity code");
                break;
        }

        if (nextActivity != null) {
            previousActivity.startActivity(nextActivity);
            previousActivity.finish();
        }
    }

    public void goToUserHome() {
        Intent nextActivity = new Intent(previousActivity, UserHomeActivity.class);
        previousActivity.startActivity(nextActivity);
        previousActivity.finish();
    }

    public void goToPayment() {
        Intent nextActivity = new Intent(previousActivity, PaymentActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToStart() {
        Intent nextActivity = new Intent(previousActivity, StartActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToServer() {
        Intent nextActivity = new Intent(previousActivity, ServerActivity.class);
        previousActivity.startActivity(nextActivity);
        previousActivity.finish();
    }

    public void goToTable() {
        Intent nextActivity = new Intent(previousActivity, TableActivity.class);
        previousActivity.startActivity(nextActivity);
        previousActivity.finish();
    }

    public void goToViewCheck() {
        Intent nextActivity = new Intent(previousActivity, FinishAndPayActivity.class);
        previousActivity.startActivity(nextActivity);
        previousActivity.finish();
    }

    public void goToAddPayment() {
        Intent nextActivity = new Intent(previousActivity, AddPaymentActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToProfile() {
        Intent nextActivity = new Intent(previousActivity, ProfileActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToEditProfile() {
        Intent nextActivity = new Intent(previousActivity, EditProfileActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToReceipts() {
        Intent nextActivity = new Intent(previousActivity, ReceiptsActivity.class);
        previousActivity.startActivity(nextActivity);
    }
    public void goToServerHome() {
        Intent nextActivity = new Intent(previousActivity, ServerHomeActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToUserLogin() {
        Intent nextActivity = new Intent(previousActivity, UserLoginActivity.class);
        previousActivity.startActivity(nextActivity);
    }

    public void goToUserRegistration() {
        Intent nextActivity = new Intent(previousActivity, UserRegistrationActivity.class);
        previousActivity.startActivity(nextActivity);
    }
}
