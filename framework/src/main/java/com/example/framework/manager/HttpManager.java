package com.example.framework.manager;

import com.example.framework.cloud.CloudManager;
import com.example.framework.utils.SHA1;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpManager {


    private static volatile HttpManager httpManager = null;

    private OkHttpClient okHttpClient;




    public HttpManager() {
        okHttpClient = new OkHttpClient();
    }

    public static HttpManager getInstance(){
        if(httpManager == null){
            synchronized (HttpManager.class){
                if(httpManager == null){
                    httpManager = new HttpManager();
                }
            }
        }
        return httpManager;
    }

    public String postCloudToken(HashMap<String,String> map){
        String Timetamp = String.valueOf(System.currentTimeMillis() / 1000);
        String Nonce = String.valueOf(Math.floor(Math.random() * 100000));

        String Signature = SHA1.sha1(CloudManager.CLOUD_SECRET + Nonce + Timetamp);

        FormBody.Builder builder = new FormBody.Builder();

        for (String key:map.keySet()) {
            builder.add(key,map.get(key));
        }

        RequestBody build = builder.build();
        Request request = new Request.Builder()
                .url(CloudManager.TOKEN_URL)
                .addHeader("Timestamp", Timetamp)
                .addHeader("App-Key", CloudManager.CLOUD_KEY)
                .addHeader("Nonce", Nonce)
                .addHeader("Signature", Signature)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(build)
                .build();


        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
            return "";
    }



}
