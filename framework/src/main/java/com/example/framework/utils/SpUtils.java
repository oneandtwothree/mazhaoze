package com.example.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.framework.BuildConfig;

public class SpUtils {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private volatile static SpUtils instance = null;

    private SpUtils(){

    }

    public static SpUtils getInstance(){
        if(instance == null){
            synchronized (SpUtils.class){
                if(instance == null){
                    instance = new SpUtils();
                }
            }
        }
        return instance;
    }


    public void initSp(Context context){
        sp = context.getSharedPreferences(BuildConfig.SP_NAME,Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void putint(String key,int defvalues){
        editor.putInt(key,defvalues);
        editor.commit();
    }

    public int getint(String key,int defvalues){
        return sp.getInt(key,defvalues);
    }

    public void putstring(String key,String values){
        editor.putString(key,values);
        editor.commit();
    }

    public String getstring(String key,String defvalues){
        return sp.getString(key,defvalues);
    }

    public void putboolean(String key,boolean values){
        editor.putBoolean(key,values);
        editor.commit();
    }

    public boolean getboolean(String key,boolean defvalues){
        return sp.getBoolean(key,defvalues);
    }


    public void deleteKey(String key){
        editor.remove(key);
        editor.commit();
    }


}
