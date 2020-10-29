package com.example.liaoapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.cloud.CloudManager;
import com.example.framework.db.LitePalHelper;
import com.example.framework.db.NewFriend;
import com.example.framework.entity.Constants;
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.gson.TextBean;
import com.example.framework.helper.WindowHelper;
import com.example.framework.manager.MediaPlayerManager;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.GlideHelper;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.example.framework.utils.TimeUtils;
import com.example.liaoapp.R;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

public class CloudService extends Service implements View.OnClickListener {
    private  Disposable disposable;

    private static final int H_TIME_WHAT = 1000;
    private int callTimer = 0;

    private boolean isMove = false;
    //是否拖拽
    private boolean isDrag = false;
    private int mLastX;
    private int mLastY;

    private CircleImageView audioIvPhoto;
    private TextView audioTvStatus;
    private LinearLayout audioLlRecording;
    private ImageView audioIvRecording;
    private LinearLayout audioLlAnswer;
    private ImageView audioIvAnswer;
    private LinearLayout audioLlHangup;
    private ImageView audioIvHangup;
    private LinearLayout audioLlHf;
    private ImageView audioIvHf;
    private ImageView audioIvSmall;

    private View AudioView;
    private String callId;

    private MediaPlayerManager mediaAutioCall;
    private MediaPlayerManager mediaAutioHangup;

