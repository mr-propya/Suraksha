package com.propya.suraksha.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.propya.suraksha.Helpers.NotificationHelpers;

public class ActivityCallback extends Service {
    public ActivityCallback() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            if (ActivityTransitionResult.hasResult(intent)) {
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                    NotificationHelpers helpers = new NotificationHelpers(this);
                    //TODO CHECK ACTIVITY HERE AND LOCATION AND GET

                    if(event.getActivityType()== DetectedActivity.RUNNING)
                    {

                    }
                    helpers.timerNotif(2,this,30000);


                }
            }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelpers helpers = new NotificationHelpers(this);
        helpers.generateForeground(null);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
