package com.example.liaoapp.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.cloud.CloudManager;
import com.example.framework.db.CallRecord;
import com.example.framework.db.LitePalHelper;
import com.example.framework.db.NewFriend;
import com.example.framework.entity.Constants;
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.gson.TextBean;
import com.example.framework.helper.NotificationHelper;
import com.example.framework.helper.WindowHelper;
import com.example.framework.manager.MediaPlayerManager;
import com.example.framework.utils.CommonUtils;
import com.example.framework.helper.GlideHelper;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.example.framework.utils.TimeUtils;
import com.example.liaoapp.Activity.ChatActivity;
import com.example.liaoapp.Activity.NewFriendActivity;
import com.example.liaoapp.MainActivity;
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

    private RelativeLayout videoBigVideo;
    private RelativeLayout videoSmallVideo;
    private LinearLayout videoLlInfo;
    private CircleImageView videoIvPhoto;
    private TextView videoTvName;
    private TextView videoTvStatus;
    private TextView videoTvTime;
    private LinearLayout videoLlAnswer;
    private LinearLayout videoLlHangup;


    private TextView mSmallTime;
    private LinearLayout mSmallLayout;

    private View AudioView;
    private View VideoView;
    private View smallAudioView;
    private String callId;

    private MediaPlayerManager mediaAutioCall;
    private MediaPlayerManager mediaAutioHangup;

    private SurfaceView mlocalView;
    private SurfaceView mRemoteView;

    private WindowManager.LayoutParams lp;

    private boolean isSpeaker = false;
    private boolean isSmallShow = false;


    //拨打状态
    private int isCallTo = 0;
    //接听状态
    private int isReceiverTo = 0;
    //拨打还是接听
    private boolean isCallOrReceiver = true;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what){
                case H_TIME_WHAT:
                    callTimer++;
                    String time = TimeUtils.formatDuring(callTimer * 1000);
                    audioTvStatus.setText(time);
                    videoTvTime.setText(time);
                    mSmallTime.setText(time);
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

        EventManager.register(this);

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


        VideoView = WindowHelper.getInstance().getview(R.layout.layout_chat_video);
        videoBigVideo = VideoView.findViewById(R.id.video_big_video);
        videoSmallVideo = VideoView.findViewById(R.id.video_small_video);
        videoLlInfo = VideoView.findViewById(R.id.video_ll_info);
        videoIvPhoto = VideoView.findViewById(R.id.video_iv_photo);
        videoTvName = VideoView.findViewById(R.id.video_tv_name);
        videoTvStatus = VideoView.findViewById(R.id.video_tv_status);
        videoTvTime = VideoView.findViewById(R.id.video_tv_time);
        videoLlAnswer = VideoView.findViewById(R.id.video_ll_answer);
        videoLlHangup = VideoView.findViewById(R.id.video_ll_hangup);

        videoLlAnswer.setOnClickListener(this);
        videoLlHangup.setOnClickListener(this);
        videoSmallVideo.setOnClickListener(this);

        createSmallAudioView();

    }

    private void createSmallAudioView() {

        lp = WindowHelper.getInstance().createLayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.TOP|Gravity.LEFT);

        smallAudioView = WindowHelper.getInstance().getview(R.layout.layout_chat_small_audio);
        mSmallTime = smallAudioView.findViewById(R.id.mSmallTime);
        mSmallLayout = smallAudioView.findViewById(R.id.mSmallLayout);

        smallAudioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowHelper.getInstance().hideview(smallAudioView);
                WindowHelper.getInstance().showview(AudioView);
            }
        });

        smallAudioView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int startX = (int) event.getRawX();
                int startY = (int) event.getRawY();



                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        isDrag = false;

                        mLastX = (int) event.getRawX();
                        mLastY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        int dx = startX - mLastX;
                        int dy = startY - mLastY;

                        if(isMove){
                            isDrag = true;
                        }else {
                            if(dx == 0 && dy ==0){
                                isMove = false;
                            }else{
                                isMove = true;
                                isDrag = true;
                            }
                        }

                        lp.x += dx;
                        lp.y += dy;

                        mLastX = startX;
                        mLastY = startY;

                        WindowHelper.getInstance().updateview(smallAudioView,lp);
                        break;
                }
                return isDrag;
            }
        });
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
                        pushSystem(message.getSenderUserId(), 1, 0, 0, textBean.getMsg());
                    }else if(textBean.getType().equals(CloudManager.TYPE_ADD_FRIEND)){

                        //存入数据库 Bmob RongCloud 都没有提供存储方法
                        //使用另外的方法来实现 存入本地数据库
                        LogUtils.i("添加好友消息");
                        saveNewFriend(textBean.getMsg(), message.getSenderUserId());
                        //查询数据库如果有重复的则不添加
                        //防止漏了消息，暂时对消息不过滤处理
//                disposable = Observable.create((ObservableOnSubscribe<List<NewFriend>>) emitter -> {
//                    emitter.onNext(LitePalHelper.getInstance().queryNewFriend());
//                    emitter.onComplete();
//                }).subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(newFriends -> {
//                            if (CommonUtils.isEmpty(newFriends)) {
//                                boolean isHave = false;
//                                for (int j = 0; j < newFriends.size(); j++) {
//                                    NewFriend newFriend = newFriends.get(j);
//                                    if (message.getSenderUserId().equals(newFriend.getId())) {
//                                        isHave = true;
//                                        break;
//                                    }
//                                }
//                                //防止重复添加
//                                if (!isHave) {
//                                    saveNewFriend(textBean.getMsg(), message.getSenderUserId());
//                                }
//                            } else {
//                                saveNewFriend(textBean.getMsg(), message.getSenderUserId());
//                            }
//                        });
                    }else if(textBean.getType().equals(CloudManager.TYPE_ARGEED_FRIEND)){
                        BmobManager.getInstance().addFriend(message.getSenderUserId(), new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    pushSystem(message.getSenderUserId(), 0, 1, 0, "");
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
                        pushSystem(message.getSenderUserId(), 1, 0, 0, getString(R.string.text_chat_record_img));
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
                    pushSystem(message.getSenderUserId(), 1, 0, 0, getString(R.string.text_chat_record_location));
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

                mediaAutioCall.startPlay(CloudManager.callAudioPath);
                updateWindowInfo(1,rongCallSession.getMediaType(),callerUserId);

                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                    WindowHelper.getInstance().showview(AudioView);

                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){
                    WindowHelper.getInstance().showview(VideoView);
                }


                isReceiverTo = 1;

                isCallOrReceiver = false;
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

                isCallTo = 1;
                isCallOrReceiver = true;
                String targetId = rongCallSession.getTargetId();
                callId = rongCallSession.getCallId();

                updateWindowInfo(0,rongCallSession.getMediaType(),targetId);
                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                    WindowHelper.getInstance().showview(AudioView);
                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){
                    WindowHelper.getInstance().showview(VideoView);
                    mlocalView  = surfaceView;
                    videoBigVideo.addView(mlocalView);
                }
            }

            @Override
            public void onCallConnected(RongCallSession rongCallSession, SurfaceView surfaceView) {
                LogUtils.i("onCallConnected");

                if(mediaAutioCall.isPlaying()){
                    mediaAutioCall.stopPlay();
                }
                isCallTo = 2;
                isReceiverTo  = 2;

                handler.sendEmptyMessage(H_TIME_WHAT);

                if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                    goneAudioView(true,false,true,true,true);
                }else if(rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){
                    goneVideoView(false,true,true,false,true,true);
                    mlocalView = surfaceView;
                }


            }

            @Override
            public void onCallDisconnected(RongCallSession rongCallSession, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {
                LogUtils.i("onCallDisconnected ");
                String callUserId = rongCallSession.getCallerUserId();
                String recevierId = rongCallSession.getTargetId();

                handler.removeMessages(H_TIME_WHAT);
                callTimer = 0;

                mediaAutioCall.pausePlay();
                mediaAutioHangup.startPlay(CloudManager.callAudioHangup);

                if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
                    if (isCallOrReceiver) {
                        if (isCallTo == 1) {
                            //代表只拨打，但是并没有接通
                            saveAudioRecord(recevierId, CallRecord.CALL_STATUS_DIAL);
                        } else if (isCallTo == 2) {
                            saveAudioRecord(recevierId, CallRecord.CALL_STATUS_ANSWER);
                        }
                    } else {
                        if (isReceiverTo == 1) {
                            saveAudioRecord(callUserId, CallRecord.CALL_STATUS_UN_ANSWER);
                        } else if (isReceiverTo == 2) {
                            saveAudioRecord(callUserId, CallRecord.CALL_STATUS_ANSWER);
                        }
                    }

                } else if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
                    if (isCallOrReceiver) {
                        if (isCallTo == 1) {
                            //代表只拨打，但是并没有接通
                            saveVideoRecord(recevierId, CallRecord.CALL_STATUS_DIAL);
                        } else if (isCallTo == 2) {
                            saveVideoRecord(recevierId, CallRecord.CALL_STATUS_ANSWER);
                        }
                    } else {
                        if (isReceiverTo == 1) {
                            saveVideoRecord(callUserId, CallRecord.CALL_STATUS_UN_ANSWER);
                        } else if (isReceiverTo == 2) {
                            saveVideoRecord(callUserId, CallRecord.CALL_STATUS_ANSWER);
                        }
                    }
                }
                WindowHelper.getInstance().hideview(AudioView);
                WindowHelper.getInstance().hideview(smallAudioView);
                WindowHelper.getInstance().hideview(VideoView);

                isCallTo = 0;
                isReceiverTo  = 0;

            }

            @Override
            public void onRemoteUserRinging(String s) {

            }

            @Override
            public void onRemoteUserJoined(String s, RongCallCommon.CallMediaType callMediaType, int i, SurfaceView surfaceView) {
                MessageEvent messageEvent = new MessageEvent(EventManager.FLAG_SEND_CAMERA_VIEW);
                messageEvent.setmSurfaceView(surfaceView);
                EventManager.post(messageEvent);
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

    private void saveNewFriend(String msg, String senderUserId) {
        pushSystem(senderUserId, 0, 0, 0, msg);
        LitePalHelper.getInstance().saveNewFriend(msg, senderUserId);
    }



    private void goneAudioView(boolean recording,boolean answer,boolean hangup,boolean hf,boolean small){
        audioLlRecording.setVisibility(recording ? View.VISIBLE : View.GONE);
        audioLlAnswer.setVisibility(answer ? View.VISIBLE : View.GONE);
        audioLlHangup.setVisibility(hangup ? View.VISIBLE : View.GONE);
        audioLlHf.setVisibility(hf ? View.VISIBLE : View.GONE);
        audioIvSmall.setVisibility(small ? View.VISIBLE : View.GONE);

    }
    private void goneVideoView(boolean info,boolean small,boolean big,boolean answer,boolean hanguo,boolean time){
        videoLlInfo.setVisibility(info ? View.VISIBLE : View.GONE);
        videoBigVideo.setVisibility(big ? View.VISIBLE : View.GONE);
        videoSmallVideo.setVisibility(small ? View.VISIBLE : View.GONE);
        videoLlAnswer.setVisibility(answer ? View.VISIBLE : View.GONE);
        videoLlHangup.setVisibility(hanguo ? View.VISIBLE : View.GONE);
        videoTvTime.setVisibility(time ? View.VISIBLE : View.GONE);

    }
    private void saveAudioRecord(String id, int callStatus) {
        LitePalHelper.getInstance()
                .saveCallRecord(id, CallRecord.MEDIA_TYPE_AUDIO, callStatus);
    }

    private void saveVideoRecord(String id, int callStatus) {
        LitePalHelper.getInstance()
                .saveCallRecord(id, CallRecord.MEDIA_TYPE_VIDEO, callStatus);
    }


    private void updateWindowInfo(final int index, final RongCallCommon.CallMediaType type, String callerUserId) {
        if(type.equals(RongCallCommon.CallMediaType.AUDIO)){
            if(index == 1){
                goneAudioView(false,true,true,false,false);
            }else if(index == 0){
                goneAudioView(false,false,true,false,false);
            }
        }else if(type.equals(RongCallCommon.CallMediaType.VIDEO)){
            if(index == 1){
                goneVideoView(true,false,false,true,true,false);
            }else if(index == 0){
                goneVideoView(true,false,true,false,true,false);
            }
        }


        BmobManager.getInstance().queryidFriend(callerUserId, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        IMUser imUser = list.get(0);

                        if(type.equals(RongCallCommon.CallMediaType.AUDIO)){
                            GlideHelper.setimg(CloudService.this,imUser.getPhoto(),audioIvPhoto);
                            if(index == 1){
                                audioTvStatus.setText(imUser.getNickName() + getString(R.string.text_service_calling));
                            }else if(index == 0){
                                audioTvStatus.setText(getString(R.string.text_service_call_ing) + imUser.getNickName() + "...");
                            }
                        }else if (type.equals(RongCallCommon.CallMediaType.VIDEO)) {
                            GlideHelper.setimg(CloudService.this, imUser.getPhoto(), videoIvPhoto);
                            videoTvName.setText(imUser.getNickName());
                            if (index == 1) {
                                videoTvStatus.setText(imUser.getNickName() + getString(R.string.text_service_video_calling));
                            } else if (index == 0) {
                                videoTvStatus.setText(getString(R.string.text_service_call_video_ing) + imUser.getNickName() + "...");
                            }
                        }
                    }
                }
            }
        });
    }

    


    private void updateVideoview() {
            videoBigVideo.removeAllViews();
            videoSmallVideo.removeAllViews();
            if(isSmallShow){
                if(mlocalView != null){
                    videoSmallVideo.addView(mlocalView);
                    mlocalView.setZOrderOnTop(true);
                }
                if(mRemoteView != null){
                    videoBigVideo.addView(mRemoteView);
                    mRemoteView.setZOrderOnTop(false);
                }
            }else {
                if(mlocalView != null){
                    videoBigVideo.addView(mlocalView);
                    mlocalView.setZOrderOnTop(false);
                }
                if(mRemoteView != null){
                    videoSmallVideo.addView(mRemoteView);
                    mRemoteView.setZOrderOnTop(true);
                }
            }
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
                break;
            case R.id.audio_iv_small:
                WindowHelper.getInstance().hideview(AudioView);
                WindowHelper.getInstance().showview(smallAudioView,lp);
                break;
            case R.id.video_ll_answer:
                CloudManager.getInstance().acceptCall(callId);
                break;
            case R.id.video_ll_hangup:
                CloudManager.getInstance().hangUpCall(callId);
                break;
            case R.id.video_small_video:
                isSmallShow = !isSmallShow;
                updateVideoview();
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case EventManager.FLAG_SEND_CAMERA_VIEW:

                SurfaceView surfaceView = messageEvent.getmSurfaceView();
                if(surfaceView != null){
                    mRemoteView = surfaceView;
                }
                updateVideoview();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != disposable){
            if(disposable.isDisposed()){
                disposable.dispose();
            }
        }

        EventManager.unregister(this);
    }


    private void pushSystem(final String id, final int type, final int friendType, final int messageType, final String msgText) {
        LogUtils.i("pushSystem");
        BmobManager.getInstance().queryidFriend(id, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        IMUser imUser = list.get(0);
                        String text = "";
                        if (type == 0) {
                            switch (friendType) {
                                case 0:
                                    text = imUser.getNickName() + getString(R.string.text_server_noti_send_text);
                                    break;
                                case 1:
                                    text = imUser.getNickName() + getString(R.string.text_server_noti_receiver_text);
                                    break;
                            }
                        } else if (type == 1) {
                            switch (messageType) {
                                case 0:
                                    text = msgText;
                                    break;
                                case 1:
                                    text = getString(R.string.text_chat_record_img);
                                    break;
                                case 2:
                                    text = getString(R.string.text_chat_record_location);
                                    break;
                            }
                        }
                        pushBitmap(type, friendType, imUser, imUser.getNickName(), text, imUser.getPhoto());
                    }
                }
            }
        });
    }

    /**
     * 发送通知
     *
     * @param type       0：特殊消息 1：聊天消息
     * @param friendType 0: 添加好友请求 1：同意好友请求
     * @param imUser     用户对象
     * @param title      标题
     * @param text       内容
     * @param url        头像Url
     */
    private void pushBitmap(final int type, final int friendType, final IMUser imUser, final String title, final String text, String url) {
        LogUtils.i("pushBitmap");
        GlideHelper.loadUrlToBitmap(this, url, new GlideHelper.OnGlideBitmapResultListener() {
            @Override
            public void onResourceReady(Bitmap resource) {
                if (type == 0) {
                    if (friendType == 0) {
                        Intent intent = new Intent(CloudService.this, NewFriendActivity.class);
                        PendingIntent pi = PendingIntent.getActivities(CloudService.this, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
                        NotificationHelper.getInstance().pushAddFriendNotification(imUser.getObjectId(), title, text, resource, pi);
                    } else if (friendType == 1) {
                        Intent intent = new Intent(CloudService.this, MainActivity.class);
                        PendingIntent pi = PendingIntent.getActivities(CloudService.this, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
                        NotificationHelper.getInstance().pushArgeedFriendNotification(imUser.getObjectId(), title, text, resource, pi);
                    }
                } else if (type == 1) {
                    Intent intent = new Intent(CloudService.this, ChatActivity.class);
                    intent.putExtra(Constants.INTENT_USER_ID, imUser.getObjectId());
                    intent.putExtra(Constants.INTENT_USER_NAME, imUser.getNickName());
                    intent.putExtra(Constants.INTENT_USER_PHOTO, imUser.getPhoto());
                    PendingIntent pi = PendingIntent.getActivities(CloudService.this, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
                    NotificationHelper.getInstance().pushMessageNotification(imUser.getObjectId(), title, text, resource, pi);
                }
            }
        });
    }
}
