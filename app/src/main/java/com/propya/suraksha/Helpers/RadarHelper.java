package com.propya.suraksha.Helpers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.propya.suraksha.Constants;
import com.propya.suraksha.Services.CheckSafetyAlways;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Handler;

import io.radar.sdk.Radar;
import io.radar.sdk.RadarTrackingOptions;
import io.radar.sdk.model.RadarEvent;
import io.radar.sdk.model.RadarUser;

public class RadarHelper {
    Context c;

    public RadarHelper(Context c){
        this.c =c;
    }


    public void setupRadar(){
        Radar.initialize(Constants.RadarConstants.TEST_PUB);
        Radar.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.i("radar","setting up");
    }


    public void trialBroadCast(final int delay){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(delay*1000);
                }catch (Exception e){}
                Intent i =new Intent("io.radar.sdk.RECEIVED");
                c.sendBroadcast(i);
            }
        });


    }

    public void startTracking(){
        RadarTrackingOptions options = new RadarTrackingOptions.Builder().priority(Radar.RadarTrackingPriority.RESPONSIVENESS)
                .offline(Radar.RadarTrackingOffline.REPLAY_STOPPED).sync(Radar.RadarTrackingSync.ALL).build();
        Intent foregroundListener = new Intent(c, CheckSafetyAlways.class);
        foregroundListener.putExtra("type","radar");
        foregroundListener.putExtra("action",1);
        ContextCompat.startForegroundService(c,foregroundListener);

        Radar.startTracking(options);
        Log.i("radar","starting tracking");
        Radar.trackOnce(new Radar.RadarCallback() {
            @Override
            public void onComplete(@NotNull Radar.RadarStatus radarStatus, @Nullable Location location, @Nullable RadarEvent[] radarEvents, @Nullable RadarUser radarUser) {
                Log.i("radar","on complete");
                Log.i("radar","on complete "+radarStatus.name());
                Log.i("radar","event "+radarEvents.length);
                for(RadarEvent r:radarEvents){
                    Log.i("radar","event "+r.toString());
                }
            }
        });
        trialBroadCast(5);
    }

}
