package com.example.liaoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.example.framework.bmob.BmobManager;
import com.example.framework.cloud.CloudManager;
import com.example.framework.db.LitePalHelper;
import com.example.framework.db.NewFriend;
import com.example.framework.entity.Constants;
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.gson.TextBean;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.example.liaoapp.R;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

public class CloudService extends Service {
    private  Disposable disposable;

    private static final int H_TIME_WHAT = 1000;


    private int callTimer = 0;

    private boolean isMove = false;
    //是否拖拽
    private boolean isDrag = false;
    private int mLastX;
    private int mLastY;

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
            public boolean onReceived(final Message message, int i) {
                LogUtils.i("message:"+message);
                String objectName = message.getObjectName();
                if(objectName.equals(CloudManager.MSG_TEXT_NAME)){
                    TextMessage textMessage = (TextMessage) message.getContent();

                    String content = textMessage.getContent();
                    LogUtils.i("content:"+content);
                    final TextBean textBean = new Gson().fromJson(content, TextBean.class);

                    if(textBean.getType().equals(CloudManager.TYPE_TEXT)){
                        MessageEvent messageEvent = new MessageEvent(EventManager.FLAG_SEND_TEXT);
                        messageEvent.setText(textBean.getMsg());
                        messageEvent.setUserId(message.getSenderUserId());
                        EventManager.post(messageEvent);
                    }else if(textBean.getType().equals(CloudManager.TYPE_ADD_FRIEND)){
                        LogUtils.i("添加好友消息");

                        disposable  = Observable.create(new ObservableOnSubscribe<List<NewFriend>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<NewFriend>> emitter) throws Exception {
                                        emitter.onNext(LitePalHelper.getInstance().querynewfriend());
                                        emitter.onComplete();
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<NewFriend>>() {
                                    @Override
                                    public void accept(List<NewFriend> newFriends) throws Exception {
                                            if(CommonUtils.isEmpty(newFriends)){
                                                boolean isHave = false;
                                                for (int j = 0; j < newFriends.size() ; j++) {
                                                    NewFriend newFriend = newFriends.get(j);
                                                    if(message.getSenderUserId().equals(newFriend.getId())){
                                                        isHave = true;
                                                        break;
                                                    }
                                                }

                                                if(!isHave){
                                                   LitePalHelper.getInstance().saveNewFriend(textBean.getMsg(),message.getSenderUserId());
                                              }
                                          }else {
                                                LitePalHelper.getInstance().saveNewFriend(textBean.getMsg(),message.getSenderUserId());
                                            }
                                    }
                                });



                        LitePalHelper.getInstance().saveNewFriend(
                              textBean.getType(),
                              message.getSenderUserId()
                        );
                    }else if(textBean.getType().equals(CloudManager.TYPE_ARGEED_FRIEND)){
                        BmobManager.getInstance().addFriend(message.getSenderUserId(), new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                                }
                            }
                        });
                    }
                }else if(objectName.equals(CloudManager.MSG_IMAGE_NAME)){

                    ImageMessage content = (ImageMessage) message.getContent();
                    String url = content.getRemoteUri().toString();
                    if(!TextUtils.isEmpty(url)){
                        LogUtils.i("url"+url);
                        MessageEvent messageEvent = new MessageEvent(EventManager.FLAG_SEND_IMAGE);
                        messageEvent.setImgUrl(url);
                        messageEvent.setUserId(message.getSenderUserId());
                        EventManager.post(messageEvent);
                    }

                }else if (objectName.equals(CloudManager.MSG_LOCATION_NAME)) {
                LocationMessage locationMessage = (LocationMessage) message.getContent();
                LogUtils.e("locationMessage:" + locationMessage.toString());
                MessageEvent event = new MessageEvent(EventManager.FLAG_SEND_LOCATION);
                event.setLa(locationMessage.getLat());
                event.setLo(locationMessage.getLng());
                event.setUserId(message.getSenderUserId());
                event.setAddress(locationMessage.getPoi());
                EventManager.post(event);

            }
                return false;
            }
        });
    }


}
