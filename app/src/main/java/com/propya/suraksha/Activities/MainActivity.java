package com.propya.suraksha.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.propya.suraksha.BluePrint.OnePlace;
import com.propya.suraksha.Constants;
import com.propya.suraksha.Helpers.ApiCallHelpers;
import com.propya.suraksha.Helpers.AppSwitcher;
import com.propya.suraksha.Helpers.NotificationHelpers;
import com.propya.suraksha.Helpers.PlacesHelperClass;
import com.propya.suraksha.Helpers.RadarHelper;
import com.propya.suraksha.Helpers.TrackMyTravel;
import com.propya.suraksha.Helpers.VolumeKeyController;
import com.propya.suraksha.Helpers.VolumeKeyController;
import com.propya.suraksha.R;
import com.propya.suraksha.Services.ActivityCallback;
import com.propya.suraksha.Services.ActivityRecognizer;
import com.propya.suraksha.Services.CheckSafetyAlways;
import com.propya.suraksha.Services.GeoFenceListener;
import com.propya.suraksha.StringListDialogFragment;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.propya.suraksha.Services.ActivityRecognizer.REQUEST_CODE;
import static java.lang.Thread.sleep;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final static int RC_SIGN_IN = 69;
    private static final int CAMERA_REQUEST = 50;
    private boolean flashLightStatus = false;
