package com.example.framework.bmob;

import cn.bmob.v3.BmobObject;

public class Friend extends BmobObject {

    private IMUser user;
    private IMUser frienduser;

    public IMUser getUser() {
        return user;
    }

    public void setUser(IMUser user) {
        this.user = user;
    }

    public IMUser getFrienduser() {
        return frienduser;
    }

    public void setFrienduser(IMUser frienduser) {
        this.frienduser = frienduser;
    }
}
