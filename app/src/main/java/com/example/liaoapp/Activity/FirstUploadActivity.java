package com.example.liaoapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.framework.base.BaseActivity;
import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.helper.FileHelper;
import com.example.framework.utils.LogUtils;
import com.example.framework.view.DiaLogView;
import com.example.liaoapp.R;

import java.io.File;

public class FirstUploadActivity extends BaseBackActivity implements View.OnClickListener {
    private ImageView ivPhoto;
    private EditText etNickname;
    private Button btnUpload;

    private Button btnCamera;
    private Button btnAlbum;
    private Button btnCancel;

    private DiaLogView seltphoto;
    private File uploadfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_upload);

        initview();
    }

    private void initview() {
        ivPhoto = findViewById(R.id.iv_photo);
        etNickname = findViewById(R.id.et_nickname);
        btnUpload = findViewById(R.id.btn_upload);

        ivPhoto.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        initPhotoView();

    }


    public static void startAct(Activity activity, int requestcod){
        Intent intent = new Intent(activity, FirstUploadActivity.class);
        activity.startActivityForResult(intent,requestcod);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_photo:
                DiaLogManager.getInstance().show(seltphoto);
                break;
            case R.id.btn_upload:
                break;
            case R.id.btn_camera:
                DiaLogManager.getInstance().hide(seltphoto);
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.btn_album:
                DiaLogManager.getInstance().hide(seltphoto);
                FileHelper.getInstance().toTu(this);
                break;
            case R.id.btn_cancel:
                DiaLogManager.getInstance().hide(seltphoto);
                break;
        }
    }


    private void initPhotoView(){
        seltphoto = DiaLogManager.getInstance().initview(this, R.layout.dialog_select_photo, Gravity.BOTTOM);

        btnCamera = seltphoto.findViewById(R.id.btn_camera);
        btnAlbum = seltphoto.findViewById(R.id.btn_album);
        btnCancel = seltphoto.findViewById(R.id.btn_cancel);

        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        btnCamera.setTextColor(getResources().getColor(R.color.colorAccent));
        btnAlbum.setTextColor(getResources().getColor(R.color.colorAccent));
        btnCancel.setTextColor(getResources().getColor(R.color.colorAccent));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtils.i("requestcode："+requestCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == FileHelper.CAMEAR_REQUEST_CODE){
                uploadfile  = FileHelper.getInstance().getTempfile();
            }else if(requestCode == FileHelper.ALBUM_REQUEST_CODE){
                Uri uri = data.getData();
                if(uri != null){
                    String s = FileHelper.getInstance().getrealUri(this, uri);
                    if(!TextUtils.isEmpty(s)){
                        uploadfile = new File(s);
                    }
                }
            }
        }

        if(uploadfile != null){
            Bitmap bitmap = BitmapFactory.decodeFile(uploadfile.getPath());
            ivPhoto.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
