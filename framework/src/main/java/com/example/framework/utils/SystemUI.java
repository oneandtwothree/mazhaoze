package com.example.framework.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class SystemUI {

    public static void fixSystemUI(Activity maActivity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            maActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            maActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
