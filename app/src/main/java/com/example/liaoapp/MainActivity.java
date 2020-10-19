package com.example.liaoapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.framework.base.BaseUiActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.entity.Constants;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.example.framework.view.DiaLogView;
import com.example.liaoapp.Activity.FirstUploadActivity;
import com.example.liaoapp.Fragment.ChatFragment;
import com.example.liaoapp.Fragment.MeFragment;
import com.example.liaoapp.Fragment.SquareFragment;
import com.example.liaoapp.Fragment.StarFragment;
import com.example.liaoapp.service.CloudService;

import java.util.List;

public class MainActivity extends BaseUiActivity implements View.OnClickListener {

    private FrameLayout mMainLayout;

    private LinearLayout llStar;
    private ImageView ivStar;
    private TextView tvStar;
    private StarFragment starFragment;
    private FragmentTransaction starTransaction = null;


    private LinearLayout llSquare;
    private ImageView ivSquare;
    private TextView tvSquare;
    private SquareFragment squareFragment;
    private FragmentTransaction squareTransaction = null;

    private LinearLayout llChat;
    private ImageView ivChat;
    private TextView tvChat;
    private ChatFragment chatFragment;
    private FragmentTransaction chatTransaction = null;

    private LinearLayout llMe;
    private ImageView ivMe;
    private TextView tvMe;
    private MeFragment meFragment;
    private FragmentTransaction meTransaction = null;

