package com.propya.suraksha.Activities;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.propya.suraksha.R;

public class TrackMyTravelMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng dest;
    private String destTitle;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("trackMap","*****oncreate");
        setContentView(R.layout.activity_track_my_travel_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapUnique);
        mapFragment.getMapAsync(this);

        String[] latlng = getIntent().getStringExtra("LATLNG").split(",");

        dest = new LatLng(Double.parseDouble(latlng[0]),Double.parseDouble(latlng[1]));
        destTitle = getIntent().getStringExtra("PLACE_NAME");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        LatLng coords = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(coords).title("You"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
                    }
                }
            });
        mMap.addMarker(new MarkerOptions().position(dest).title(destTitle).icon(BitmapDescriptorFactory.defaultMarker(120)));

    }
}
