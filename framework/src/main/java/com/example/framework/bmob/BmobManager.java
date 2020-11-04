package com.example.framework.bmob;

import android.content.Context;

import com.example.framework.utils.CommonUtils;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class BmobManager {
    private volatile static BmobManager bmobManager;
    //使用这个key，可提交到网络服务器上f8efae5debf319071b44339cf51153fc
    private static final String BMOB_KEY = "ac6c474d8d8fffa22ca52bc91f2b590d";

    private BmobManager() {
    }

    public static BmobManager getInstance(){
        if(bmobManager == null){
            synchronized (BmobManager.class){
                if(bmobManager == null){
                    bmobManager = new BmobManager();
                }
            }
        }
        return bmobManager;
    }

    public void initbmob(Context context){
        Bmob.initialize(context,BMOB_KEY);
    }

    public void requestSMScode(String phone, QueryListener<Integer> listener){
        BmobSMS.requestSMSCode(phone,"",listener);
    }

    public void signorLoginByMyPhone(String phone, String code, LogInListener<IMUser> logInListener){
        BmobUser.signOrLoginByMobilePhone(phone,code,logInListener);
    }
    public void LoginByPas(String name,String word,LogInListener<IMUser> logInListener){
        BmobUser.loginByAccount(name, word,logInListener );
    }

    public void uploadFirstPhoto(final String name, File file, final Onupload onupload){
        final IMUser user = getUser();
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    user.setNickName(name);
                    user.setPhoto(bmobFile.getFileUrl());

                    user.setTokenNickName(name);
                    user.setTokenPhoto(bmobFile.getFileUrl());


                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                onupload.OnUpDone();
                            }else {
                                onupload.OnUpFail(e);
                            }
                        }
                    });
                }else {
                    onupload.OnUpFail(e);
                }
            }
        });
    }

    public void queryPhoneFriend(String phone, FindListener<IMUser> listener){
       basequery("mobilePhoneNumber",phone,listener);
    }
    public void queryidFriend(String id, FindListener<IMUser> listener){
       basequery("objectId",id,listener);
    }

    public void queryPrivateSet(FindListener<PrivateSet> listener){
        BmobQuery<PrivateSet> objectBmobQuery = new BmobQuery<>();
        objectBmobQuery.findObjects(listener);
    }


    public void queryallFriend(FindListener<Friend> listener){
        BmobQuery<Friend> objectBmobQuery = new BmobQuery<>();
        objectBmobQuery.addWhereEqualTo("user",getUser());
        objectBmobQuery.findObjects(listener);
    }

    public void queryAll(FindListener<IMUser> listener){
        BmobQuery<IMUser> objectBmobQuery = new BmobQuery<>();
        objectBmobQuery.findObjects(listener);
    }
    public void queryFateSet(FindListener<FateSet> listener) {
        BmobQuery<FateSet> query = new BmobQuery<>();
        query.findObjects(listener);
    }


    public void basequery(String key,String value,FindListener<IMUser> listener){
        BmobQuery<IMUser> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo(key,value);
        bmobQuery.findObjects(listener);
    }

    public void pushSquare(int mediaType, String text, String path, SaveListener<String> listener) {
        SquareSet squareSet = new SquareSet();
        squareSet.setUserId(getUser().getObjectId());
        squareSet.setPushTime(System.currentTimeMillis());

        squareSet.setText(text);
        squareSet.setMediaUrl(path);
        squareSet.setPushType(mediaType);
        squareSet.save(listener);
    }
    public void queryAllSquare(FindListener<SquareSet> listener) {
        BmobQuery<SquareSet> query = new BmobQuery<>();
        query.findObjects(listener);
    }

    public void addFriend(IMUser imUser, SaveListener<String> listener) {
        Friend friend = new Friend();
        friend.setUser(getUser());
        friend.setFrienduser(imUser);
        friend.save(listener);
    }
    public void addFriend(String id, final SaveListener<String> listener) {
        queryidFriend(id, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        IMUser imUser = list.get(0);
                        addFriend(imUser, listener);
                    }
                }
            }
        });
    }
    public void addPrivateset(SaveListener<String> listener){
        PrivateSet privateSet = new PrivateSet();
        privateSet.setUserid(getUser().getObjectId());
        privateSet.setPhone(getUser().getMobilePhoneNumber());
        privateSet.save(listener);
    }
    public void delPrivateset(String id,UpdateListener listener){
        PrivateSet privateSet = new PrivateSet();
        privateSet.setObjectId(id);
        privateSet.delete(listener);
    }
    public void addFateSet(SaveListener<String> listener) {
        FateSet set = new FateSet();
        set.setUserId(getUser().getObjectId());
        set.save(listener);
    }

    public void delFateSet(String id, UpdateListener listener) {
        FateSet set = new FateSet();
        set.setObjectId(id);
        set.delete(listener);
    }

    public IMUser getUser(){
        return BmobUser.getCurrentUser(IMUser.class);
    }

    public boolean  islogin(){
        return BmobUser.isLogin();
    }

    public void loginByAccount(String userName, String pw, SaveListener<IMUser> listener) {
        IMUser imUser = new IMUser();
        imUser.setUsername(userName);
        imUser.setPassword(pw);
        imUser.login(listener);
    }

    public void fetchUserInfo(FetchUserInfoListener<BmobUser> listener) {
        BmobUser.fetchUserInfo(listener);
    }
    public interface Onupload{
        void OnUpDone();
        void OnUpFail(BmobException e);
    }
}
