package com.example.framework.bmob;

import android.content.Context;
import android.view.Gravity;

import com.example.framework.R;
import com.example.framework.view.DiaLogView;

public class DiaLogManager {
    private static volatile DiaLogManager diaLogManager;

    private DiaLogManager() {
    }
    public static DiaLogManager getInstance(){
        if(diaLogManager == null){
            synchronized (DiaLogManager.class){
                if(diaLogManager == null){
                    diaLogManager = new DiaLogManager();
                }
            }
        }
        return  diaLogManager;
    }

    public DiaLogView initview(Context context,int layout){
        return new DiaLogView(context,layout, R.style.Theme_Dialog, Gravity.CENTER);
    }
    public DiaLogView initview(Context context,int layout,int gravity){
        return new DiaLogView(context,layout, R.style.Theme_Dialog,gravity);
    }

    public void show(DiaLogView diaLogView){
        if(diaLogView != null){
            if(!diaLogView.isShowing()){
                diaLogView.show();
            }
        }
    }
    public void hide(DiaLogView view) {
        if (view != null) {
            if (view.isShowing()) {
                view.dismiss();
            }

        }
    }
}
