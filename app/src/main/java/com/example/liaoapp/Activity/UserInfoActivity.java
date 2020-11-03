package com.example.liaoapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.adapter.CommonAdapter;
import com.example.framework.adapter.CommonViewHolder;
import com.example.framework.base.BaseUiActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.bmob.Friend;
import com.example.framework.bmob.IMUser;
import com.example.framework.cloud.CloudManager;
import com.example.framework.entity.Constants;
import com.example.framework.utils.CommonUtils;
import com.example.framework.helper.GlideHelper;
import com.example.framework.utils.LogUtils;
import com.example.framework.view.DiaLogView;
import com.example.liaoapp.R;
import com.example.liaoapp.model.UserInfoModel;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseUiActivity implements View.OnClickListener {


    private ImageView ivback;

    private CommonAdapter<UserInfoModel> commonAdapter;
    private List<UserInfoModel> mUserlist = new ArrayList<>();

    private CircleImageView iv_user_photo;
    private TextView tv_nickname;
    private TextView tv_desc;

    private RecyclerView mUserInfoView;

    private Button btn_add_friend;
    private Button btn_chat;
    private Button btn_audio_chat;
    private Button btn_video_chat;

    private LinearLayout ll_is_friend;

    private  String userid = "";
    private  DiaLogView initview;

    private int[] mColor = {0x881E90FF, 0x8800FF7F, 0x88FFD700, 0x88FF6347, 0x88F08080, 0x8840E0D0};

    private EditText etMsg;
    private TextView tvCancel;
    private TextView tvAddFriend;


    private IMUser imUser;

    public static void startActivity(Context mContext, String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra(Constants.INTENT_USER_ID, userId);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();
    }

    private void initView() {

        initAddFriendDialog();

        //获取用户ID
        userid = getIntent().getStringExtra(Constants.INTENT_USER_ID);

        ivback = (ImageView) findViewById(R.id.iv_back);
        iv_user_photo = (CircleImageView) findViewById(R.id.iv_user_photo);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        mUserInfoView = (RecyclerView) findViewById(R.id.mUserInfoView);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_audio_chat = (Button) findViewById(R.id.btn_audio_chat);
        btn_video_chat = (Button) findViewById(R.id.btn_video_chat);
        ll_is_friend = (LinearLayout) findViewById(R.id.ll_is_friend);

        ivback.setOnClickListener(this);
        btn_add_friend.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_audio_chat.setOnClickListener(this);
        btn_video_chat.setOnClickListener(this);
        iv_user_photo.setOnClickListener(this);

        commonAdapter = new CommonAdapter<>(mUserlist, new CommonAdapter.OnBindDataListener<UserInfoModel>() {
            @Override
            public void onBindViewHolder(UserInfoModel model, CommonViewHolder commonViewHolder, int type, int position) {
              //  commonViewHolder.setBackGroundColor(R.id.ll_bg,model.getBgColor());
                commonViewHolder.getView(R.id.ll_bg).setBackgroundColor(model.getBgColor());
                commonViewHolder.setText(R.id.tv_type, model.getTitle());
                commonViewHolder.setText(R.id.tv_content, model.getContent());
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_user_info_item;
            }
        });
        mUserInfoView.setLayoutManager(new GridLayoutManager(this,3));
        mUserInfoView.setAdapter(commonAdapter);

        queryUserInfo();
    }

    private void initAddFriendDialog() {
        initview = DiaLogManager.getInstance().initview(this, R.layout.dialog_send_friend);

        etMsg = initview.findViewById(R.id.et_msg);
        tvCancel = initview.findViewById(R.id.tv_cancel);
        tvAddFriend = initview.findViewById(R.id.tv_add_friend);

        etMsg.setText(getString(R.string.text_me_info_tips) + BmobManager.getInstance().getUser().getNickName());

        tvCancel.setOnClickListener(this);
        tvAddFriend.setOnClickListener(this);
    }

    private void queryUserInfo() {
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        LogUtils.i(userid);
        BmobManager.getInstance().queryidFriend(userid, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        imUser = list.get(0);
                        updataUserInfo(imUser);
                    }
                }
            }
        });
        BmobManager.getInstance().queryallFriend(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        for (int i = 0; i <list.size() ; i++) {
                            Friend friend = list.get(i);
                            if(friend.getFrienduser().getObjectId().equals(userid)){
                                    btn_add_friend.setVisibility(View.GONE);
                                    ll_is_friend.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });

    }

    private void updataUserInfo(IMUser imUser) {
        GlideHelper.setimg(UserInfoActivity.this,imUser.getTokenPhoto(),iv_user_photo);
        tv_nickname.setText(imUser.getNickName());
        tv_desc.setText(imUser.getDesc());

        addUserInfoModel(mColor[0], getString(R.string.text_me_info_sex), imUser.isSex() ? getString(R.string.text_me_info_boy) : getString(R.string.text_me_info_girl));
        addUserInfoModel(mColor[1], getString(R.string.text_me_info_age), imUser.getAge() + getString(R.string.text_search_age));
        addUserInfoModel(mColor[2], getString(R.string.text_me_info_birthday), imUser.getBirthday());
        addUserInfoModel(mColor[3], getString(R.string.text_me_info_constellation), imUser.getConstellation());
        addUserInfoModel(mColor[4], getString(R.string.text_me_info_hobby), imUser.getHobby());
        addUserInfoModel(mColor[5], getString(R.string.text_me_info_status), imUser.getStatus());
        //刷新数据
        commonAdapter.notifyDataSetChanged();
    }

    private void addUserInfoModel(int color, String title, String content) {
        UserInfoModel model = new UserInfoModel();
        model.setBgColor(color);
        model.setTitle(title);
        model.setContent(content);
        mUserlist.add(model);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                DiaLogManager.getInstance().hide(initview);
                break;
            case R.id.tv_add_friend:
                String msg = etMsg.getText().toString().trim();

                if(TextUtils.isEmpty(msg)){
                    msg = " 你好，我是" + BmobManager.getInstance().getUser().getNickName();
                    return;
                }

                CloudManager.getInstance().sendaddfriendTextMessage(msg,CloudManager.TYPE_ADD_FRIEND,userid);
                Toast.makeText(this, "消息发送成功", Toast.LENGTH_SHORT).show();
                DiaLogManager.getInstance().hide(initview);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_user_photo:
                break;
            case R.id.btn_add_friend:
                DiaLogManager.getInstance().show(initview);
                break;
            case R.id.btn_chat:
                ChatActivity.startActivity(UserInfoActivity.this,userid,imUser.getNickName(),imUser.getPhoto());
                break;
            case R.id.btn_audio_chat:
                CloudManager.getInstance().startAudioCall(this,userid);
                break;
            case R.id.btn_video_chat:
                CloudManager.getInstance().startVideoCall(this,userid);
                break;
        }
    }
}
