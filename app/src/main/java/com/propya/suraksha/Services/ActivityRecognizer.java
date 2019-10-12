package com.propya.suraksha.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.propya.suraksha.Helpers.NotificationHelpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityRecognizer extends IntentService {

    Context context;
    public static final int REQUEST_CODE = 69;

    public ActivityRecognizer(String name) {
        super(name);
    }

    public ActivityRecognizer(){
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        NotificationHelpers helpers = new NotificationHelpers(this);
        Notification build = helpers.getNotiSmall("assa", "assa", null).build();
        startForeground(8,build);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ActivityTransitionRequest request = buildTransitionRequest();
        Intent callback = new Intent(context,ActivityCallback.class);
        PendingIntent pendingIntent = PendingIntent.getService(context,REQUEST_CODE,callback,0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            pendingIntent= PendingIntent.getForegroundService(context,REQUEST_CODE,callback,0);
        }
        Task<Void> voidTask = ActivityRecognition.getClient(context).requestActivityTransitionUpdates(request, pendingIntent);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Successful Activity Recognition", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    public static ActivityTransitionRequest buildTransitionRequest()
    {
        List<ActivityTransition> transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());


        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());


        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.STILL).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.STILL).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());


        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());
        return new ActivityTransitionRequest(transitions);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        if(ActivityTransitionResult.hasResult(intent))
//            {
//                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
//                List<ActivityTransitionEvent> eventList;
//                eventList=result.getTransitionEvents();//ascending order
//                Collections.reverse(eventList);//events in descending order
//                for (ActivityTransitionEvent event : eventList)
//                {
//                    event.getActivityType();//running or not
//                    //TODO based on type and time decide what to do
//                    //if type==running +start and loc ==unsafe start unsafe flow
//                    //if running +start and loc = safe then more time for notif
//                    //if driving then start driving flow
//                    //if start event has occured end must occur too
//                    event.getElapsedRealTimeNanos();//kitna time hua
//                    event.getTransitionType();//start or end of event
//                    Toast.makeText(context,"Activity : "+event.getActivityType(),Toast.LENGTH_LONG).show();
//
//                }
//            }
    }
}
