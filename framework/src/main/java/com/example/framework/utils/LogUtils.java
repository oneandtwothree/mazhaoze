package com.example.framework.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.framework.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void i (String text){
           if(BuildConfig.LOG_DEBUG){
               if(!TextUtils.isEmpty(text)){
               Log.i(BuildConfig.LOG_TAG, text);
                   writeToFile(text);
           }
        }
    }
    public static void e (String text){
        if(BuildConfig.LOG_DEBUG){
            if(!TextUtils.isEmpty(text)){
                Log.e(BuildConfig.LOG_TAG, text);
                writeToFile(text);
            }
        }
    }

    private static void writeToFile(String text){
        String fileName = "/sdcard/LiaoApp/LiaoApp.log";

        String log = simpleDateFormat.format(new Date())+" "+text + "\n";

        File fileGroup = new File("/sdcard/LiaoApp/");
        if(!fileGroup.exists()){
            fileGroup.mkdirs();
        }


        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            fileOutputStream = new FileOutputStream(fileName,true);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream,Charset.forName("gbk")));
            bufferedWriter.write(log);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
