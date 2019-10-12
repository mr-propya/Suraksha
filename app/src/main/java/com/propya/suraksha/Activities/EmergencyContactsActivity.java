package com.propya.suraksha.Activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.propya.suraksha.R;

public class EmergencyContactsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        EditText ed1 = findViewById(R.id.ec1);
        EditText ed2 = findViewById(R.id.ec2);
        EditText ed3 = findViewById(R.id.ec3);
        EditText ed4 = findViewById(R.id.ec3);
        EditText ed5 = findViewById(R.id.ec3);

        //mDatabase.child("users").child(currentUser.getUid()).child("Emergency contacts").setValue("");

    }
}
