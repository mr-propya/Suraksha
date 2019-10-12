package com.propya.suraksha.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.propya.suraksha.Helpers.PlacesAutoCompleteAdapter;
import com.propya.suraksha.R;
import com.propya.suraksha.Services.CheckSafetyAlways;

public class TmtAutocomplete extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener{

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private EditText etPlace;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmt_autocomplete);

        Places.initialize(this, getResources().getString(R.string.google_maps_key));

        recyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        etPlace = (EditText) findViewById(R.id.place_search);
        btnSubmit = (Button) findViewById(R.id.submitBtn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etPlace.getText().toString().equals("")) {
                    mAutoCompleteAdapter.getFilter().filter(etPlace.getText().toString());
                    if (recyclerView.getVisibility() == View.GONE) {recyclerView.setVisibility(View.VISIBLE);}
                } else {
                    if (recyclerView.getVisibility() == View.VISIBLE) {recyclerView.setVisibility(View.GONE);}
                }
            }
        });

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
    }


    @Override
    public void click(final Place place) {
        Toast.makeText(this, "Your travel is being tracked", Toast.LENGTH_LONG).show();

        final Location destination = new Location(LocationManager.GPS_PROVIDER);
        destination.setLatitude(place.getLatLng().latitude);
        destination.setLongitude(place.getLatLng().longitude);
        Intent i = new Intent(this, CheckSafetyAlways.class);
        i.putExtra("type","trackMyTravel");
        i.putExtra("lat",place.getLatLng().latitude);
        i.putExtra("lon",place.getLatLng().longitude);
        ContextCompat.startForegroundService(this,i);

        new AlertDialog.Builder(this)
                .setTitle("Your travel is being tracked")
                .setMessage("We will send you a notification if you deviate from the path")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TmtAutocomplete.this, TrackMyTravelMap.class);
                        String latlng = place.getLatLng().toString();
                        intent.putExtra("LATLNG", latlng.substring(latlng.indexOf("(")+1,latlng.indexOf(")")));
                        intent.putExtra("PLACE_NAME", place.getName());
                        startActivity(intent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
