package com.example.framework;

import android.content.Context;

import com.example.framework.bmob.BmobManager;
import com.example.framework.cloud.CloudManager;
import com.example.framework.helper.WindowHelper;
import com.example.framework.manager.MapManager;
import com.example.framework.manager.VoiceManager;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePal;

public class Framework {
    private volatile  static  Framework framework;

    private String BUGLY_KEY = "fbf89df081";

    private Framework(){

    }

    public static  Framework getInstance(){
        if(framework == null){
            synchronized (Framework.class){
                if(framework == null){
                    framework = new Framework();
                }
            }
        }
        return framework;
    }

    public void initFramework(Context mContext) {
        LogUtils.i("initFramework");
        SpUtils.getInstance().initSp(mContext);
        BmobManager.getInstance().initbmob(mContext);
        CloudManager.getInstance().initCloud(mContext);
        LitePal.initialize(mContext);
        MapManager.getInstance().initMap(mContext);
        WindowHelper.getInstance().initWindow(mContext);
        CrashReport.initCrashReport(mContext, BUGLY_KEY, BuildConfig.LOG_DEBUG);
        ZXingLibrary.initDisplayOpinion(mContext);

    }
}
