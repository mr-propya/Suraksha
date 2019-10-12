package com.propya.suraksha.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.propya.suraksha.Helpers.ApiCallHelpers;
import com.propya.suraksha.R;

public class UnsafeRegion extends AppCompatActivity implements OnMapReadyCallback {
    String key;
    GoogleMap map;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsafe_region);
        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapsUnsafe)).getMapAsync(this);
        key = getIntent().getStringExtra("key");
        FirebaseDatabase.getInstance().getReference("geoFences/"+key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                LatLng latLng = new LatLng(dataSnapshot.child("lat").getValue(Double.class)
                        , dataSnapshot.child("lon").getValue(Double.class));
                CircleOptions circleOptions = new CircleOptions().center(latLng)
                        .radius(dataSnapshot.child("radius").getValue(Integer.class))
                        .fillColor(R.color.redAlpha);
                Circle c = map.addCircle(circleOptions);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(dataSnapshot.getKey());
                marker = map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,map.getMaxZoomLevel()*.8f));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.setMyLocationEnabled(true);
    }

    public void raiseSOS(View v){
        ApiCallHelpers helpers = new ApiCallHelpers(this);
        helpers.raiseSOS();
    }

    public void nearestRoute(View v){

    }


}
