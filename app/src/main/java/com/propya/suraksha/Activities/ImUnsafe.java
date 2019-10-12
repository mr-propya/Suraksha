package com.propya.suraksha.Activities;

import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.propya.suraksha.R;

import com.propya.suraksha.R;
import com.propya.suraksha.StringListDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class ImUnsafe extends AppCompatActivity {

    Button surveyButton;
    int i=0;
    int[] result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsafe);
        surveyButton=findViewById(R.id.survey_button);
        surveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getString();
//                Toast.makeText(getApplicationContext(),"some toast",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getResponses(final String[] strings, final int index){
        if(index == strings.length){
            Toast.makeText(this, "Thank you for your response..\nIt will be stored with us", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> options = new ArrayList<>();
        options.add("No");
        options.add("Yes");
        StringListDialogFragment dialogFragment = StringListDialogFragment.newInstance(strings[index],options);
        dialogFragment.attachListener(new StringListDialogFragment.Listener() {
            @Override
            public void onStringClicked(int position) {
                result[index] = position;
                getResponses(strings,index+1);
            }
        });
        dialogFragment.show(getSupportFragmentManager(),"");






    }



    public void getString(){
        String[] strings = new String[]{
                        "Are you spending less time with family and friends?\n" +
                        "Does your partner make you feel guilty for doing things you like to do?\n" +
                        "Is your partner very jealous or possessive?\n" +
                        "Does your partner criticize or insult you?\n" +
                        "Are you afraid of making your partner angry?\n" +
                        "Has your partner hurt or threatened to hurt you?\n"
        };
        strings = strings[0].split("\n");
        result = new int[strings.length];
        getResponses(strings,0);



        }


 }
