package com.example.liaoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.framework.bmob.BmobManager;
import com.example.framework.entity.Constants;
import com.example.framework.utils.SpUtils;
import com.example.liaoapp.MainActivity;
import com.example.liaoapp.R;

public class IndexActivity extends AppCompatActivity {

    private static final int SKIP_MAIN = 1000;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case SKIP_MAIN:
                    startMain();
                    break;
            }
            return false;
        }
    });

    private void startMain() {
        boolean getboolean = SpUtils.getInstance().getboolean(Constants.SP_IS_FIRST_APP, true);

        Intent intent = new Intent();

        if(getboolean){
            intent.setClass(this,GuideActivity.class);
            SpUtils.getInstance().putboolean(Constants.SP_IS_FIRST_APP,false);
        }else {
            String getstring = SpUtils.getInstance().getstring(Constants.SP_TOKEN, "");
            if(TextUtils.isEmpty(getstring)){
                if (BmobManager.getInstance().islogin()) {
                    //跳转到主页
                    intent.setClass(this, MainActivity.class);
                } else {
                    //跳转到登录页
                    intent.setClass(this, LoginActivity.class);
                }
            }else {
                intent.setClass(this, MainActivity.class);
            }
        }
        startActivity(intent);
        finish();


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        handler.sendEmptyMessageDelayed(SKIP_MAIN,2*1000);
    }
}
