package com.example.framework;

import android.content.Context;

import com.example.framework.bmob.BmobManager;
import com.example.framework.cloud.CloudManager;
import com.example.framework.manager.MapManager;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;

import org.litepal.LitePal;

public class Framework {
    private volatile  static  Framework framework;
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
    }
}
