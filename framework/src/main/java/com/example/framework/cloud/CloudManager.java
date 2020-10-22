package com.example.framework.cloud;

import android.content.Context;

import com.example.framework.R;
import com.example.framework.utils.LogUtils;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class CloudManager {
    private static volatile CloudManager cloudManager = null;

    public static final String TOKEN_URL = "https://api-cn.ronghub.com/user/getToken.json";
    public static final String CLOUD_KEY = "8luwapkv848bl";
    public static final String CLOUD_SECRET = "nk2hOm5FhSG";



    public static final String MSG_TEXT_NAME = "RC:TxtMsg";
    public static final String MSG_IMAGE_NAME = "RC:ImgMsg";
    public static final String MSG_LOCATION_NAME = "RC:LBSMsg";




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
               CloudManager.getInstance().sendTextMessage("很高兴见到你","96dd8823d7");
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

    private IRongCallback.ISendMessageCallback iSendMessageCallback
            = new IRongCallback.ISendMessageCallback() {

        @Override
        public void onAttached(Message message) {
            // 消息成功存到本地数据库的回调
        }

        @Override
        public void onSuccess(Message message) {
            // 消息发送成功的回调
            LogUtils.i("sendMessage onSuccess");
        }

        @Override
        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
            // 消息发送失败的回调
            LogUtils.e("sendMessage onError:" + errorCode);
        }
    };

    public void sendTextMessage(String msg,String id){
        TextMessage obtain = TextMessage.obtain(msg);

        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, id, obtain, null, null,iSendMessageCallback);
    }

}