    private ImageView ivGoUpload;
    public static final int UPLOAD_REQUEST_CODE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initview();

    }

    private void initview() {

        request( new OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                LogUtils.i(""+noPermissions);
            }
        });

        mMainLayout = findViewById(R.id.mMainLayout);
        llStar = findViewById(R.id.ll_star);
        ivStar = findViewById(R.id.iv_star);
        tvStar = findViewById(R.id.tv_star);
        llSquare = findViewById(R.id.ll_square);
        ivSquare = findViewById(R.id.iv_square);
        tvSquare = findViewById(R.id.tv_square);
        llChat = findViewById(R.id.ll_chat);
        ivChat = findViewById(R.id.iv_chat);
        tvChat = findViewById(R.id.tv_chat);
        llMe = findViewById(R.id.ll_me);
        ivMe = findViewById(R.id.iv_me);
        tvMe = findViewById(R.id.tv_me);


        llStar.setOnClickListener(this);
        llSquare.setOnClickListener(this);
        llChat.setOnClickListener(this);
        llMe.setOnClickListener(this);

        initfragment();
        CheckMainTab(0);

        checkToken();
    }

    private void checkToken() {
        String token = SpUtils.getInstance().getstring(Constants.SP_TOKEN, "");

        if(!TextUtils.isEmpty(token)){
            startService(new Intent(this, CloudService.class));
        }else {
            String tokenPhoto = BmobManager.getInstance().getUser().getTokenPhoto();
            String tokenNickName = BmobManager.getInstance().getUser().getTokenNickName();
            if(!TextUtils.isEmpty(tokenPhoto) && !TextUtils.isEmpty(tokenNickName)){
                createToken();
            }else {
                createUploadDialog();
            }
        }
    }

    private void createUploadDialog() {
        final DiaLogView mUploadView = DiaLogManager.getInstance().initview(this, R.layout.dialog_first_upload);
        mUploadView.setCancelable(false);
        ivGoUpload = mUploadView.findViewById(R.id.iv_go_upload);
        ivGoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiaLogManager.getInstance().hide(mUploadView);
                FirstUploadActivity.startAct(MainActivity.this,UPLOAD_REQUEST_CODE);

            }
        });

        DiaLogManager.getInstance().show(mUploadView);

    }

    private void createToken() {

    }

    private void initfragment() {
        if(starFragment == null){
            starFragment = new StarFragment();
            starTransaction = getSupportFragmentManager().beginTransaction();
            starTransaction.add(R.id.mMainLayout,starFragment);
            starTransaction.commit();
        }
        if(squareFragment == null){
            squareFragment = new SquareFragment();
            squareTransaction = getSupportFragmentManager().beginTransaction();
            squareTransaction.add(R.id.mMainLayout,squareFragment);
            squareTransaction.commit();
        }
        if(chatFragment == null){
            chatFragment = new ChatFragment();
            chatTransaction = getSupportFragmentManager().beginTransaction();
            chatTransaction.add(R.id.mMainLayout,chatFragment);
            chatTransaction.commit();
        }
        if(meFragment == null){
            meFragment = new MeFragment();
            meTransaction = getSupportFragmentManager().beginTransaction();
            meTransaction.add(R.id.mMainLayout,meFragment);
            meTransaction.commit();
        }

    }


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if(starFragment != null && fragment instanceof StarFragment){
            starFragment = (StarFragment)fragment;
        }
        if(squareFragment != null && fragment instanceof SquareFragment){
            squareFragment = (SquareFragment)fragment;
        }
        if(chatFragment != null && fragment instanceof ChatFragment){
            chatFragment = (ChatFragment)fragment;
        }
        if(meFragment != null && fragment instanceof MeFragment){
            meFragment = (MeFragment)fragment;
        }

    }

    private void showfragment(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideAll(transaction);
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    private void hideAll(FragmentTransaction fragmentTransaction){
        if(starFragment != null){
            fragmentTransaction.hide(starFragment);
        }
        if(squareFragment != null){
            fragmentTransaction.hide(squareFragment);
        }
        if(chatFragment != null){
            fragmentTransaction.hide(chatFragment);
        }
        if(meFragment != null){
            fragmentTransaction.hide(meFragment);
        }

    }


    private void CheckMainTab(int i){
        switch (i){
            case 0:
                showfragment(starFragment);
                ivStar.setImageResource(R.drawable.img_star_p);
                ivSquare.setImageResource(R.drawable.img_square);
                ivChat.setImageResource(R.drawable.img_chat);
                ivMe.setImageResource(R.drawable.img_me);

                tvStar.setTextColor(getResources().getColor(R.color.colorAccent));
                tvSquare.setTextColor(Color.BLACK);
                tvChat.setTextColor(Color.BLACK);
                tvMe.setTextColor(Color.BLACK);

                break;
            case 1:
                showfragment(squareFragment);
                ivStar.setImageResource(R.drawable.img_star);
                ivSquare.setImageResource(R.drawable.img_square_p);
                ivChat.setImageResource(R.drawable.img_chat);
                ivMe.setImageResource(R.drawable.img_me);

                tvStar.setTextColor(Color.BLACK);
                tvSquare.setTextColor(getResources().getColor(R.color.colorAccent));
                tvChat.setTextColor(Color.BLACK);
                tvMe.setTextColor(Color.BLACK);
                break;
            case 2:
                showfragment(chatFragment);
                ivStar.setImageResource(R.drawable.img_star);
                ivSquare.setImageResource(R.drawable.img_square);
                ivChat.setImageResource(R.drawable.img_chat_p);
                ivMe.setImageResource(R.drawable.img_me);

                tvStar.setTextColor(Color.BLACK);
                tvSquare.setTextColor(Color.BLACK);
                tvChat.setTextColor(getResources().getColor(R.color.colorAccent));
                tvMe.setTextColor(Color.BLACK);
                break;
            case 3:
                showfragment(meFragment);
                ivStar.setImageResource(R.drawable.img_star);
                ivSquare.setImageResource(R.drawable.img_square);
                ivChat.setImageResource(R.drawable.img_chat);
                ivMe.setImageResource(R.drawable.img_me_p);

                tvStar.setTextColor(Color.BLACK);
                tvSquare.setTextColor(Color.BLACK);
                tvChat.setTextColor(Color.BLACK);
                tvMe.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_star:
                CheckMainTab(0);
                break;
            case R.id.ll_square:
                CheckMainTab(1);
                break;
            case R.id.ll_chat:
                CheckMainTab(2);
                break;
            case R.id.ll_me:
                CheckMainTab(3);
                break;
        }
    }
}
