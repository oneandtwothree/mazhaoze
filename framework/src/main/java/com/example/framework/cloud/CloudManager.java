package com.example.framework.cloud;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.framework.R;
import com.example.framework.utils.LogUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

public class CloudManager {
    private static volatile CloudManager cloudManager = null;

    public static final String TOKEN_URL = "https://api-cn.ronghub.com/user/getToken.json";
    public static final String CLOUD_KEY = "8luwapkv848bl";
    public static final String CLOUD_SECRET = "nk2hOm5FhSG";

    public static final String MSG_TEXT_NAME = "RC:TxtMsg";
    public static final String MSG_IMAGE_NAME = "RC:ImgMsg";
    public static final String MSG_LOCATION_NAME = "RC:LBSMsg";
    public static final String MSG_GROUP_NAME = "RC:GRPMsg";


    public static final String TYPE_TEXT = "TYPE_TEXT";
    public static final String TYPE_ADD_FRIEND = "TYPE_ADD_FRIEND";
    public static final String TYPE_ARGEED_FRIEND = "TYPE_ARGEED_FRIEND";



    //来电铃声
    public static final String callAudioPath = "http://downsc.chinaz.net/Files/DownLoad/sound1/201501/5363.wav";
    //挂断铃声
    public static final String callAudioHangup = "http://downsc.chinaz.net/Files/DownLoad/sound1/201501/5351.wav";

    //------------------------------------------------------------------------------------------------------------------------------------------

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

    public boolean isConnect() {
        return RongIMClient.getInstance().getCurrentConnectionStatus()
                == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------

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

    private void sendTextMessage(String msg,String id){
        LogUtils.i("sendTextMessage");
        TextMessage obtain = TextMessage.obtain(msg);

        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, id, obtain, null, null,iSendMessageCallback);
    }


    public void sendTextMessage(String msg, String type, String targetId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", msg);
            //如果没有这个Type 就是一条普通消息
            jsonObject.put("type", type);
            sendTextMessage(jsonObject.toString(), targetId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendaddfriendTextMessage(String msg,String type,String id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg",msg);
            jsonObject.put("type",type);
            sendTextMessage(jsonObject.toString(),id);
       }catch (Exception e){
           e.printStackTrace();
       }
    }


    public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback){
        RongIMClient.getInstance().getConversationList(callback, Conversation.ConversationType.PRIVATE);
    }

    public void gethistoryMessages(String targerid, RongIMClient.ResultCallback<List<Message>> callback){
        RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.PRIVATE,targerid,-1,1000,callback);
    }


    public void getGroupList(RongIMClient.ResultCallback<List<Conversation>> callback){
        RongIMClient.getInstance().getConversationList(callback, Conversation.ConversationType.GROUP);
    }



    public void getRemotehistoryMessages(String targerid, RongIMClient.ResultCallback<List<Message>> callback){
        RongIMClient.getInstance().getRemoteHistoryMessages(Conversation.ConversationType.PRIVATE,targerid,0,20,callback);
    }

    private RongIMClient.SendImageMessageCallback sendImageMessageCallback = new RongIMClient.SendImageMessageCallback() {
        @Override
        public void onAttached(Message message) {
            LogUtils.i("onAttached");
        }

        @Override
        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
            LogUtils.i("onError"+errorCode);
        }

        @Override
        public void onSuccess(Message message) {
            LogUtils.i("onSuccess");
        }

        @Override
        public void onProgress(Message message, int i) {
            LogUtils.i("onProgress"+i);
        }
    };

    public void sendImageMessage(String id,File file){
        ImageMessage obtain = ImageMessage.obtain(Uri.fromFile(file), Uri.fromFile(file), true);
        RongIMClient.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE,id,obtain,null,null,sendImageMessageCallback);
    }
    public void sendLocationMessage(String mTargetId, double lat, double lng, String poi) {
        LocationMessage locationMessage = LocationMessage.obtain(lat, lng, poi, null);
        io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(
                mTargetId, Conversation.ConversationType.PRIVATE, locationMessage);
        RongIMClient.getInstance().sendLocationMessage(message,
                null, null, iSendMessageCallback);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------

    public void startCall(Context mContext,String id, RongCallCommon.CallMediaType mediaType){
        if (!isVoIPEnabled(mContext)) {
            return;
        }
        if(!isConnect()){
            Toast.makeText(mContext, mContext.getString(R.string.text_server_status), Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> userids = new ArrayList<>();
        userids.add(id);
        RongCallClient.getInstance().startCall(
                Conversation.ConversationType.PRIVATE,
                id,
                userids,
                null,
                mediaType,
                null
        );
    }

    public void startAudioCall(Context mContext,String id){
        if (!isVoIPEnabled(mContext)) {
            return;
        }
        startCall(mContext,id,RongCallCommon.CallMediaType.AUDIO);
    }
    public void startVideoCall(Context mContext,String id){
        if (!isVoIPEnabled(mContext)) {
            return;
        }
        startCall(mContext,id,RongCallCommon.CallMediaType.VIDEO);
    }


    public void setReceivedCallListener(IRongReceivedCallListener listener){
        if (null == listener) {
            return;
        }
        RongCallClient.setReceivedCallListener(listener);
    }
    //拨打
    public void acceptCall(String Callid){
        RongCallClient.getInstance().acceptCall(Callid);
    }
    //挂断
    public void hangUpCall(String Callid){
        RongCallClient.getInstance().hangUpCall(Callid);
    }
    //切换媒体
    public void changeCallMediaType(RongCallCommon.CallMediaType mediaType){
        RongCallClient.getInstance().changeCallMediaType(mediaType);
    }
    //切换摄像头
    public void switchCamera(){
        RongCallClient.getInstance().switchCamera();
    }
    //摄像头开关
    public void setEnableLocalVideo(boolean enabled){
        RongCallClient.getInstance().setEnableLocalVideo(enabled);
    }
    //音频开关
    public void setEnableLocalAudio(boolean enabled){
        RongCallClient.getInstance().setEnableLocalAudio(enabled);
    }
    //免提开关
    public void setEnableSpeakerphone(boolean enabled){
        RongCallClient.getInstance().setEnableSpeakerphone(enabled);
    }
    // 监听通话状态
    public void setVoIPCallListener(IRongCallListener listener) {
        if (null == listener) {
            return;
        }
        RongCallClient.getInstance().setVoIPCallListener(listener);
    }
    public boolean isVoIPEnabled(Context mContext) {
        if (!RongCallClient.getInstance().isVoIPEnabled(mContext)) {
            Toast.makeText(mContext, mContext.getString(R.string.text_devices_not_supper_audio), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
