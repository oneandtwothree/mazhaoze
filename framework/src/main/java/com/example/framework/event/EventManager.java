package com.example.framework.event;

import org.greenrobot.eventbus.EventBus;

public class EventManager {

    public static final int FLAG_TEST = 1000;


    public static void register(Object obj){
        EventBus.getDefault().register(obj);
    }

    public static void unregister(Object obj){
        EventBus.getDefault().unregister(obj);
    }

    public static void post(int type){
        EventBus.getDefault().post(new MessageEvent(FLAG_TEST));
    }

}
