package com.example.framework.db;

import org.litepal.crud.LitePalSupport;

public class NewFriend extends LitePalSupport {

    private String msg;
    private String usid;
    private long saveTime;


    private int isAgree = -1;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return usid;
    }

    public void setId(String usid) {
        this.usid = usid;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(int isAgree) {
        this.isAgree = isAgree;
    }
}
