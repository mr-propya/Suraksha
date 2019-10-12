package com.propya.suraksha.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.propya.suraksha.Activities.UnsafeRegion;
import com.propya.suraksha.Constants;
import com.propya.suraksha.Helpers.ApiCallHelpers;
import com.propya.suraksha.Helpers.AppSwitcher;
import com.propya.suraksha.Helpers.NotificationHelpers;
import com.propya.suraksha.Helpers.SettingsContentObserver;
import com.propya.suraksha.Helpers.TrackMyTravel;

import org.json.JSONObject;

import java.util.HashMap;

public class CheckSafetyAlways extends Service {
    NotificationHelpers helpers;
    TrackMyTravel trackMyTravelHelper;
    TrackMyTravel.TrackMyTravelRaise concern;

    public static boolean isRunningUnsafe = false;
    private boolean breakLoop = false;
    long delay = 30000;
    AppSwitcher appSwitcher;
    public CheckSafetyAlways() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this.getClass().getName(), "Starting noti");

        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(this,new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );

        appSwitcher = new AppSwitcher(this, new AppSwitcher.AppSwitched() {
            @Override
            public void switched(String packageName) {
                Intent i = new Intent("");
                i.putExtra("alarm",11);
                alarmBroadCasted(i);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("help");

        Log.i("OnCreate", "Entered oncreate");
        if (!Constants.runningTriggered) {
            helpers = new NotificationHelpers(this);
            NotificationCompat.Builder builder = helpers.getNotiSmall("You are safe", "Found Activity on Phone", null);
            Intent unsafe = new Intent(this, CheckSafetyAlways.class);
            Intent sos = new Intent(this, CheckSafetyAlways.class);
            unsafe.putExtra("type", "alarm");
            sos.putExtra("type", "alarm");

            unsafe.putExtra("alarm", -1);
            sos.putExtra("alarm", -5);

            PendingIntent foregroundServiceUnsafe = PendingIntent.getService(this, 2, unsafe, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent foregroundServiceSOS = PendingIntent.getService(this, 3, sos, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                foregroundServiceUnsafe = PendingIntent.getForegroundService(this, 2, unsafe, PendingIntent.FLAG_UPDATE_CURRENT);
                foregroundServiceSOS = PendingIntent.getForegroundService(this, 3, sos, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            builder.addAction(0, "I'm Unsafe", foregroundServiceUnsafe);
            builder.addAction(0, "SOS", foregroundServiceSOS);
            startForeground(1, builder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            if (intent.getStringExtra("type") != null) {
                if (intent.getStringExtra("type").equals("alarm"))
                    alarmBroadCasted(intent);
//                if (intent.getStringExtra("type").equals("radar"))
//                    radar(intent);
                if (intent.getStringExtra("type").equals("geofence"))
                    geofence(intent);
                if (intent.getStringExtra("type").equals("trackMyTravel"))
                    startTrackMyTravel(intent, this);
                if (intent.getStringExtra("type").equals("notalarm")) {

                    NotificationManagerCompat.from(this).cancel(16);
                }
            }

                if (ActivityTransitionResult.hasResult(intent)) {
                    processTrans(intent);
                }

            }
            if (ActivityTransitionResult.hasResult(intent)) {
                processTrans(intent);
            }

        return START_STICKY;
    }

    private void geofence(Intent intent) {
        String key = intent.getStringExtra("key");
        Log.i("geofence","got in service "+key);
        NotificationHelpers helpers = new NotificationHelpers(this);
        Intent i = new Intent(this, UnsafeRegion.class);
        i.putExtra("key",key);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,8,i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat.from(this).notify(8,helpers.getNotiSmall("Caution Ahead","You are entering an unsafe region...\nPlease click here to proceed ahead",pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()).build());
    }

    private void processTrans(Intent intent) {
        ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
        Log.i("new trans whole",result.toString());
        for(ActivityTransitionEvent a : result.getTransitionEvents()){

            //TODO CHECK RUNNING TRIGGER HERE

            NotificationHelpers helpers = new NotificationHelpers(this);

            if(!Constants.runningTriggered)
            {
                helpers.timerNotif(2,this,30000);
                Constants.runningTriggered=true;
            }
            else
            {

            }

        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void alarmBroadCasted(Intent i){
        switch (i.getIntExtra("alarm",0)){
            case -1://unsafe
                if(isRunningUnsafe){
                   return;
                }
                isRunningUnsafe = true;
                appSwitcher.start();
                breakLoop = false;
                Log.i(CheckSafetyAlways.class.getName(),"Unsafe alarm raised");
               ApiCallHelpers apiCallHelpersAgain = new ApiCallHelpers(this);
               apiCallHelpersAgain.pingBack();
                NotificationHelpers notificationHelpers = new NotificationHelpers(this);
                notificationHelpers.NotifImSafe(25,this,delay);
               Handler h = new Handler();
//               keepNotifying(h,delay);
               break;

            case -3:
                NotificationHelpers notificationHelpersAgain = new NotificationHelpers(this);
                notificationHelpersAgain.NotifImSafe(25,this,delay);

            case -5://sos
                Log.i(CheckSafetyAlways.class.getName(),"SOS alarm raised");
                ApiCallHelpers apiCallHelpers = new ApiCallHelpers(this);
                apiCallHelpers.raiseSOS();
                break;


            case 10://safe
                Log.i(CheckSafetyAlways.class.getName(),"Marked Safe");
                ApiCallHelpers safetyHelper = new ApiCallHelpers(this);
                safetyHelper.markSafe();
                Constants.runningTriggered = false;
                NotificationManagerCompat.from(this).cancel(2);
                break;

            case 11://safe from ping back
                isRunningUnsafe = false;
                Log.i(CheckSafetyAlways.class.getName(),"Marked Safe from im unsafe callbacks");
                ApiCallHelpers safetyHelperCallback = new ApiCallHelpers(this);
                safetyHelperCallback.markSafe();
                NotificationManagerCompat.from(this).cancel(25);
                break;

            case 15://end unsafe
                appSwitcher.stop();
                Log.i("api end","ending safety callback");
                Log.i(CheckSafetyAlways.class.getName(),"ended unsafe Safe");
                isRunningUnsafe = false;
                breakLoop = true;
                ApiCallHelpers endHelpers = new ApiCallHelpers(this);
                HashMap<String,Object> data = new HashMap<>();
                data.put("userUID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                NotificationManagerCompat.from(this).cancel(25);
                endHelpers.callVolley(Constants.URLs.END_PING, data, new ApiCallHelpers.CallbackVolley() {
                    @Override
                    public void volleyCallBack(String msg) {
                        Log.i("api callback",msg);
                    }
                    @Override
                    public void volleyCallBack(JSONObject data) {
                        Log.i("api success",data.toString());
                        Toast.makeText(CheckSafetyAlways.this, "Marked you out on unsafe situation", Toast.LENGTH_SHORT).show();
                    }
                });
                break;


        }
    }

    void startTrackMyTravel(final Intent i , final Context c) {
        concern = new TrackMyTravel.TrackMyTravelRaise() {
            @Override
            public void RaiseConcern() {
                NotificationHelpers notificationHelpers = new NotificationHelpers(c);
                NotificationCompat.Builder builder = notificationHelpers.getNotiSmall("Caution","You have been deviated from destination",null);
                Intent i = new Intent(c,CheckSafetyAlways.class);
                i.putExtra("type", "alarm");
                i.putExtra("alarm",-5);
                PendingIntent pendingIntent = PendingIntent.getService(c,67,i,0);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    pendingIntent = PendingIntent.getForegroundService(c,67,i,0);

                }
                builder.addAction(0, "SOS",pendingIntent);
                Intent i2 = new Intent(c,CheckSafetyAlways.class);
                i.putExtra("type", "notalarm");
                PendingIntent pendingIntent2 = PendingIntent.getService(c,68,i,0);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    pendingIntent2 = PendingIntent.getForegroundService(c,68,i,0);

                }
                builder.addAction(0, "I'm safe",pendingIntent2);

                NotificationManagerCompat.from(c).notify(16,
                        builder.build());
            }
        };
        LocationServices.getFusedLocationProviderClient(c).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Location destination = new Location(location);
                location.setLongitude(i.getDoubleExtra("lon",location.getLongitude()));
                location.setLatitude(i.getDoubleExtra("lat",location.getLatitude()));
                trackMyTravelHelper  = new TrackMyTravel(location,destination,c,concern);
                trackMyTravelHelper.startLocationUpdates();
            }
        });
    }



    void trackMyTravelStop(Context c, LocationCallback locationCallback){
        trackMyTravelHelper.stopLocationUpdates();
    }



}
