package com.example.framework.bmob;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

public class BmobManager {
    private volatile static BmobManager bmobManager;

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

    public IMUser getUser(){
        return BmobUser.getCurrentUser(IMUser.class);
    }

    public boolean  islogin(){
        return BmobUser.isLogin();
    }


}
