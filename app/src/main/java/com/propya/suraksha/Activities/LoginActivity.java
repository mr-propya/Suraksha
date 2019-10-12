package com.propya.suraksha.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.propya.suraksha.R;

import java.util.Collections;

public class LoginActivity extends IntroActivity {

    Details details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        details = new Details();
        super.onCreate(savedInstanceState);
        addSlide(new SimpleSlide.Builder().title(R.string.app_name).description("Aapki suraksha apke haatho").image(R.mipmap.ic_launcher_round).background(R.color.colorPrimaryDark).build());
        addSlide(new SimpleSlide.Builder().title("Permissions required").description("Please grant location permissions to proceed")
                .image(R.mipmap.ic_launcher_round).scrollable(false).background(R.color.colorPrimaryDark)
                .build());
        addSlide(new SimpleSlide.Builder().title(R.string.app_name).description("Please proceed to login").image(R.mipmap.ic_launcher_round).scrollable(false).background(R.color.colorPrimaryDark).build());
        addSlide(new FragmentSlide.Builder().background(R.color.colorPrimary)
        .fragment(details).build());
        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                if(position == 1)
                    return true;
                if(position == 2){
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                        return true;
                    }else{
                        login();
                        return false;
                    }
                }
                if(position == 3){
                    Details.getDetails();
                }
                return true;
            }

            @Override
            public boolean canGoBackward(int position) {
                return true;
            }
        });
    }
    void login(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                new AuthUI.IdpConfig.PhoneBuilder().build()
                        ))
                        .build(),
                65);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
