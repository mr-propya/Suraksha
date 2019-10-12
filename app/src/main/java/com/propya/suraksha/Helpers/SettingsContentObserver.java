package com.propya.suraksha.Helpers;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Logger;

public class SettingsContentObserver extends ContentObserver {
    int previousVolume;
    Context context;
    String sos = "121";
    String fake = "222";
    String current;
    Handler handler;

    public SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        context=c;
        this.handler = handler;
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta=previousVolume-currentVolume;

        if(delta>0)
        {
            Log.d("Ściszył!","decrease"); // volume decreased.
            previousVolume=currentVolume;
            if(current == null){
                current = "1";
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        current = null;
                    }
                },5000);
            }else{
                current = current+"1";
            }
            check();

        }
        else if(delta<0)
        {
            Log.d("Zrobił głośniej!","increase"); // volume increased.
            previousVolume=currentVolume;
            if(current == null){
                current = "2";
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        current = null;
                    }
                },3000);
            }else{
                current = current+"2";
            }
            check();
        }
    }

    private void check() {
        if(current!=null){
            if(current.equals(sos)){
                Toast.makeText(context, "sos", Toast.LENGTH_SHORT).show();

            }if(current.equals(fake)){
                Toast.makeText(context, "fake", Toast.LENGTH_SHORT).show();

            }


        }
    }
}
