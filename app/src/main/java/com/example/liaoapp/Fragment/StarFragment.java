package com.example.liaoapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.framework.adapter.CloudTagAdapter;
import com.example.framework.base.BaseFragment;
import com.example.liaoapp.Activity.AddFriendActivity;
import com.example.liaoapp.R;
import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> mlist = new ArrayList<>();
    private CloudTagAdapter cloudTagAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_star, null);
        initview();
        return view;
    }

    private void initview() {
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

        for (int i = 0; i < 100 ; i++) {
            mlist.add("Star" + i);
        }

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
                Toast.makeText(getActivity(), "position:"+parent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_camera:
                break;
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            case R.id.ll_random:
                break;
            case R.id.ll_soul:
                break;
            case R.id.ll_fate:
                break;
            case R.id.ll_love:
                break;
        }
    }
}
