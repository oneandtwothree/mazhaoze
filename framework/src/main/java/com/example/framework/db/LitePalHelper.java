package com.example.framework.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class LitePalHelper {

    private static volatile LitePalHelper litePalHelper = null;

    public LitePalHelper() {
    }

    public static LitePalHelper getInstance(){
        if(litePalHelper == null){
            synchronized (LitePalHelper.class){
                if(litePalHelper == null){
                    litePalHelper = new LitePalHelper();
                }
            }
        }
        return litePalHelper;
    }

    public void basesave(LitePalSupport litePalSupport){
        litePalSupport.save();
    }

    public void saveNewFriend(String msg,String id){
        NewFriend newFriend = new NewFriend();
        newFriend.setMsg(msg);
        newFriend.setId(id);
        newFriend.setIsAgree(-1);
        newFriend.setSaveTime(System.currentTimeMillis());
        basesave(newFriend);
    }


    public List<? extends LitePalSupport> basequery(Class cls){
        return LitePal.findAll(cls);
    }


    public List<NewFriend> querynewfriend(){
        return (List<NewFriend>)basequery(NewFriend.class);
    }

    public void updateNewFriend(String usid,int agree){
        NewFriend newFriend = new NewFriend();
        newFriend.setIsAgree(agree);
        newFriend.updateAll("usid = ?",usid);

    }

}
