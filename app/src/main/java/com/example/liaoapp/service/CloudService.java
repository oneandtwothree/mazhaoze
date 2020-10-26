package com.example.liaoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class CloudService extends Service {
    private  Disposable disposable;

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
                    final TextBean textBean = new TextBean();
                    textBean.setMsg(content);
                    textBean.setType(CloudManager.TYPE_TEXT);

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

                    }

                }
                return false;
            }
        });
    }


}
