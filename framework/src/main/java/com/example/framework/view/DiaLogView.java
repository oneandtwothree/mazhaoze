package com.example.framework.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public class DiaLogView extends Dialog {
    public DiaLogView(Context context,int layout, int themeID,int gravity) {
        super(context,themeID);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();

        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity = gravity;

        window.setAttributes(attributes);

    }
}
