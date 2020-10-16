package com.example.liaoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.framework.base.BaseUiActivity;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.TimeUtils;

public class MainActivity extends BaseUiActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
