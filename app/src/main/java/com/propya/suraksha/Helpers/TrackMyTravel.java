package com.propya.suraksha.Helpers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.propya.suraksha.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class TrackMyTravel {
    Location source,destination;
    Context c;
    Location current;
    int distance,eta;
    TrackMyTravelRaise concern;
    LocationCallback locationCallback;

    String urlBase[] = {"https://maps.googleapis.com/maps/api/directions/json?origin=","&destination=","&key="+Constants.TrackTravelConstants.apiKey,"&mode=walking"};


    public TrackMyTravel(Location source, Location destination, Context c,TrackMyTravelRaise concern) {
        this.source = source;
        this.current = source;
        this.destination = destination;
        this.c = c;
        this.concern = concern;
        distance = Integer.MAX_VALUE;
        eta = Integer.MAX_VALUE;
        startLocationUpdates();
    }

    public void stopLocationUpdates(){
        LocationServices.getFusedLocationProviderClient(c).removeLocationUpdates(locationCallback);
    }

    public void startLocationUpdates(){
        LocationRequest request = new LocationRequest();
        request.setInterval(1000* Constants.TrackTravelConstants.interval);
        request.setMaxWaitTime(1000* Constants.TrackTravelConstants.interval+10000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                getRoute(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(c).requestLocationUpdates(request,locationCallback, null);
    }

    void stop(){
        LocationServices.getFusedLocationProviderClient(c).removeLocationUpdates(locationCallback);
    }

    void getRoute(Location location){
        if(location.distanceTo(destination) < 50){
            stop();
        }
        String url =urlBase[0];
        url+= ""+location.getLatitude()+","+location.getLongitude();
        url+= urlBase[1];
        url+= ""+destination.getLatitude()+","+destination.getLongitude();
        url+=urlBase[2];
        Log.i("final url",url);
        ApiCallHelpers helpers = new ApiCallHelpers(c);
        helpers.callVolley(url, new HashMap<String, Object>(), new ApiCallHelpers.CallbackVolley() {
            @Override
            public void volleyCallBack(String msg) {
                Log.i(TrackMyTravel.class.getName(),"api key "+msg);
            }

            @Override
            public void volleyCallBack(JSONObject data) {
                try {
                    if(data.getString("status").equals("OK")){
                        JSONArray routes = data.getJSONArray("routes");
                        int[] localDistances = new int[2];
                        for(int i=0;i<routes.length();i++){
                            int[] distance = computeDistane(routes.getJSONObject(i));
                            if(localDistances[0]==0 || localDistances[0]>distance[0]){
                                if(localDistances[1]==0 || localDistances[1]>distance[1]){
                                    localDistances=distance;
                                }
                            }
                            checkDistance(localDistances);
                        }
                    }
                }catch (Exception e){}
            }
        });
    }

    void checkDistance(int[] distances){
        if(distances[0] > distance){
            if(distances[1] > eta){
                if(((int)distances[0]*Constants.TrackTravelConstants.buffer) > distance){
                    Log.i("trackmytravel","raise concern");
                    concern.RaiseConcern();
                    stop();
                }else{
                    Log.i("Trackmytravel","correct path");
                    return;
                }
        }
    }
    distance = distances[0];
    eta = distances[1];
}

    int[] computeDistane(JSONObject object){
        int[] result = new int[2];
        try {
            JSONArray legs = object.getJSONArray("legs");
            for(int i = 0 ;i<legs.length();i++){
                JSONObject step = legs.getJSONObject(i);
                result[0]+=step.getJSONObject("distance").getInt("value");
                result[1]+=step.getJSONObject("duration").getInt("value");
            }
        }catch (Exception e){}
        return result;
    }

    public interface TrackMyTravelRaise{
        public void RaiseConcern();
    }

}
