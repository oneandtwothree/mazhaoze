package com.example.liaoapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.framework.base.BaseBackActivity;
import com.example.framework.base.BaseUiActivity;
import com.example.framework.helper.FileHelper;
import com.example.liaoapp.R;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class QrCodeActivity extends BaseUiActivity implements View.OnClickListener {
    private FrameLayout flMyContainer;
    private ImageView ivBack;
    private TextView ivToAblum;
    private ImageView ivFlashlight;

    private static final int REQUEST_IMAGE = 1234;

    private boolean isOpenLight = false;

    private CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);


        initQrcode();
        initView();
    }

    private void initQrcode() {
        CaptureFragment captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_qrcode);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    private void initView() {
        flMyContainer = findViewById(R.id.fl_my_container);
        ivBack = findViewById(R.id.iv_back);
        ivToAblum = findViewById(R.id.iv_to_ablum);
        ivFlashlight = findViewById(R.id.iv_flashlight);


        ivBack.setOnClickListener(this);
        ivToAblum.setOnClickListener(this);
        ivFlashlight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_to_ablum:
                openAblum();
                break;
            case R.id.iv_flashlight:
                try {
                    isOpenLight = !isOpenLight;
                    CodeUtils.isLightEnable(isOpenLight);
                    ivFlashlight.setImageResource(isOpenLight ? R.drawable.img_flashlight_p : R.drawable.img_flashlight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void openAblum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                String path = FileHelper.getInstance()
                        .getrealUri(QrCodeActivity.this, uri);
                try {
                    CodeUtils.analyzeBitmap(path, new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            analyzeCallback.onAnalyzeSuccess(mBitmap, result);
                        }

                        @Override
                        public void onAnalyzeFailed()  {
                            analyzeCallback.onAnalyzeFailed();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