//    int count=0;
    int count=0;
    boolean keepGoing=false;
    AppSwitcher appSwitcher;
    Context c;
    TextView txt;
    ImageView btn;
    ImageView unsafe;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private FusedLocationProviderClient client;
    TrackMyTravel.TrackMyTravelRaise trackMyTravelRaise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Tag","oncreate ");
        super.onCreate(savedInstanceState);
        c=this;
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            login();
            return;
        }

        setContentView(R.layout.activity_main2);
        viewSetter();

        final ImageView soundlight;
        soundlight=(ImageView)findViewById(R.id.soundlight);

        //-----------------------------------------------

        final boolean hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        final MediaPlayer mp=MediaPlayer.create(this, R.raw.sample);
        soundlight.setOnClickListener(new View.OnClickListener() {
//            int count=0;
//            boolean keepGoing=false;
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(!mp.isPlaying())
                {
                    mp.start();

                }
                else
                {
                    mp.pause();
                    mp.seekTo(0);
                }
                keepGoing = !keepGoing;

            }
        });

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
        if (hasCameraFlash) {


            Thread t = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    while(true) {
                        while (keepGoing) {
                            sos();
                            flashLightOff();
                        }
                    }
                }
            });
            t.start();

        } else {
            Toast.makeText(MainActivity.this, "No flash available on your device",
                    Toast.LENGTH_SHORT).show();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void sos()
    {
        try {
//                if(!mp.isPlaying())
//                {
//                    mp.start();
//                }
//                else
//                {
//                    mp.pause();
//                    mp.seekTo(0);
//                }

            flashLightOn();
            sleep(100);
            flashLightOff();
            sleep(100);
            flashLightOn();
            sleep(100);
            flashLightOff();
            sleep(100);
            flashLightOn();
            sleep(100);
            flashLightOff();
            sleep(100);
            //---
            flashLightOn();
            sleep(200);
            flashLightOff();
            sleep(200);
            flashLightOn();
            sleep(200);
            flashLightOff();
            sleep(200);
            flashLightOn();
            sleep(200);
            flashLightOff();
            sleep(200);
            //---
            flashLightOn();
            sleep(100);
            flashLightOff();
            sleep(100);
            flashLightOn();
            sleep(100);
            flashLightOff();
            sleep(100);
            flashLightOn();
            sleep(100);
            flashLightOff();
            sleep(100);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public class PhotoDecodeRunnable implements Runnable{
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run()
        {
            ImageView img=(ImageView)findViewById(R.id.soundlight);
                //int x;
                img.setOnClickListener(new View.OnClickListener() {
                    int c=0;
                    @Override
                    public void onClick(View view) {
                        c++;
                        while(c%2!=0) {
                            sos();
                            Log.i("Tag", "count=" + c);
                        }
                        flashLightOff();

                        //x=1;
                    }
                });




        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        void sos()
        {
            try {
//                if(!mp.isPlaying())
//                {
//                    mp.start();
//                }
//                else
//                {
//                    mp.pause();
//                    mp.seekTo(0);
//                }

                flashLightOn();
                sleep(100);
                flashLightOff();
                sleep(100);
                flashLightOn();
                sleep(100);
                flashLightOff();
                sleep(100);
                flashLightOn();
                sleep(100);
                flashLightOff();
                sleep(100);
                //---
                flashLightOn();
                sleep(200);
                flashLightOff();
                sleep(200);
                flashLightOn();
                sleep(200);
                flashLightOff();
                sleep(200);
                flashLightOn();
                sleep(200);
                flashLightOff();
                sleep(200);
                //---
                flashLightOn();
                sleep(100);
                flashLightOff();
                sleep(100);
                flashLightOn();
                sleep(100);
                flashLightOff();
                sleep(100);
                flashLightOn();
                sleep(100);
                flashLightOff();
                sleep(100);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.i("Tag","flashlight on methos");
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
            //imageFlashlight.setImageResource(R.drawable.btn_switch_on);
        } catch (CameraAccessException e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.i("Tag","flashlight off method ");
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
            //imageFlashlight.setImageResource(R.drawable.btn_switch_off);
        } catch (CameraAccessException e) {
        }
    }



    void viewSetter(){
        addListener();
//        txt= (TextView)findViewById(R.id.mainText);
//        txt= (TextView)findViewById(R.id.mainText);
        btn= (ImageView)findViewById(R.id.NearestSafePlace);
        unsafe=(ImageView)findViewById(R.id.unsafe);
        unsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> options = new ArrayList<>();
                options.add("Danger At Home");
                options.add("Travelling alone on road");
                options.add("I'm in a Cab");
                options.add("Fake call");
                StringListDialogFragment unsafePullUp = StringListDialogFragment.newInstance("CHOOSE AN OPTION :",options);
                unsafePullUp.attachListener(new StringListDialogFragment.Listener() {
                    @Override
                    public void onStringClicked(int position) {
                        switch (position){
                            case 0://at home
                                startActivity(new Intent(MainActivity.this,ImUnsafe.class));
                                break;
                            case 1://alone
                                Intent i = new Intent(MainActivity.this,CheckSafetyAlways.class);
                                ContextCompat.startForegroundService(MainActivity.this,i);
                                Toast.makeText(c, "Started tracking", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                //fake call || cab
                                ApiCallHelpers helpers = new ApiCallHelpers(c);
                                helpers.callVolley(Constants.URLs.CALL_ME, new HashMap<String, Object>(), new ApiCallHelpers.CallbackVolley() {
                                    @Override
                                    public void volleyCallBack(String msg) {

                                    }

                                    @Override
                                    public void volleyCallBack(JSONObject data) {

                                    }
                                });

                        }
                    }
                });
                unsafePullUp.show(getSupportFragmentManager(),"unsafe_options");


            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PlacesHelperClass helperClass = new PlacesHelperClass(c,
                        new PlacesHelperClass.ResultReady() {
                            @Override
                            public void results(OnePlace[] results) {
                                Intent intent = new Intent(c,MapsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("markSafePlaces",true);
                                bundle.putSerializable("safeplaces",results);
                                Log.i("MAPSARRAY",""+results.length);
                                intent.putExtra("SafePlaces",bundle);
                                startActivity(intent);
                            }
                        });
                LocationServices.getFusedLocationProviderClient(MainActivity.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        helperClass.findNearbyPlaces(location.getLatitude(),location.getLongitude());

                    }
                });

            }
        });
        trial();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            client = LocationServices.getFusedLocationProviderClient(this);
        }
        //Button button = findViewById(R.id.maps);
        trackMyTravelRaise = new TrackMyTravel.TrackMyTravelRaise() {
            @Override
            public void RaiseConcern() {

                Log.i("TrackMyTravel","Deviated!!!!!!!!");

            }
        };
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fcmToken").setValue(instanceIdResult.getToken());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK) {
                setContentView(R.layout.activity_main);
                viewSetter();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    void login(){
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(Collections.singletonList(
//                                new AuthUI.IdpConfig.PhoneBuilder().build()
//                        ))
//                        .build(),
//                RC_SIGN_IN);
        startActivity(new Intent(this,LoginActivity.class));
    }



    void trial(){
        appSwitcher= new AppSwitcher(this, new AppSwitcher.AppSwitched() {
            @Override
            public void switched(String packageName) {
                Toast.makeText(c, "App switched "+packageName, Toast.LENGTH_SHORT).show();
            }
        });
        appSwitcher.start();
        new NotificationHelpers(this).generateForeground(null);
        registerActivity();
        startActivityTracking();
//        RadarHelper radarHelper = new RadarHelper(this);
//        radarHelper.setupRadar();
//        radarHelper.startTracking();
        LocationServices.getFusedLocationProviderClient(this)
                .getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location result = task.getResult();
                    Log.i("Current location is ","lat "+result.getLatitude()+" long "+result.getLongitude());
                }
            }
        });
        GeoFenceListener listener = new GeoFenceListener(this,null);
        geofenceChecker();
//        RadarHelper radarHelper = new RadarHelper(this);
//        radarHelper.setupRadar();
//        radarHelper.startTracking();
    }

    public void openMaps(View v){
        new ApiCallHelpers(this).raiseSOS();
        Toast.makeText(c, "Raised an sos", Toast.LENGTH_SHORT).show();
        appSwitcher.stop();
//        Intent i = new Intent(this,UnsafeRegion.class);
//        i.putExtra("key","manipalHall");
//        startActivity(i);

    }
    void geofenceChecker(){
        LocationRequest request = new LocationRequest();
        request.setInterval(1000);
        request.setMaxWaitTime(5000);
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(request,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location l = new Location(locationResult.getLastLocation());
                l.setLongitude(74.7956938);
                l.setLatitude(13.3521216);
                Log.i("geofence current loc",""+locationResult.getLastLocation().getLatitude());
                Log.i("geofence current loc",""+locationResult.getLastLocation().getLongitude());
                Log.i("geofence dis loc",""+locationResult.getLastLocation().distanceTo(l));
            }
        },null);

    }


    public void openAutocomplete(View v){
        startActivity(new Intent(this, TmtAutocomplete.class));
        }



    void registerActivity(){
        final Context context = this;

        Intent callback = new Intent(context, CheckSafetyAlways.class);


        PendingIntent pendingIntent = PendingIntent.getService(context,REQUEST_CODE,callback,0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            pendingIntent= PendingIntent.getForegroundService(context,REQUEST_CODE,callback,0);

        }
        Task<Void> voidTask = ActivityRecognition.getClient(context).requestActivityUpdates(5000, pendingIntent);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(this.getClass().getName(),"success");
                Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });



    }

    void startActivityTracking(){
        ActivityTransitionRequest request = ActivityRecognizer.buildTransitionRequest();
        Intent callback = new Intent(this,CheckSafetyAlways.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,REQUEST_CODE+5,callback,0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            pendingIntent= PendingIntent.getForegroundService(this,REQUEST_CODE+5,callback,0);
        }
        Task<Void> voidTask = ActivityRecognition.getClient(this).requestActivityTransitionUpdates(request, pendingIntent);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(this.getClass().getName(),"success full");
                Toast.makeText(MainActivity.this, "Successful Activity Recognition 1", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }







    public void trackMyTravel(View v){
        final Location destination = new Location(LocationManager.GPS_PROVIDER);
        destination.setLatitude(19.212671);
        destination.setLongitude(72.866803);
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                final TrackMyTravel helper = new TrackMyTravel(location,destination,MainActivity.this, new TrackMyTravel.TrackMyTravelRaise(){
                    @Override
                    public void RaiseConcern() {
                        Log.i("TrackTravel","concern raised");
                        NotificationHelpers notificationHelpers = new NotificationHelpers(MainActivity.this);
                        NotificationManagerCompat.from(MainActivity.this).notify(16,
                                notificationHelpers.getNotiSmall("Caution","You have been deviated from destination",null).build());
                    }
                });

            }
        });


    }
    public void addListener(){
        VolumeKeyController volumeKeyController = new VolumeKeyController(this);
        volumeKeyController.setActive(true);
    }

    ActivityTransitionRequest buildTransitionRequest()
    {
        List<ActivityTransition> transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        transitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());
        return new ActivityTransitionRequest(transitions);
    }
}

