package com.example.liaoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.framework.base.BaseUiActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.TimeUtils;

import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseUiActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IMUser user = BmobManager.getInstance().getUser();
        Toast.makeText(this, ""+user.getMobilePhoneNumber(), Toast.LENGTH_SHORT).show();
    }
}
