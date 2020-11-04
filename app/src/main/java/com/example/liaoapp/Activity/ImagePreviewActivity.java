package com.example.liaoapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.framework.base.BaseUiActivity;
import com.example.framework.entity.Constants;
import com.example.framework.helper.GlideHelper;
import com.example.liaoapp.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class ImagePreviewActivity extends BaseUiActivity implements View.OnClickListener {
    private PhotoView photoView;
    private ImageView ivBack;
    private TextView tvDownload;

    public static void startActivity(Context context,String url,boolean isurl){
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(Constants.INTENT_IMAGE_URL,url);
        intent.putExtra(Constants.INTENT_IMAGE_TYPE,isurl);
        context.startActivity(intent);
    }
    public static void startActivity(Context mContext, boolean isUrl, String url) {
        Intent intent = new Intent(mContext, ImagePreviewActivity.class);
        intent.putExtra(Constants.INTENT_IMAGE_TYPE, isUrl);
        intent.putExtra(Constants.INTENT_IMAGE_URL, url);
        mContext.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        photoView = findViewById(R.id.photo_view);
        ivBack = findViewById(R.id.iv_back);
        tvDownload = findViewById(R.id.tv_download);

        ivBack.setOnClickListener(this);

        Intent intent = getIntent();
        boolean booleanExtra = intent.getBooleanExtra(Constants.INTENT_IMAGE_TYPE, false);
        String url = intent.getStringExtra(Constants.INTENT_IMAGE_URL);

        if(booleanExtra){
            GlideHelper.setimg(this,url,photoView);
        }else {
            GlideHelper.setimg(this,new File(url),photoView);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back){
            finish();
        }
    }
}
