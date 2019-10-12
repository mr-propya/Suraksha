package com.propya.suraksha.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.propya.suraksha.R;

public class HelpAcknowledge extends AppCompatActivity {

    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_acknowlege);
        showDialog();
        Bundle data = getIntent().getBundleExtra("data");
        uid = data.getString("user");
        extractData(data);
    }


    void showDialog(){
        new AlertDialog.Builder(this).setMessage("You agree to take the responsibility and help the user?\nFor safety reasons your details might be shared with the user and our records\n" +
                "Do you agree to proceed?").setTitle("I want to help").setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference("locations/"+uid).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d:dataSnapshot.getChildren()){
                            dialogInterface.dismiss();
                            double lat = d.child("lat").getValue(Double.class);
                            double lon = d.child("lon").getValue(Double.class);
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon));
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }).setNegativeButton("Bad person", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }).setCancelable(false).show();


    }

    void extractData(Bundle b){
        NotificationManagerCompat.from(this).cancel(b.getInt("notiID"));
    }
}
