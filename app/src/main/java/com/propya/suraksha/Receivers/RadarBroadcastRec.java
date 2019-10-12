package com.propya.suraksha.Receivers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import io.radar.sdk.Radar;
import io.radar.sdk.RadarReceiver;
import io.radar.sdk.model.RadarEvent;
import io.radar.sdk.model.RadarUser;

public class RadarBroadcastRec extends RadarReceiver {

    @Override
    public void onClientLocationUpdated(@NotNull Context context, @NotNull Location location, boolean stopped) {
        super.onClientLocationUpdated(context, location, stopped);
        Log.i("radar brod","client loc update");
    }

    @Override
    public void onError(@NotNull Context context, @NotNull Radar.RadarStatus radarStatus) {
        Log.i("radar brod","error"+radarStatus.name());

    }

    @Override
    public void onEventsReceived(@NotNull Context context, @NotNull RadarEvent[] radarEvents, @NotNull RadarUser radarUser) {
        Log.i("radar brod","event received"+ radarEvents.length);
        for (RadarEvent r: radarEvents){
            Log.i("radar brod event",r.getGeofence().getDescription());
            Log.i("radar brod event",r.getType().name());
        }


    }

    @Override
    public void onLocationUpdated(@NotNull Context context, @NotNull Location location, @NotNull RadarUser user) {
        super.onLocationUpdated(context, location, user);
        Log.i("radar brod","loc update");

    }

    @Override
    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
        super.onReceive(context, intent);
        Log.i("radar brod","received");

    }
}
