package com.example.framework.bmob;

import cn.bmob.v3.BmobObject;

public class PrivateSet extends BmobObject {

    private String Userid;
    private String phone;

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
