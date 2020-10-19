package com.example.framework.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.example.framework.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {

    public static final int CAMEAR_REQUEST_CODE = 1004;
    public static final int ALBUM_REQUEST_CODE = 1005;

    private static volatile FileHelper fileHelper;

    private SimpleDateFormat simpleDateFormat;
    private File tempfile = null;

    private Uri imgUri;

    public FileHelper() {
        simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    }

    public static FileHelper getInstance(){
        if(fileHelper == null){
            synchronized (FileHelper.class){
                if(fileHelper == null){
                    fileHelper = new FileHelper();
                }
            }
        }
        return fileHelper;
    }

    public File getTempfile() {
        return tempfile;
    }

    public void toCamera(Activity activity){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String filename = simpleDateFormat.format(new Date());
        tempfile = new File(Environment.getExternalStorageDirectory(),filename+".jpg");

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            imgUri = Uri.fromFile(tempfile);
        }else {
           imgUri =  FileProvider.getUriForFile(activity,activity.getPackageName(),tempfile);
           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        LogUtils.i("imageUri"+imgUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        activity.startActivityForResult(intent,CAMEAR_REQUEST_CODE);
    }


    public void toTu(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent,ALBUM_REQUEST_CODE);
    }

    public String getrealUri(Context context, Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(context, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }


}
