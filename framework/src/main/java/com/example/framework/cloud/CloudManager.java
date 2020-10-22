package com.example.framework.cloud;

import android.content.Context;

import com.example.framework.R;
import com.example.framework.utils.LogUtils;

import io.rong.imlib.RongIMClient;

public class CloudManager {
    private static volatile CloudManager cloudManager = null;

    public static final String TOKEN_URL = "https://api-cn.ronghub.com/user/getToken.json";
    public static final String CLOUD_KEY = "8luwapkv848bl";
    public static final String CLOUD_SECRET = "nk2hOm5FhSG";


    public CloudManager() {
    }

    public static CloudManager getInstance(){
        if(cloudManager == null){
            synchronized (CloudManager.class){
                if(cloudManager == null){
                    cloudManager = new CloudManager();
                }
            }
        }
        return cloudManager;
    }

    public void initCloud(Context context){
        RongIMClient.init(context,CLOUD_KEY);
    }

    public void connect(String token){
       RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
           @Override
           public void onSuccess(String s) {
                LogUtils.e("连接成功"+s);
           }

           @Override
           public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
               LogUtils.e("连接失败"+connectionErrorCode);
           }

           @Override
           public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

           }
       });
    }

    public void disconnect(){
        RongIMClient.getInstance().disconnect();
    }

    public void logout(){
        RongIMClient.getInstance().logout();
    }


    public void setReceiveMessage(RongIMClient.OnReceiveMessageListener listener){
        RongIMClient.setOnReceiveMessageListener(listener);
    }
}
