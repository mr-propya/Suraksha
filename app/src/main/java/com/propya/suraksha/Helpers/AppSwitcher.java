package com.propya.suraksha.Helpers;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.propya.suraksha.Constants;
import com.rvalerio.fgchecker.AppChecker;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppSwitcher {
    Context c;
    AppSwitched callback;
    Handler handler;
    private boolean keepRunning = true;
    private String lastUsedName;

    public AppSwitcher(Context c,AppSwitched callback){
        this.c = c;
        this.callback = callback;
        this.handler = new Handler();
    }

    public void start(){
        keepRunning = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run(){
                while (keepRunning){
                    printForegroundTask();
                    try {
                        Thread.sleep(Constants.TrackTravelConstants.checkActivity*1000);
                    }catch (Exception e){}
                }

            }
        });
        t.start();

    }
    public void stop(){
        keepRunning = false;

    }

    private void printForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) c.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.i("foreground 2", "Current App in foreground is: " + currentApp);
        if(currentApp.contains("android") || currentApp.contains(c.getPackageName())){
            return;
        }
        if(lastUsedName == null){
            lastUsedName = currentApp;
        }
        if(!lastUsedName.equals(currentApp)){
            Log.i("foreground","new app is "+currentApp);
            notify(currentApp);
        }
        lastUsedName = currentApp;
    }
   private void notify(final String name){

        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.switched(name);
            }
        });

   }


    public interface AppSwitched{
        public void switched(String packageName);
    }
}
