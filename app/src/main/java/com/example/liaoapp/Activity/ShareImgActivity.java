package com.example.liaoapp.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.helper.FileHelper;
import com.example.framework.utils.GlideHelper;
import com.example.framework.view.LodingView;
import com.example.liaoapp.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ShareImgActivity extends BaseBackActivity implements View.OnClickListener {
    private LinearLayout llContent;
    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvSex;
    private TextView tvAge;
    private TextView tvPhone;
    private TextView tvDesc;
    private ImageView ivQrcode;
    private LinearLayout llDownload;

    private LodingView mLodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_img);

        initview();
    }

    private void initview() {

        mLodingView = new LodingView(this);
        mLodingView.setLodingText(getString(R.string.text_shar_save_ing));


        llContent = findViewById(R.id.ll_content);
        ivPhoto = findViewById(R.id.iv_photo);
        tvName = findViewById(R.id.tv_name);
        tvSex = findViewById(R.id.tv_sex);
        tvAge = findViewById(R.id.tv_age);
        tvPhone = findViewById(R.id.tv_phone);
        tvDesc = findViewById(R.id.tv_desc);
        ivQrcode = findViewById(R.id.iv_qrcode);
        llDownload = findViewById(R.id.ll_download);



        llDownload.setOnClickListener(this);

        loadInfo();
    }

    private void loadInfo() {
        IMUser imUser = BmobManager.getInstance().getUser();

        GlideHelper.setimg(this, imUser.getPhoto(), ivPhoto);
        tvName.setText(imUser.getNickName());
        tvSex.setText(imUser.isSex() ? R.string.text_me_info_boy : R.string.text_me_info_girl);
        tvAge.setText(imUser.getAge()+ getString(R.string.text_search_age));
        tvPhone.setText(imUser.getMobilePhoneNumber());
        tvDesc.setText(imUser.getDesc());

        createQRCode(imUser.getObjectId());
    }

    private void createQRCode(final String userId) {

        ivQrcode.post(new Runnable() {
            @Override
            public void run() {
                String textContent = "Meet#" + userId;
                Bitmap mBitmap = CodeUtils.createImage(textContent,
                        ivQrcode.getWidth(), ivQrcode.getHeight(), null);
                ivQrcode.setImageBitmap(mBitmap);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_download:
                mLodingView.show();
                llContent.setDrawingCacheEnabled(true);

                llContent.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                llContent.layout(0, 0, llContent.getMeasuredWidth(),
                        llContent.getMeasuredHeight());

                Bitmap mBitmap = llContent.getDrawingCache();

                if (mBitmap != null) {
                    FileHelper.getInstance().saveBitmapToAlbum(this, mBitmap);
                    mLodingView.hide();
                }
                break;
        }
    }
}
