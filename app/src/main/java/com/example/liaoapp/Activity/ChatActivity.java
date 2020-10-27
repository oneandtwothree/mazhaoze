package com.example.liaoapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.adapter.CommonAdapter;
import com.example.framework.adapter.CommonViewHolder;
import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.cloud.CloudManager;
import com.example.framework.entity.Constants;
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.gson.TextBean;
import com.example.framework.helper.FileHelper;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.liaoapp.R;
import com.example.liaoapp.model.ChatModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

public class ChatActivity extends BaseBackActivity implements View.OnClickListener {

    private static final int TYPE_LEFT_TEXT = 0;
    private static final int TYPE_LEFT_IMAGE = 1;
    private static final int TYPE_LEFT_LOCATION = 2;

    private static final int TYPE_RIGHT_TEXT = 3;
    private static final int TYPE_RIGHT_IMAGE = 4;
    private static final int TYPE_RIGHT_LOCATION = 5;



    private LinearLayout llChatBg;
    private RecyclerView mChatView;
    private EditText etInputMsg;
    private Button btnSendMsg;
    private LinearLayout llVoice;
    private LinearLayout llCamera;
    private LinearLayout llPic;
    private LinearLayout llLocation;


    private String youruserid;
    private String yourusername;
    private String youruserphoto;

    private String myuserphoto;

    private File upload = null;


    private CommonAdapter<ChatModel> modelCommonAdapter;
    private List<ChatModel> mList = new ArrayList<>();

