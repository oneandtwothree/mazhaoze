package com.example.liaoapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.framework.base.BaseFragment;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.utils.GlideHelper;
import com.example.liaoapp.Activity.NewFriendActivity;
import com.example.liaoapp.Activity.PrivateSetActivity;
import com.example.liaoapp.Activity.ShareImgActivity;
import com.example.liaoapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends BaseFragment implements View.OnClickListener {
    private View view;

    private CircleImageView ivMePhoto;
    private TextView tvNickname;
    private TextView tvServerStatus;
    private LinearLayout llMeInfo;
    private LinearLayout llNewFriend;
    private LinearLayout llPrivateSet;
    private LinearLayout llShare;
    private LinearLayout llSetting;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, null);
         initView();
        return view;

    }

    private void initView() {
        ivMePhoto = view.findViewById(R.id.iv_me_photo);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvServerStatus = view.findViewById(R.id.tv_server_status);
        llMeInfo = view.findViewById(R.id.ll_me_info);
        llNewFriend = view.findViewById(R.id.ll_new_friend);
        llPrivateSet = view.findViewById(R.id.ll_private_set);
        llShare = view.findViewById(R.id.ll_share);
        llSetting = view.findViewById(R.id.ll_setting);


        llMeInfo.setOnClickListener(this);
        llNewFriend.setOnClickListener(this);
        llPrivateSet.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llSetting.setOnClickListener(this);

        loadMeInfo();



    }

    private void loadMeInfo() {
        IMUser imUser = BmobManager.getInstance().getUser();
        GlideHelper.setimg(getActivity(), imUser.getPhoto(), ivMePhoto);
        tvNickname.setText(imUser.getNickName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_me_info:
                break;
            case R.id.ll_new_friend:
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
                break;
            case R.id.ll_private_set:
                startActivity(new Intent(getActivity(), PrivateSetActivity.class));
                break;
            case R.id.ll_share:
                startActivity(new Intent(getActivity(), ShareImgActivity.class));
                break;
            case R.id.ll_setting:
                break;
        }
    }
}
