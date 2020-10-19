package com.example.framework.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.framework.R;
import com.example.framework.bmob.DiaLogManager;
import com.example.framework.utils.AnimUtils;

public class LodingView {

    private DiaLogView diaLogView;
    private ImageView ivLoding;
    private TextView tvLodingText;
    private ObjectAnimator mAinm;


    public LodingView(Context context) {
        diaLogView = DiaLogManager.getInstance().initview(context, R.layout.dialog_loding);

        ivLoding = diaLogView.findViewById(R.id.iv_loding);
        tvLodingText = diaLogView.findViewById(R.id.tv_loding_text);

        mAinm = AnimUtils.rotation(ivLoding);

    }

    public void setLodingText(String text){
        if(!TextUtils.isEmpty(text)){
            tvLodingText.setText(text);
        }
    }

    public void show(){
        mAinm.start();
        DiaLogManager.getInstance().show(diaLogView);
    }
    public void show(String text){
        setLodingText(text);
        mAinm.start();
        DiaLogManager.getInstance().show(diaLogView);
    }
    public void hide(){
        mAinm.pause();
        DiaLogManager.getInstance().hide(diaLogView);
    }


}
