package com.propya.suraksha.Helpers;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.gson.JsonObject;
import com.propya.suraksha.Constants;

import org.json.JSONObject;

import java.util.HashMap;

public class ApiCallHelpers {

    Context c;

    public ApiCallHelpers(Context c){
        this.c = c;
    }

    public void raiseSOS(){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(c);
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                HashMap<String,Object> data = new HashMap<>();
                data.put("lat",location.getLatitude());
                data.put("lon",location.getLongitude());
                data.put("userUID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                callVolley(Constants.URLs.ASK_HELP, data, new CallbackVolley() {
                    @Override
                    public void volleyCallBack(String msg) {
                        Log.i(ApiCallHelpers.class.getName(),msg);
                    }

                    @Override
                    public void volleyCallBack(JSONObject data) {
                        Log.i(ApiCallHelpers.class.getName(),"Response");
                        Log.i(ApiCallHelpers.class.getName(),data.toString());

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i(ApiCallHelpers.class.getName(),"Error occured "+e.getLocalizedMessage());
            }
        });
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("userUID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        callVolley(Constants.URLs.SMS_ME,stringObjectHashMap, new CallbackVolley() {
            @Override
            public void volleyCallBack(String msg) {

            }

            @Override
            public void volleyCallBack(JSONObject data) {

            }
        });
    }



    public void callVolley(String url, HashMap<String,Object> data,final CallbackVolley callbackVolley){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callbackVolley.volleyCallBack(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackVolley.volleyCallBack("Error occurred"+error.getLocalizedMessage());
            }
        });
        request
                .setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(c).add(request);
        callbackVolley.volleyCallBack("Queued Request");
    }

    public void pingBack() {
        markSafe();
        HashMap<String,Object> data = new HashMap<>();
        data.put("userUID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        callVolley(Constants.URLs.ASK_PING, data, new CallbackVolley() {
            @Override
            public void volleyCallBack(String msg) {
                Log.i(ApiCallHelpers.class.getName(),msg);
            }

            @Override
            public void volleyCallBack(JSONObject data) {
                Log.i(ApiCallHelpers.class.getName(),"Response");
                Log.i(ApiCallHelpers.class.getName(),data.toString());
            }
        });


    }

    public void markSafe(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations/"+
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String key = ref.push().getKey();
        final HashMap<String,Object> data = new HashMap<>();
        data.put("time", ServerValue.TIMESTAMP);


        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(c);
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                data.put("lat",location.getLatitude());
                data.put("lon",location.getLongitude());
                ref.child(key).setValue(data);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ref.child(key).setValue(data);
            }

        });

        Toast.makeText(c,"SAFE",Toast.LENGTH_LONG).show();

    }

    public interface CallbackVolley{
        void volleyCallBack(String msg);
        void volleyCallBack(JSONObject data);

    }

}
