package com.propya.suraksha.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.propya.suraksha.Helpers.NotificationHelpers;

public class OnCompleted extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Callback received", Toast.LENGTH_SHORT).show();
        Log.i("OnBoot","callBacked");
        NotificationHelpers helpers = new NotificationHelpers(context);
        helpers.generateForeground(null);
        FirebaseMessaging.getInstance().subscribeToTopic("help");
    }
}
