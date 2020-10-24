package com.example.liaoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.framework.cloud.CloudManager;
import com.example.framework.db.LitePalHelper;
import com.example.framework.entity.Constants;
import com.example.framework.gson.TextBean;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.google.gson.Gson;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

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
                String objectName = message.getObjectName();
                if(objectName.equals(CloudManager.MSG_TEXT_NAME)){
                    TextMessage textMessage = (TextMessage) message.getContent();

                    String content = textMessage.getContent();
                    LogUtils.i("content:"+content);
                    TextBean textBean = new Gson().fromJson(content, TextBean.class);


                    if(textBean.getType().equals(CloudManager.TYPE_TEXT)){

                    }else if(textBean.getType().equals(CloudManager.TYPE_ADD_FRIEND)){
                        LogUtils.i("添加好友成功");
                        LitePalHelper.getInstance().saveNewFriend(
                              textBean.getType(),
                              message.getSenderUserId()
                        );
                    }else if(textBean.getType().equals(CloudManager.TYPE_ARGEED_FRIEND)){

                    }

                }
                return false;
            }
        });
    }
}
