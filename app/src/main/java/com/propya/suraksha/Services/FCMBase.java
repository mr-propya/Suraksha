package com.propya.suraksha.Services;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.propya.suraksha.Constants;
import com.propya.suraksha.Helpers.NotificationHelpers;

import java.util.Map;

public class FCMBase extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("FCM message rec",remoteMessage.getData().toString());
        Map<String, String> data = remoteMessage.getData();
        if(data.containsKey("type")){
            String type = data.get("type");
            if(type.equals(Constants.NotifyConstants.FCM_BROADCAST_LOC)){
                if(!data.get("user").equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    checkBroadCast(data);
            }
        }
    }

    private void checkBroadCast(final Map<String, String> data) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Log.i("FCM Check BroadCast","Successful retrieved last location");
                    Location result = task.getResult();
                    Location help = new Location(result);
                    help.setLatitude(Double.parseDouble(data.get("lat")));
                    help.setLongitude(Double.parseDouble(data.get("lon")));
                    if(help.distanceTo(result)<Float.parseFloat(data.get("radius"))){
                        Log.i("FCM Check BroadCast","accepted in radius");
                        new NotificationHelpers(FCMBase.this).askingHelp(data);
                    }else{
                        Log.i("FCM Check BroadCast","DiscardedOut of radius");
                    }


                }else{
                    Log.i("FCM Check BroadCast","Error getting last location");
                }
            }
        });



    }
}