    public static void startActivity(Context context,String userid,String username,String userphoto){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.INTENT_USER_ID,userid);
        intent.putExtra(Constants.INTENT_USER_NAME,username);
        intent.putExtra(Constants.INTENT_USER_PHOTO,userphoto);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initview();

    }

    private void initview() {
        llChatBg = findViewById(R.id.ll_chat_bg);
        mChatView = findViewById(R.id.mChatView);
        etInputMsg = findViewById(R.id.et_input_msg);
        btnSendMsg = findViewById(R.id.btn_send_msg);
        llVoice = findViewById(R.id.ll_voice);
        llCamera = findViewById(R.id.ll_camera);
        llPic = findViewById(R.id.ll_pic);
        llLocation = findViewById(R.id.ll_location);

        btnSendMsg.setOnClickListener(this);
        llVoice.setOnClickListener(this);
        llCamera.setOnClickListener(this);
        llPic.setOnClickListener(this);
        llLocation.setOnClickListener(this);

        mChatView.setLayoutManager(new LinearLayoutManager(this));

        modelCommonAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnMoreBindDataListener<ChatModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(final ChatModel model, CommonViewHolder commonViewHolder, int type, int position) {
                    switch (model.getType()){
                        case TYPE_LEFT_TEXT:
                            commonViewHolder.setText(R.id.tv_left_text,model.getText());
                            commonViewHolder.setImgurl(ChatActivity.this,R.id.iv_left_photo,youruserphoto);
                            break;
                        case TYPE_RIGHT_TEXT:
                            commonViewHolder.setText(R.id.tv_right_text,model.getText());
                            commonViewHolder.setImgurl(ChatActivity.this,R.id.iv_right_photo,myuserphoto);
                            break;
                        case TYPE_LEFT_IMAGE:
                            commonViewHolder.setImgurl(ChatActivity.this,R.id.iv_left_img,model.getImgurl());
                            commonViewHolder.setImgurl(ChatActivity.this,R.id.iv_left_photo,youruserphoto);

                            commonViewHolder.getView(R.id.iv_left_img).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ImagePreviewActivity.startActivity(ChatActivity.this,model.getImgurl(),true);
                                }
                            });

                            break;
                        case TYPE_RIGHT_IMAGE:
                            if(TextUtils.isEmpty(model.getImgurl())){
                                if(model.getFile() != null){
                                    commonViewHolder.setImgFile(ChatActivity.this,R.id.iv_right_img,model.getFile());
                                    commonViewHolder.getView(R.id.iv_right_img).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImagePreviewActivity.startActivity(ChatActivity.this,model.getFile().getPath(),false);
                                        }
                                    });
                                }
                            }else {
                                commonViewHolder.setImgurl(ChatActivity.this,R.id.iv_right_img,model.getImgurl());
                                commonViewHolder.getView(R.id.iv_right_img).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ImagePreviewActivity.startActivity(ChatActivity.this,model.getImgurl(),true);
                                    }
                                });
                            }
                            commonViewHolder.setImgurl(ChatActivity.this,R.id.iv_right_photo,myuserphoto);
                            break;
                        case TYPE_LEFT_LOCATION:
                            break;
                        case TYPE_RIGHT_LOCATION:
                            break;
                    }


            }

            @Override
            public int getLayoutId(int type) {
                if(type == TYPE_LEFT_TEXT){
                    return R.layout.layout_chat_left_text;
                }else if(type == TYPE_RIGHT_TEXT){
                    return R.layout.layout_chat_right_text;
                }else if(type == TYPE_LEFT_IMAGE){
                    return R.layout.layout_chat_left_img;
                }else if(type == TYPE_RIGHT_IMAGE){
                    return R.layout.layout_chat_right_img;
                }else if(type == TYPE_LEFT_LOCATION){
                    return R.layout.layout_chat_left_location;
                }else if(type == TYPE_RIGHT_LOCATION){
                    return R.layout.layout_chat_right_location;
                }
                return 0;
            }
        });
        mChatView.setAdapter(modelCommonAdapter);

        LoadMe();
        queryMessage();
    }

    private void queryMessage() {
        CloudManager.getInstance().gethistoryMessages(youruserid, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if(CommonUtils.isEmpty(messages)){
                   parstingMessage(messages);
                }else {
                    queryRemoteMessage();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.e("errorCode"+errorCode);
            }
        });
    }


    private void queryRemoteMessage() {
        CloudManager.getInstance().getRemotehistoryMessages(youruserid, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if(CommonUtils.isEmpty(messages)){
                    parstingMessage(messages);
                }else {
                    queryRemoteMessage();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.e("errorCode"+errorCode);
            }
        });
    }

    private void parstingMessage(List<Message> messages) {
        Collections.reverse(messages);
        for (int i = 0; i <messages.size() ; i++) {
            Message message = messages.get(i);
            String name = message.getObjectName();
            if(name.equals(CloudManager.MSG_TEXT_NAME)){
                TextMessage textMessage = (TextMessage) message.getContent();
                String msg = (String) textMessage.getContent();
                LogUtils.i("msg:"+msg);
                TextBean textBean = new Gson().fromJson(msg, TextBean.class);

                if(textBean.getType().equals(CloudManager.TYPE_TEXT)){
                    if(message.getSenderUserId().equals(youruserid)){
                        addText(1,textBean.getMsg());
                    }else {
                        addText(0,textBean.getMsg());
                    }
                }
            }else if(name.equals(CloudManager.MSG_IMAGE_NAME)){
                ImageMessage content = (ImageMessage) message.getContent();
                String url = content.getRemoteUri().toString();
                if(!TextUtils.isEmpty(url)){
                    LogUtils.i("url"+url);
                    if(message.getSenderUserId().equals(youruserid)){
                        addImage(1,url);
                    }else {
                        addImage(0,url);
                    }
                }


            }else if(name.equals(CloudManager.MSG_LOCATION_NAME)){

            }
        }
    }



    private void LoadMe() {
        Intent intent = getIntent();

        youruserid = intent.getStringExtra(Constants.INTENT_USER_ID);
        yourusername = intent.getStringExtra(Constants.INTENT_USER_NAME);
        youruserphoto = intent.getStringExtra(Constants.INTENT_USER_PHOTO);

        myuserphoto = BmobManager.getInstance().getUser().getPhoto();

        if(!TextUtils.isEmpty(yourusername)){
            getSupportActionBar().setTitle(yourusername);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_msg:
                String trim = etInputMsg.getText().toString().trim();
                if(TextUtils.isEmpty(trim)){
                    return;
                }
                CloudManager.getInstance().sendTextMessage(trim,CloudManager.TYPE_TEXT,youruserid);
                addText(0,trim);
                etInputMsg.setText("");
                break;
            case R.id.ll_voice:
                break;
            case R.id.ll_camera:
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.ll_pic:
                FileHelper.getInstance().toTu(this);
                break;
            case R.id.ll_location:
                break;
        }
    }


    private void baseAddItemMsg(ChatModel chatModel){
        mList.add(chatModel);
        modelCommonAdapter.notifyDataSetChanged();
        mChatView.scrollToPosition(mList.size()-1);
    }

    private void addText(int index,String txt){

        ChatModel chatModel = new ChatModel();

        if(index == 1){
            chatModel.setType(TYPE_LEFT_TEXT);
        }else {
            chatModel.setType(TYPE_RIGHT_TEXT);
        }
        chatModel.setText(txt);
        baseAddItemMsg(chatModel);
    }

    private void addImage(int index,String url){
        ChatModel chatModel = new ChatModel();

        if(index == 1){
            chatModel.setType(TYPE_LEFT_IMAGE);
        }else {
            chatModel.setType(TYPE_RIGHT_IMAGE);
        }
        chatModel.setImgurl(url);
        baseAddItemMsg(chatModel);
    }

    private void addImage(int index,File file){
        ChatModel chatModel = new ChatModel();

        if(index == 1){
            chatModel.setType(TYPE_LEFT_IMAGE);
        }else {
            chatModel.setType(TYPE_RIGHT_IMAGE);
        }
        chatModel.setFile(file);
        baseAddItemMsg(chatModel);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtils.i("requestcode："+requestCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == FileHelper.CAMEAR_REQUEST_CODE){
                upload  = FileHelper.getInstance().getTempfile();
            }else if(requestCode == FileHelper.ALBUM_REQUEST_CODE){
                Uri uri = data.getData();
                if(uri != null){
                    String s = FileHelper.getInstance().getrealUri(this, uri);
                    if(!TextUtils.isEmpty(s)){
                        upload = new File(s);
                    }
                }
            }
        }

        if(upload != null){
            CloudManager.getInstance().sendImageMessage(youruserid,upload);
            addImage(0,upload);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (!event.getUserId().equals(youruserid)) {
            return;
        }
        switch (event.getType()) {
            case EventManager.FLAG_SEND_TEXT:
                addText(1, event.getText());
                break;
            case EventManager.FLAG_SEND_IMAGE:
                addImage(1, event.getImgurl());
                break;
            case EventManager.FLAG_SEND_LOCATION:
                break;
        }
    }


}