package com.example.liaoapp.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.framework.base.BaseActivity;
import com.example.framework.view.TouchPictureV;
import com.example.liaoapp.R;

public class TestActivity extends BaseActivity {
    private TouchPictureV TouchV;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TouchV = findViewById(R.id.TouchV);


        TouchV.setViewRestultListener(new TouchPictureV.OnviewResultListener() {
            @Override
            public void onResult() {
                Toast.makeText(TestActivity.this, "验证通过", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
