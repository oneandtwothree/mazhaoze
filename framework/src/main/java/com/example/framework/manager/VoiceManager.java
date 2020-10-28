package com.example.framework.manager;

import android.content.Context;

import com.example.framework.utils.LogUtils;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class VoiceManager {

    private static volatile VoiceManager voiceManager = null;

    public VoiceManager(Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=5f98d462");

        mIatDialog = new RecognizerDialog(context, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtils.i("InitListener"+i);
            }
        });

        mIatDialog.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        mIatDialog.setParameter( SpeechConstant.SUBJECT, null );
//设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "json");
//此处engineType为“cloud”
        mIatDialog.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD );
//设置语音输入语言，zh_cn为简体中文
        mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//设置结果返回语言
        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
// 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
//取值范围{1000～10000}
        mIatDialog.setParameter(SpeechConstant.VAD_BOS, "4000");
//设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
//自动停止录音，范围{0~10000}
        mIatDialog.setParameter(SpeechConstant.VAD_EOS, "1000");
//设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIatDialog.setParameter(SpeechConstant.ASR_PTT,"1");

    }

    public static VoiceManager getInstance(Context context){
        if(voiceManager == null){
            synchronized (VoiceManager.class){
                if(voiceManager == null){
                    voiceManager = new VoiceManager(context);
                }
            }
        }
        return voiceManager;
    }

    private RecognizerDialog mIatDialog;


    public void startSpeak(RecognizerDialogListener listener){
        mIatDialog.setListener(listener);
        mIatDialog.show();
    }
}
