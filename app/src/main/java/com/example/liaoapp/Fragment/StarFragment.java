package com.example.liaoapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.framework.base.BaseFragment;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.bmob.Friend;
import com.example.framework.bmob.IMUser;
import com.example.framework.cloud.CloudManager;
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.helper.PairFriendHelper;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.framework.view.DiaLogView;
import com.example.framework.view.LodingView;
import com.example.liaoapp.Activity.AddFriendActivity;
import com.example.liaoapp.Activity.QrCodeActivity;
import com.example.liaoapp.Activity.UserInfoActivity;
import com.example.liaoapp.R;
import com.example.liaoapp.adapter.CloudTagAdapter;
import com.example.liaoapp.model.StarModel;
import com.moxun.tagcloudlib.view.TagCloudView;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class StarFragment extends BaseFragment implements View.OnClickListener{
    private View view;

    private TextView tvStarTitle;
    private ImageView ivCamera;
    private ImageView ivAdd;
    private TextView tvConnectStatus;
    private TagCloudView mCloudView;
    private LinearLayout llRandom;
    private TextView tvRandom;
    private LinearLayout llSoul;
    private TextView tvSoul;
    private LinearLayout llFate;
    private TextView tvFate;
    private LinearLayout llLove;
    private TextView tvLove;

    private List<StarModel> mlist = new ArrayList<>();
    private CloudTagAdapter cloudTagAdapter;

    private static final int REQUEST_CODE = 1235;
    private LodingView mlodingView;

    private DiaLogView mNullDialogView;
    private TextView tv_null_text;
    private TextView tv_null_cancel;

    private TextView tv_connect_status;
    private List<IMUser> mAllUserList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_star, null);
        initview();
        return view;
    }

    private void initview() {
        mlodingView = new LodingView(getActivity());

        tvStarTitle = view.findViewById(R.id.tv_star_title);
        ivCamera = view.findViewById(R.id.iv_camera);
        ivAdd = view.findViewById(R.id.iv_add);
      //  tvConnectStatus = view.findViewById(R.id.tv_connect_status);
        mCloudView = view.findViewById(R.id.mCloudView);
        llRandom = view.findViewById(R.id.ll_random);
        tvRandom = view.findViewById(R.id.tv_random);
        llSoul = view.findViewById(R.id.ll_soul);
        tvSoul = view.findViewById(R.id.tv_soul);
        llFate = view.findViewById(R.id.ll_fate);
        tvFate = view.findViewById(R.id.tv_fate);
        llLove = view.findViewById(R.id.ll_love);
        tvLove = view.findViewById(R.id.tv_love);


        ivCamera.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        llRandom.setOnClickListener(this);
        llSoul.setOnClickListener(this);
        llFate.setOnClickListener(this);
        llLove.setOnClickListener(this);


        cloudTagAdapter = new CloudTagAdapter(getActivity(),mlist);
        mCloudView.setAdapter(cloudTagAdapter);

        mCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                UserInfoActivity.startActivity(getActivity(),mlist.get(position).getUserId());
            }
        });

        PairFriendHelper.getInstance().setOnPairResultListener(new PairFriendHelper.OnPairResultListener() {

            @Override
            public void OnPairListener(String userId) {
                startUserInfo(userId);
            }

            @Override
            public void OnPairFailListener() {
                mlodingView.hide();
                Toast.makeText(getActivity(), getString(R.string.text_pair_null), Toast.LENGTH_SHORT).show();
            }
        });


        loadUser();
    }


    private void startUserInfo(String userId) {
        mlodingView.hide();
        UserInfoActivity.startActivity(getActivity(), userId);
    }

    private void loadUser() {
        BmobManager.getInstance().queryAll(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if(e == null){
                    if (CommonUtils.isEmpty(list)) {

                        if (mAllUserList.size() > 0) {
                            mAllUserList.clear();
                        }

                        if (mlist.size() > 0) {
                            mlist.clear();
                        }

                        mAllUserList = list;

                        //这里是所有的用户 只适合我们现在的小批量
                        int index = 50;
                        if (list.size() <= 50) {
                            index = list.size();
                        }
                        //直接填充
                        for (int i = 0; i < index; i++) {
                            IMUser imUser = list.get(i);
                            saveStarUser(imUser.getObjectId(),
                                    imUser.getNickName(),
                                    imUser.getPhoto());
                        }
                        LogUtils.i("done...");
                        //当请求数据已经加载出来的时候判断是否连接服务器
                        if(CloudManager.getInstance().isConnect()){
                            //已经连接，并且星球加载，则隐藏
                            tv_connect_status.setVisibility(View.GONE);
                        }
                        cloudTagAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void  saveStarUser(String userid,String nickName,String photoUrl){
        StarModel starModel = new StarModel();
        starModel.setNickName(nickName);
        starModel.setPhotoUrl(photoUrl);
        starModel.setUserId(userid);

        mlist.add(starModel);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_camera:
                Intent intent = new Intent(getActivity(), QrCodeActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            case R.id.ll_random:
                pairUs(0);
                break;
            case R.id.ll_soul:

                if(TextUtils.isEmpty(BmobManager.getInstance().getUser().getConstellation())){
                    tv_null_text.setText(getString(R.string.text_star_par_tips_1));
                    DiaLogManager.getInstance().show(mNullDialogView);
                    return;
                }

                if(BmobManager.getInstance().getUser().getAge() == 0){
                    tv_null_text.setText(getString(R.string.text_star_par_tips_2));
                    DiaLogManager.getInstance().show(mNullDialogView);
                    return;
                }

                if(TextUtils.isEmpty(BmobManager.getInstance().getUser().getHobby())){
                    tv_null_text.setText(getString(R.string.text_star_par_tips_3));
                    DiaLogManager.getInstance().show(mNullDialogView);
                    return;
                }

                if(TextUtils.isEmpty(BmobManager.getInstance().getUser().getStatus())){
                    tv_null_text.setText(getString(R.string.text_star_par_tips_4));
                    DiaLogManager.getInstance().show(mNullDialogView);
                    return;
                }


                pairUs(1);
                break;
            case R.id.ll_fate:
                pairUs(2);
                break;
            case R.id.ll_love:
                pairUs(3);
                break;
        }
    }

    private void pairUs(int index){
        switch (index){
            case 0:
                mlodingView.show(getString(R.string.text_pair_random));
                break;
            case 1:
                mlodingView.show(getString(R.string.text_pair_soul));
                break;
            case 2:
                mlodingView.show(getString(R.string.text_pair_fate));
                break;
            case 3:
                mlodingView.show(getString(R.string.text_pair_love));
                break;
        }
        if (CommonUtils.isEmpty(mAllUserList)) {
            //计算
            PairFriendHelper.getInstance().pairUser(index, mAllUserList);
        } else {
            mlodingView.hide();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    LogUtils.i("result：" + result);
                    if (!TextUtils.isEmpty(result)) {
                        //是我们自己的二维码
                        if (result.startsWith("Meet")) {
                            String[] split = result.split("#");
                            LogUtils.i("split:" + split.toString());
                            if (split != null && split.length >= 2) {
                                try {
                                    UserInfoActivity.startActivity(getActivity(), split[1]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.text_toast_error_qrcode), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.text_toast_error_qrcode), Toast.LENGTH_SHORT).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), getString(R.string.text_qrcode_fail), Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        PairFriendHelper.getInstance().disposable();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case EventManager.EVENT_SERVER_CONNECT_STATUS:
                if(event.isConnectStatus()){
                    if(CommonUtils.isEmpty(mlist)){
                        tv_connect_status.setVisibility(View.GONE);
                    }
                }else{
                    tv_connect_status.setText(getString(R.string.text_star_pserver_fail));
                }
                break;
        }
    }

}
