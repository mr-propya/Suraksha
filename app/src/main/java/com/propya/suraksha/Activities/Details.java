package com.propya.suraksha.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.propya.suraksha.R;

import java.util.HashMap;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Details extends Fragment {

    static View rootView;
    static String ref;
    public static boolean userExists;
    public static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getDetails();
        }
    };

    public Details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getData();
        rootView =  inflater.inflate(R.layout.fragment_details, container, false);
        return rootView;
    }

    void getData(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        ref = "users/"+FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference(ref).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rootView.findViewById(R.id.progressScreen).setVisibility(View.GONE);
                if(dataSnapshot.exists()){
                    userExists = true;
                    TextView viewById = (TextView) rootView.findViewById(R.id.isReturningUser);
                    viewById.setVisibility(View.VISIBLE);
                    viewById.setText("Welcome back "+dataSnapshot.child("name").getValue(String.class)+
                            "\nYou may proceed now");
                }else{
                    rootView.findViewById(R.id.getDetails).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static boolean getDetails(){
        try {
            TextView name = rootView.findViewById(R.id.nameDetails);
            TextView address = rootView.findViewById(R.id.addressDetails);
            LinearLayout layout = rootView.findViewById(R.id.emergencyDetails);
            HashMap<String,String> details = new HashMap<>();
            LinearLayout nameLayout= (LinearLayout) layout.getChildAt(0);
            LinearLayout phoneLayout= (LinearLayout) layout.getChildAt(1);
            for(int i =0;i<3;i++){
                String text1 = ((TextView) nameLayout.getChildAt(i)).getText().toString();
                String text2 = ((TextView)phoneLayout.getChildAt(i)).getText().toString();
                details.put(text1,text2);
            }
            HashMap<String,Object> data = new HashMap<>();
            data.put("name",name.getText().toString());
            data.put("address",address.getText().toString());
            data.put("emergencyNo",details);
            FirebaseDatabase.getInstance().getReference(ref).setValue(data);
            userExists = true;
            return true;
        }catch (Exception e){
            return false;
        }

    }

}
