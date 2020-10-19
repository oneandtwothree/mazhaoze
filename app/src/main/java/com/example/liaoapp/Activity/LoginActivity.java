package com.example.liaoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.framework.base.BaseUiActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.entity.Constants;
import com.example.framework.utils.SpUtils;
import com.example.framework.view.DiaLogView;
import com.example.framework.view.LodingView;
import com.example.framework.view.TouchPictureV;
import com.example.liaoapp.MainActivity;
import com.example.liaoapp.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

public class LoginActivity extends BaseUiActivity implements View.OnClickListener {
    private EditText etPhone;
    private EditText etCode;
    private Button btnSendCode;
    private Button btnLogin;
    private TextView tvTestLogin;
    private TextView tvUserAgreement;


    private static final int H_TIME = 1001;
    private static int time = 60;

    private TouchPictureV mPictureV;

    private LodingView lodingView;

    private   DiaLogView initview;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case H_TIME:
                    time--;
                    btnSendCode.setText(time + "s");
                    if(time > 0){
                        handler.sendEmptyMessageDelayed(H_TIME,1000);
                    }else {
                        btnSendCode.setEnabled(true);
                        btnSendCode.setText(getString(R.string.text_login_send));
                        time = 60;
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        lodingView = new LodingView(this);

        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        btnSendCode = findViewById(R.id.btn_send_code);
        btnLogin = findViewById(R.id.btn_login);
        tvTestLogin = findViewById(R.id.tv_test_login);
        tvUserAgreement = findViewById(R.id.tv_user_agreement);
        initDialogView();

        btnLogin.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);


        String getstring = SpUtils.getInstance().getstring(Constants.SP_PHONE, "");
        if (TextUtils.isEmpty(getstring)) {
            etPhone.setText(getstring);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_code:
                DiaLogManager.getInstance().show(initview);
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        final String phone = etPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, R.string.text_login_phone_null, Toast.LENGTH_SHORT).show();
        }
        String code = etCode.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, R.string.text_login_phone_null, Toast.LENGTH_SHORT).show();
        }

        lodingView.show("正在登录......");

        BmobManager.getInstance().signorLoginByMyPhone(phone, code, new LogInListener<IMUser>() {
            @Override
            public void done(IMUser imUser, BmobException e) {
                if(e == null){
                    lodingView.hide();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    SpUtils.getInstance().putstring(Constants.SP_PHONE,phone);
                    finish();
                }else {
                    if (e.getErrorCode() == 207) {
                        Toast.makeText(LoginActivity.this, getString(R.string.text_login_code_error), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "ERROR:" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendSMS() {
        String phone = etPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, R.string.text_login_phone_null, Toast.LENGTH_SHORT).show();
            return;
        }
        BmobManager.getInstance().requestSMScode(phone, new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e == null){
                    btnSendCode.setEnabled(false);
                    handler.sendEmptyMessage(H_TIME);
                    Toast.makeText(LoginActivity.this, "短信验证码发送成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "短信验证码发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initDialogView(){

        initview = DiaLogManager.getInstance().initview(this, R.layout.dialog_code_view);
        mPictureV = initview.findViewById(R.id.mPictureV);


        mPictureV.setViewRestultListener(new TouchPictureV.OnviewResultListener() {
            @Override
            public void onResult() {
                DiaLogManager.getInstance().hide(initview);
                Toast.makeText(LoginActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                sendSMS();
            }
        });

    }

}
