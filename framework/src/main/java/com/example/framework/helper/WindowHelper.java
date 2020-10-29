package com.example.framework.helper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class WindowHelper {
    private static volatile WindowHelper windowHelper = null;

    private Context mcontext;
    private WindowManager wm;
    private WindowManager.LayoutParams layoutParams;;

    private Handler handler = new Handler();

    public WindowHelper() {
    }

    public static WindowHelper getInstance(){
        if(windowHelper == null){
            synchronized (WindowHelper.class){
                if(windowHelper == null){
                    windowHelper = new WindowHelper();
                }
            }
        }
        return windowHelper;
    }

    public void initWindow(Context context){
        this.mcontext = context;

        wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        layoutParams = createLayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

    }

    public WindowManager.LayoutParams createLayoutParams(int width, int height, int gravity) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //设置宽高
        layoutParams.width = width;
        layoutParams.height = height;

        //设置标志位

        layoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        //设置格式
        layoutParams.format = PixelFormat.TRANSLUCENT;

        //设置位置
        layoutParams.gravity = gravity;

        //设置类型
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        return layoutParams;
    }


    public View getview(int viewid){
        return View.inflate(mcontext,viewid,null);
    }

    public void showview(final View view){
        if(view != null){
            if(view.getParent() ==  null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            wm.addView(view, layoutParams);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
    public void hideview(final View view){
        if(view != null){
            if(view.getParent() !=  null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            wm.removeView(view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void updateview(final View view, final WindowManager.LayoutParams layoutParams){
        if(view != null && layoutParams != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    wm.updateViewLayout(view,layoutParams);
                }
            });
        }
    }

}
