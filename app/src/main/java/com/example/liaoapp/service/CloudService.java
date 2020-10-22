package com.example.liaoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.framework.cloud.CloudManager;
import com.example.framework.entity.Constants;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

public class CloudService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        linkcloudServer();
    }

    private void linkcloudServer() {
        String token = SpUtils.getInstance().getstring(Constants.SP_TOKEN, "");
        LogUtils.i("token:"+token);
        CloudManager.getInstance().connect(token);
        CloudManager.getInstance().setReceiveMessage(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                LogUtils.i("message:"+message);
                return false;
            }
        });
    }
}