    private boolean isSpeaker = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what){
                case H_TIME_WHAT:
                    callTimer++;
                    String time = TimeUtils.formatDuring(callTimer * 1000);
                    audioTvStatus.setText(time);
                    handler.sendEmptyMessageDelayed(H_TIME_WHAT,1000);
                    break;
            }
            return false;
        }
    });


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
        initWindow();
        linkcloudServer();
    }

    private void initService() {
        mediaAutioCall = new MediaPlayerManager();
        mediaAutioHangup = new MediaPlayerManager();

        mediaAutioCall.setOnComplteionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaAutioCall.startPlay(CloudManager.callAudioPath);
            }
        });



    }

    private void initWindow() {
        AudioView = WindowHelper.getInstance().getview(R.layout.layout_chat_audio);
        audioIvPhoto = AudioView.findViewById(R.id.audio_iv_photo);
        audioTvStatus = AudioView.findViewById(R.id.audio_tv_status);
        audioLlRecording = AudioView.findViewById(R.id.audio_ll_recording);
        audioIvRecording = AudioView.findViewById(R.id.audio_iv_recording);
        audioLlAnswer = AudioView.findViewById(R.id.audio_ll_answer);
        audioIvAnswer = AudioView.findViewById(R.id.audio_iv_answer);
        audioLlHangup = AudioView.findViewById(R.id.audio_ll_hangup);
        audioIvHangup = AudioView.findViewById(R.id.audio_iv_hangup);
        audioLlHf = AudioView.findViewById(R.id.audio_ll_hf);
        audioIvHf = AudioView.findViewById(R.id.audio_iv_hf);
        audioIvSmall = AudioView.findViewById(R.id.audio_iv_small);

        audioLlRecording.setOnClickListener(this);
        audioLlAnswer.setOnClickListener(this);
        audioLlHangup.setOnClickListener(this);
        audioLlHf.setOnClickListener(this);
        audioIvSmall.setOnClickListener(this);

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

                MessageEvent event = new MessageEvent(EventManager.FLAG_UPDATE_FRIEND_LIST);
                EventManager.post(event);

                return false;
            }
        });


        CloudManager.getInstance().setReceivedCallListener(new IRongReceivedCallListener() {
            @Override
            public void onReceivedCall(RongCallSession rongCallSession) {
                LogUtils.i("onReceivedCall");
                if (!CloudManager.getInstance().isVoIPEnabled(CloudService.this)) {
                    return;
                }
                String callerUserId = rongCallSession.getCallerUserId();
                callId = rongCallSession.getCallId();
                updateWindowInfo(1,callerUserId);

                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                    WindowHelper.getInstance().showview(AudioView);
                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){
                    LogUtils.i("视频通话");
                }
            }

            @Override
            public void onCheckPermission(RongCallSession rongCallSession) {
                LogUtils.i("onCheckPermission");
            }
        });






        CloudManager.getInstance().setVoIPCallListener(new IRongCallListener() {
            @Override
            public void onCallOutgoing(RongCallSession rongCallSession, SurfaceView surfaceView) {
                LogUtils.i("onCallOutgoing");

                String targetId = rongCallSession.getTargetId();
                updateWindowInfo(0,targetId);

                callId = rongCallSession.getCallId();
                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                    WindowHelper.getInstance().showview(AudioView);
                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){

                }
            }

            @Override
            public void onCallConnected(RongCallSession rongCallSession, SurfaceView surfaceView) {
                LogUtils.i("onCallConnected");

                if(mediaAutioCall.isPlaying()){
                    mediaAutioCall.stopPlay();
                }
                handler.sendEmptyMessage(H_TIME_WHAT);
                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){

                    goneAudioView(true,false,true,true,true);
                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){

                }


            }

            @Override
            public void onCallDisconnected(RongCallSession rongCallSession, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {
                LogUtils.i("onCallDisconnected ");

                handler.removeMessages(H_TIME_WHAT);
                callTimer = 0;

                mediaAutioCall.pausePlay();
                mediaAutioHangup.startPlay(CloudManager.callAudioHangup);

                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                        WindowHelper.getInstance().hideview(AudioView);
                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){

                }
            }

            @Override
            public void onRemoteUserRinging(String s) {

            }

            @Override
            public void onRemoteUserJoined(String s, RongCallCommon.CallMediaType callMediaType, int i, SurfaceView surfaceView) {

            }

            @Override
            public void onRemoteUserInvited(String s, RongCallCommon.CallMediaType callMediaType) {

            }

            @Override
            public void onRemoteUserLeft(String s, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {

            }

            @Override
            public void onMediaTypeChanged(String s, RongCallCommon.CallMediaType callMediaType, SurfaceView surfaceView) {

            }

            @Override
            public void onError(RongCallCommon.CallErrorCode callErrorCode) {

            }

            @Override
            public void onRemoteCameraDisabled(String s, boolean b) {

            }

            @Override
            public void onRemoteMicrophoneDisabled(String s, boolean b) {

            }

            @Override
            public void onNetworkReceiveLost(String s, int i) {

            }

            @Override
            public void onNetworkSendLost(int i, int i1) {

            }

            @Override
            public void onFirstRemoteVideoFrame(String s, int i, int i1) {

            }

            @Override
            public void onAudioLevelSend(String s) {

            }

            @Override
            public void onAudioLevelReceive(HashMap<String, String> hashMap) {

            }

            @Override
            public void onRemoteUserPublishVideoStream(String s, String s1, String s2, SurfaceView surfaceView) {

            }

            @Override
            public void onRemoteUserUnpublishVideoStream(String s, String s1, String s2) {

            }
        });

    }


    private void goneAudioView(boolean recording,boolean answer,boolean hangup,boolean hf,boolean small){
        audioLlRecording.setVisibility(recording ? View.VISIBLE : View.GONE);
        audioLlAnswer.setVisibility(answer ? View.VISIBLE : View.GONE);
        audioLlHangup.setVisibility(hangup ? View.VISIBLE : View.GONE);
        audioLlHf.setVisibility(hf ? View.VISIBLE : View.GONE);
        audioIvSmall.setVisibility(small ? View.VISIBLE : View.GONE);

    }


    private void updateWindowInfo(final int index, String callerUserId) {

        if(index == 1){
            goneAudioView(false,true,true,false,false);
            mediaAutioCall.startPlay(CloudManager.callAudioPath);
        }else if(index == 0){
            goneAudioView(false,false,true,false,false);
        }

        BmobManager.getInstance().queryidFriend(callerUserId, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        IMUser imUser = list.get(0);
                        GlideHelper.setimg(CloudService.this,imUser.getPhoto(),audioIvPhoto);
                        if(index == 1){
                            audioTvStatus.setText(imUser.getNickName() + getString(R.string.text_service_calling));
                        }else if(index == 0){
                            audioTvStatus.setText(getString(R.string.text_service_call_ing) + imUser.getNickName() + "...");
                        }
                    }
                }
            }
        });
    }

   @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_ll_recording:
                Toast.makeText(this, "暂不支持录音功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.audio_ll_answer:
                CloudManager.getInstance().acceptCall(callId);
                break;
            case R.id.audio_ll_hangup:
                CloudManager.getInstance().hangUpCall(callId);
                break;
            case R.id.audio_ll_hf:
                    isSpeaker = !isSpeaker;
                CloudManager.getInstance().setEnableSpeakerphone(isSpeaker);
                    audioIvHf.setImageResource(isSpeaker?R.drawable.img_hf_p:R.drawable.img_hf);
                CloudManager.getInstance().setEnableSpeakerphone(isSpeaker);
                break;
            case R.id.audio_iv_small:
                break;
        }
    }
}
