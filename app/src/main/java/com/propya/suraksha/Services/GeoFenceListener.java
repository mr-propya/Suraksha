package com.propya.suraksha.Services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.propya.suraksha.Receivers.GeofenceNativeReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeoFenceListener {

    ArrayList<Geofences> geofencesData = new ArrayList<>();
    GeoFenceCallBack callBack;
    Context c;
    PendingIntent pendingIntent;
    private String actionFilter;

    public GeoFenceListener(Context c,@NonNull GeoFenceCallBack callBack) {
        getGeofences();
        this.c = c;
        this.callBack = callBack;
        this.actionFilter = c.getPackageName()+".geofence";
    }


    void getGeofences(){
        FirebaseDatabase.getInstance().getReference("geoFences").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Geofences geofence = data.getValue(Geofences.class);
                    geofence.setKey(data.getKey());
                    geofencesData.add(geofence);
                }
                Log.i("geofence Data Received","len "+ geofencesData.size());
                saveGeofences();
                setUpGeofences();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveGeofences(){
        SharedPreferences.Editor preferences = c.getSharedPreferences("geofences",Context.MODE_PRIVATE).edit();
        for(Geofences g : geofencesData){
            String key = g.key;
            preferences.putInt(key+"_start",g.start);
            preferences.putInt(key+"_end",g.end);
        }
        preferences.apply();
    }

    public static boolean isValidGeofence(String key,Context c){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
        int time = Integer.parseInt(dateFormat.format(date));

        int start = c.getSharedPreferences("geofences",Context.MODE_PRIVATE).getInt(key+"_start",0);
        int end = c.getSharedPreferences("geofences",Context.MODE_PRIVATE).getInt(key+"_end",0);

        if(time> start && time<end){
            return true;
        }
        return false;
    }


    private void setUpGeofences(){
        final List<Geofence> geofenceList = new ArrayList<>();
        for(Geofences g: geofencesData){
            geofenceList.add(
              new Geofence.Builder().setRequestId(g.key)
                      .setCircularRegion(g.lat,g.lon,g.radius)
                      .setExpirationDuration(Geofence.NEVER_EXPIRE)
                      .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                      .build()
            );
        }

        LocationServices.getGeofencingClient(c).removeGeofences(getPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startListening();
                Log.i("geofence ","removed existing geofencesData");
                GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
                builder.addGeofences(geofenceList);
                builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER|GeofencingRequest.INITIAL_TRIGGER_EXIT);
                LocationServices.getGeofencingClient(c)
                        .addGeofences(builder.build(),getPendingIntent())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("geofence"," successfully added");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.i("geofence","failed to add geofence");
                        Log.i("geofence","failed bcoz "+e.getLocalizedMessage());
                    }
                });
                Log.i("geofence ","added new geo fences");
            }
        });
    }

    private void startListening(){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
                if(geofencingEvent.hasError()){
                    return;
                }
                callBack.CallBack(geofencingEvent);
            }
        };
        Log.i("geofence","started listening");
        IntentFilter filter = new IntentFilter(actionFilter);
        c.registerReceiver(receiver,filter);
    }


    PendingIntent getPendingIntent() {
        if (pendingIntent == null) {
            Intent i = new Intent(c, GeofenceNativeReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(c, 5, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }


    public interface GeoFenceCallBack{
        public void CallBack(GeofencingEvent event);
    }



}

