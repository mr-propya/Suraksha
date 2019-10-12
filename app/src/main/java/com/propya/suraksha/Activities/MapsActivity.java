package com.propya.suraksha.Activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.propya.suraksha.BluePrint.OnePlace;
import com.propya.suraksha.Helpers.ApiCallHelpers;
import com.propya.suraksha.R;
import com.propya.suraksha.StringListDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    //private static final String TAG =  ;
    private GoogleMap mMap;
    MarkerOptions markerOptions;
    boolean markSafePlaces=false;
    boolean alreadyMarked = false;
    OnePlace[] safeplaces;
    Marker previous;

//MapsActivity(OnePlace[] safeplaces)
//{
//    this.safeplaces=safeplaces;
//    this.markSafePlaces=true;
//}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(getIntent().getBundleExtra("SafePlaces")!=null)
        {
            Bundle bundle = getIntent().getBundleExtra("SafePlaces");
            this.markSafePlaces=bundle.getBoolean("markSafePlaces");
            this.safeplaces = (OnePlace[]) bundle.getSerializable("safeplaces");
        }
        else
        {
            this.markSafePlaces=false;
        }
        Log.i("MAPSSAFE",""+this.markSafePlaces);
        Log.i("MAPSSAFE",""+safeplaces.length);
        LocationRequest request = new LocationRequest();
        request.setInterval(15000);
        request.setMaxWaitTime(30000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(request,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i("location received",""+locationResult.getLastLocation().getLatitude());
                Log.i("location received",""+locationResult.getLastLocation().getLongitude());
                Log.i("location received",""+locationResult.getLastLocation().getProvider());
                onLocationChanged(locationResult.getLastLocation());
                LatLng currentloc = new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                LatLng school = new LatLng(19.212671, 72.866803);

                String url = "httpsm://maps.googleapis.com/maps/api/directions/json?origin=19.2070727,72.8590071&destination=19.212671,72.866803&key=AIzaSyCs_eULsIncv9KI9csnt5sEM1OrSIt5iOs";
                mMap.moveCamera(CameraUpdateFactory.
                        newLatLngZoom(new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude()),mMap.getMaxZoomLevel()*.75f));
                ApiCallHelpers helpers = new ApiCallHelpers(MapsActivity.this);
                helpers.callVolley(url, new HashMap<String, Object>(), new ApiCallHelpers.CallbackVolley() {
                    @Override
                    public void volleyCallBack(String msg) {
                        Log.i("Maps error",msg);
                    }

                    @Override
                    public void volleyCallBack(JSONObject data) {
                        Log.i("Maps response",data.toString());
                    }
                });
//                markSafePlacesOnMap(currentloc,school);

                if(markSafePlaces)
                {
                    markSafePlacesOnMap();
                }


            }


            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        },null);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void markSafePlacesOnMap(LatLng currentloc, LatLng school) {

        markerOptions= new MarkerOptions().position(currentloc).title("Current Location");
        Marker marker = mMap.addMarker(markerOptions);
        if(previous != null)
            previous.remove();
        previous = marker;
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentloc));
//        mMap.addPolyline(new PolylineOptions().add(
//                currentloc,
//                school
//                ).width(10).color(Color.RED)
//        );



    }
    private  void  markSafePlacesOnMap()
    {
        if(markSafePlaces&&!alreadyMarked)
        {
            final ArrayList<String> safeplacesArray = new ArrayList<>();

            for (int i=0;i<safeplaces.length;i++)
            {


                OnePlace oneplace = safeplaces[i];
                LatLng safeplacelatlng= new LatLng(oneplace.getLatitude(),oneplace.getLongitude());
                markerOptions= new MarkerOptions().position(safeplacelatlng).title(oneplace.getName());
                mMap.addMarker(markerOptions);
                safeplacesArray.add(oneplace.getName()+"\n"+oneplace.getVicinity());
            }

            StringListDialogFragment safePlacePullUp = StringListDialogFragment.newInstance("Nearby Safest places",safeplacesArray);
            safePlacePullUp.attachListener(new StringListDialogFragment.Listener() {
                @Override
                public void onStringClicked(int position) {
                    safeplaces[position].getLatitude();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="
                                    +safeplaces[position].getLatitude()+","
                                    +safeplaces[position].getLongitude()));
                    startActivity(intent);
                }
            });
            safePlacePullUp.show(getSupportFragmentManager(),"safe_places");
            alreadyMarked=true;
        }
    }

    public void openMaps(View v){

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }


        // Add a marker in Sydney and move the camera



    }


    @Override
    public void onLocationChanged(Location location) {
        double longitude  = location.getLongitude();
        double latitude = location.getLatitude();
        Log.d(String.valueOf(MapsActivity.this), "onLocationChanged: longitude : "+longitude+", latitude : " + latitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
