package com.propya.suraksha.Helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.propya.suraksha.Activities.HelpAcknowledge;
import com.propya.suraksha.Constants;
import com.propya.suraksha.R;
import com.propya.suraksha.Services.ActivityRecognizer;
import com.propya.suraksha.Services.CheckSafetyAlways;

import java.util.Map;
import java.util.Objects;

public class NotificationHelpers {
    private Context c;

    public NotificationHelpers(Context c){
        this.c = c;
        registerChannel();
    }

    public void askingHelp(Map<String,String> data){
        Log.i("Asking helper",data.toString());
        NotificationCompat.Builder builder = getNotiSmall("Someone needs help",
                "Someone nearby needs your help...\nIt would be really great if you can have a look outside and help us", null);
        Intent i = new Intent(c, HelpAcknowledge.class);
        Bundle b= new Bundle();
        int notiId = 100;
        for(String k : data.keySet()){
            b.putString(k,data.get(k));
        }
        b.putInt("notiID",notiId);
        i.putExtra("data",b);
        PendingIntent activity = PendingIntent.getActivity(c, 5, i, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);
        builder.setStyle(new NotificationCompat.BigTextStyle()).addAction(0,"Help",activity);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        NotificationManagerCompat.from(c).notify(notiId,builder.build());
    }


    private void registerChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.NotifyConstants.NOTIFICATION_CHANNEL;
            String description =Constants.NotifyConstants.NOTIFICATION_CHANNEL;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constants.NotifyConstants.NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = c.getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }


    public void generateForeground(Bundle data){
        Intent i = new Intent(c, CheckSafetyAlways.class);
        i.putExtra("data",data);
        ContextCompat.startForegroundService(c,i);
//        Intent j = new Intent(c, ActivityRecognizer.class);
//
//        ContextCompat.startForegroundService(c,j);


    }


    public NotificationCompat.Builder getNotiSmall(String title, String msg, PendingIntent pendingIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, Constants.NotifyConstants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        if(pendingIntent != null){
            builder.setContentIntent(pendingIntent);
        }
        return builder;
    }

    public void updateNoti(int previd, Notification updatedNoti,Context context)
    {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(previd, updatedNoti);
    }


    public void timerNotif(final int previd, final Context context, long timeinmillis)
    {
        Constants.runningTriggered=true;
        ApiCallHelpers unsafehelper = new ApiCallHelpers(context);
//        unsafehelper.pingBack();//triggered im unsafe
        new CountDownTimer(timeinmillis, 1000) {

            public void onTick(long millisUntilFinished) {

                if(!Constants.runningTriggered)
                    return;
                long remSec= millisUntilFinished/1000;
                NotificationCompat.Builder builder = getNotiSmall("Running in an unsafe zone!","SOS will be triggered in "+remSec+" seconds!",null);
                Intent safe = new Intent(context,CheckSafetyAlways.class);
                safe.putExtra("type","alarm");
                safe.putExtra("alarm",10);
                PendingIntent safetyService=PendingIntent.getService(context,5,safe,PendingIntent.FLAG_UPDATE_CURRENT);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    safetyService = PendingIntent.getForegroundService(context, 5, safe, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                builder.addAction(0,"I'M SAFE!",safetyService);
                Notification notification = builder.build();
                updateNoti(previd,notification,context);
            }

            public void onFinish() {
                if(Constants.runningTriggered){
                    new ApiCallHelpers(c).raiseSOS();
                }
                Toast.makeText(context,"SOS raised",Toast.LENGTH_SHORT).show();
                Constants.runningTriggered=false;
                Constants.SOSRaised=true;
                NotificationManagerCompat.from(c).cancel(previd);


            }
        }.start();

    }



    public void NotifImSafe(final int previd, final Context context, long timeinmillis)
    {
        CheckSafetyAlways.isRunningUnsafe=true;
        new CountDownTimer(timeinmillis, 1000) {

            public void onTick(long millisUntilFinished) {

                if(!CheckSafetyAlways.isRunningUnsafe)
                    return;
                long remSec= millisUntilFinished/1000;
                NotificationCompat.Builder builder = getNotiSmall("Please Mark Yourself safe!","SOS will be triggered in "+remSec+" seconds!",null);
                Intent safe = new Intent(context,CheckSafetyAlways.class);
                safe.putExtra("type","alarm");
                safe.putExtra("alarm",11);
                PendingIntent safetyService=PendingIntent.getService(context,52,safe,0);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    safetyService = PendingIntent.getForegroundService(context, 52, safe, 0);
                }
                builder.addAction(0,"MARK SAFE!",safetyService);

                Intent safeEnd = new Intent(context,CheckSafetyAlways.class);
                safeEnd.putExtra("type","alarm");
                safeEnd.putExtra("alarm",15);
                PendingIntent safetyServiceEnd=PendingIntent.getService(context,51,safeEnd,0);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    safetyServiceEnd = PendingIntent.getForegroundService(context, 51, safeEnd,0);
                }
                builder.addAction(0,"Out",safetyServiceEnd);


                Notification notification = builder.build();
                updateNoti(previd,notification,context);
            }

            public void onFinish() {
                NotificationManagerCompat.from(c).cancel(previd);
                if(CheckSafetyAlways.isRunningUnsafe){
                    Toast.makeText(context,"SOS raised",Toast.LENGTH_SHORT).show();
                }else{

                    Intent i = new Intent(c,CheckSafetyAlways.class);
                    i.putExtra("type","alarm");
                    i.putExtra("alarm",-3);
                    ContextCompat.startForegroundService(c,i);
                }
                CheckSafetyAlways.isRunningUnsafe = false;
                Constants.SOSRaised=true;


            }
        }.start();

    }

}
