package com.example.liaoapp.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.example.framework.Framework;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(getApplicationInfo().packageName.equals(
                getCurProcessName(getApplicationContext()))){
            Framework.getInstance().initFramework(this);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
