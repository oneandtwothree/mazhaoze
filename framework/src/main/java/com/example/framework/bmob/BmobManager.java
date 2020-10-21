package com.example.framework.bmob;

import android.content.Context;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class BmobManager {
    private volatile static BmobManager bmobManager;
    //使用这个key，可提交到网络服务器上f8efae5debf319071b44339cf51153fc
    private static final String BMOB_KEY = "ac6c474d8d8fffa22ca52bc91f2b590d";

    private BmobManager() {
    }

    public static BmobManager getInstance(){
        if(bmobManager == null){
            synchronized (BmobManager.class){
                if(bmobManager == null){
                    bmobManager = new BmobManager();
                }
            }
        }
        return bmobManager;
    }

    public void initbmob(Context context){
        Bmob.initialize(context,BMOB_KEY);
    }

    public void requestSMScode(String phone, QueryListener<Integer> listener){
        BmobSMS.requestSMSCode(phone,"",listener);
    }

    public void signorLoginByMyPhone(String phone, String code, LogInListener<IMUser> logInListener){
        BmobUser.signOrLoginByMobilePhone(phone,code,logInListener);
    }

    public void uploadFirstPhoto(final String name, File file, final Onupload onupload){
        final IMUser user = getUser();
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    user.setNickName(name);
                    user.setPhoto(bmobFile.getFileUrl());

                    user.setTokenNickName(name);
                    user.setTokenPhoto(bmobFile.getFileUrl());


                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                onupload.OnUpDone();
                            }else {
                                onupload.OnUpFail(e);
                            }
                        }
                    });
                }else {
                    onupload.OnUpFail(e);
                }
            }
        });
    }

    public void queryFriend(String phone, FindListener<IMUser> listener){
       basequery("mobilePhoneNumber",phone,listener);
    }
    public void queryAll(FindListener<IMUser> listener){
        BmobQuery<IMUser> objectBmobQuery = new BmobQuery<>();
        objectBmobQuery.findObjects(listener);
    }

    public void basequery(String key,String value,FindListener<IMUser> listener){
        BmobQuery<IMUser> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo(key,value);
        bmobQuery.findObjects(listener);
    }


    public IMUser getUser(){
        return BmobUser.getCurrentUser(IMUser.class);
    }

    public boolean  islogin(){
        return BmobUser.isLogin();
    }

    public interface Onupload{
        void OnUpDone();
        void OnUpFail(BmobException e);
    }
}
