package com.propya.suraksha.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.propya.suraksha.Services.CheckSafetyAlways;
import com.propya.suraksha.Services.GeoFenceListener;

public class GeofenceNativeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            return;
        }
        Log.i("geofence ",geofencingEvent.toString());
        Log.i("geofence ",geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
        Log.i("geofence ",geofencingEvent.toString());
        for (Geofence g: geofencingEvent.getTriggeringGeofences()){
            String key = g.getRequestId();
            Log.i("Geofence receiver",key+" is checking");
            if(GeoFenceListener.isValidGeofence(key,context)){
                Log.i("Geofence receiver",key+" is valid");
                Intent i = new Intent(context, CheckSafetyAlways.class);
                i.putExtra("type","geofence");
                i.putExtra("key",key);
                ContextCompat.startForegroundService(context,i);
                break;

            }
        }
    }
}
