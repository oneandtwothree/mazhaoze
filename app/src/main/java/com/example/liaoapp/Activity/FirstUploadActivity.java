package com.example.liaoapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.framework.base.BaseActivity;
import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.helper.FileHelper;
import com.example.framework.utils.LogUtils;
import com.example.framework.view.DiaLogView;
import com.example.framework.view.LodingView;
import com.example.liaoapp.R;

import java.io.File;

import cn.bmob.v3.exception.BmobException;

public class FirstUploadActivity extends BaseBackActivity implements View.OnClickListener {
    private ImageView ivPhoto;
    private EditText etNickname;
    private Button btnUpload;

    private Button btnCamera;
    private Button btnAlbum;
    private Button btnCancel;

    private DiaLogView seltphoto;
    private File uploadfile = null;

    private LodingView lodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_upload);

        initview();
    }

    private void initview() {
        lodingView = new LodingView(this);
        lodingView.setLodingText("正在上传头像...");
        ivPhoto = findViewById(R.id.iv_photo);
        etNickname = findViewById(R.id.et_nickname);
        btnUpload = findViewById(R.id.btn_upload);

        ivPhoto.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        btnUpload.setEnabled(false);

        etNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                        btnUpload.setEnabled(uploadfile != null);
                }else {
                    btnUpload.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                uploadPhoto();
                break;
            case R.id.tv_camera:
                DiaLogManager.getInstance().hide(seltphoto);
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.tv_ablum:
                DiaLogManager.getInstance().hide(seltphoto);
                FileHelper.getInstance().toTu(this);
                break;
            case R.id.tv_cancel:
                DiaLogManager.getInstance().hide(seltphoto);
                break;
        }
    }

    private void uploadPhoto() {
        lodingView.show();
        String trim = etNickname.getText().toString().trim();
        BmobManager.getInstance().uploadFirstPhoto(trim, uploadfile, new BmobManager.Onupload() {
            @Override
            public void OnUpDone() {
                lodingView.hide();
                Toast.makeText(FirstUploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                FirstUploadActivity.this.finish();
                setResult(RESULT_OK);

            }

            @Override
            public void OnUpFail(BmobException e) {
                lodingView.hide();
                Toast.makeText(FirstUploadActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initPhotoView(){
        seltphoto = DiaLogManager.getInstance().initview(this, R.layout.dialog_select_photo, Gravity.BOTTOM);

        btnCamera = seltphoto.findViewById(R.id.tv_camera);
        btnAlbum = seltphoto.findViewById(R.id.tv_ablum);
        btnCancel = seltphoto.findViewById(R.id.tv_cancel);

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
                uploadfile  = FileHelper.getInstance().getTempFile();
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


            String trim = etNickname.getText().toString().trim();
            btnUpload.setEnabled(!TextUtils.isEmpty(trim));

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
