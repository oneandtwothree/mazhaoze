package com.example.liaoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.adapter.CommonAdapter;
import com.example.framework.adapter.CommonViewHolder;
import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.Friend;
import com.example.framework.bmob.IMUser;
import com.example.framework.cloud.CloudManager;
import com.example.framework.event.EventManager;
import com.example.framework.manager.HttpManager;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.liaoapp.MainActivity;
import com.example.liaoapp.R;
import com.example.liaoapp.model.AllFriendModel;
import com.example.liaoapp.model.ChoseFriendModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChoseFriendActivity extends BaseBackActivity {
    private ViewStub itemEmptyView;
    private RecyclerView mFriendView;

    private CommonAdapter<ChoseFriendModel> mallfriendadap;
    private List<ChoseFriendModel> modelList = new ArrayList<>();
    private List<ChoseFriendModel> checkList ;

    private String groupid = null;
    private String groupname = null;

    private Button choseFinish;
    private Disposable subscribes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);

        checkList = new ArrayList<>();

        Intent intent = getIntent();
        groupid = intent.getStringExtra("groupid");
        groupname = intent.getStringExtra("groupname");

        initview();




    }

    private void initview() {
        itemEmptyView = findViewById(R.id.item_empty_view);
        mFriendView = findViewById(R.id.mFriendView);

        choseFinish = findViewById(R.id.chose_finish);

        mFriendView.setLayoutManager(new LinearLayoutManager(this));
        mFriendView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));



        mallfriendadap = new CommonAdapter<>(modelList, new CommonAdapter.OnBindDataListener<ChoseFriendModel>() {
            @Override
            public void onBindViewHolder(final ChoseFriendModel model, CommonViewHolder commonViewHolder, int type, int position) {
                commonViewHolder.setImgurl(ChoseFriendActivity.this,R.id.iv_photo,model.getUrl());
                commonViewHolder.setText(R.id.tv_nickname,model.getNickname());
                commonViewHolder.setText(R.id.tv_desc,model.getDesc());
                commonViewHolder.setImgsex(R.id.iv_sex,model.isSex()?R.drawable.img_boy_icon:R.drawable.img_girl_icon);

                CheckBox cb = commonViewHolder.getView(R.id.friend_pd);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (modelList.get(position).isIscheck()){
                            cb.setChecked(false);
                            modelList.get(position).setIscheck(false);
                            checkList.remove(modelList.get(position));
                        }else {
                            cb.setChecked(true);
                            modelList.get(position).setIscheck(true);
                            checkList.add(modelList.get(position));
                        }
                    }
                });

            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_friend_item;
            }
        });
        mFriendView.setAdapter(mallfriendadap);
        queryMyFriends();


        choseFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(null != groupid){
                   if(null != groupname){
                       creategroup();
                   }
               }
            }
        });
    }

    private void creategroup() {
        final HashMap<String,String> map = new HashMap<>();
        map.put("userId",BmobManager.getInstance().getUser().getObjectId());
        for (int i = 0; i <checkList.size() ; i++) {
            ChoseFriendModel choseFriendModel = checkList.get(i);
            String id = choseFriendModel.getId();
            map.put("userIds",id);
        }
        map.put("groupId",groupid);
        map.put("groupName",groupname);


        subscribes = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = HttpManager.getInstance().postCloudGoup(map);
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        for (int i = 0; i <checkList.size() ; i++) {
                            ChoseFriendModel choseFriendModel = checkList.get(i);
                            String id = choseFriendModel.getId();
                            BmobManager.getInstance().addMyGroup(id, groupid, groupname,new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                }
                            });
                        }

                        BmobManager.getInstance().addMyGroup(BmobManager.getInstance().getUser().getObjectId(), groupid, groupname,new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    startActivity(new Intent(ChoseFriendActivity.this, MainActivity.class));
                                }
                            }
                        });
                    }
                });
    }

    private void queryMyFriends(){
        BmobManager.getInstance().queryallFriend(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        itemEmptyView.setVisibility(View.GONE);
                        mFriendView.setVisibility(View.VISIBLE);
                        if (modelList.size() > 0) {
                            modelList.clear();
                        }
                        for (int i = 0; i <list.size() ; i++) {
                            Friend friend = list.get(i);
                            String objectId = friend.getFrienduser().getObjectId();
                            BmobManager.getInstance().queryidFriend(objectId, new FindListener<IMUser>() {
                                @Override
                                public void done(List<IMUser> list, BmobException e) {
                                    if(e == null) {
                                        if (CommonUtils.isEmpty(list)) {
                                            IMUser imUser = list.get(0);

                                            ChoseFriendModel allFriendModel = new ChoseFriendModel();
                                            allFriendModel.setId(imUser.getObjectId());
                                            allFriendModel.setUrl(imUser.getPhoto());
                                            allFriendModel.setNickname(imUser.getNickName());
                                            allFriendModel.setDesc("签名："+imUser.getDesc());
                                            allFriendModel.setSex(imUser.isSex());
                                            allFriendModel.setIscheck(false);
                                            modelList.add(allFriendModel);
                                            mallfriendadap.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }else {
                        itemEmptyView.setVisibility(View.VISIBLE);
                        mFriendView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribes != null) {
            if (!subscribes.isDisposed()) {
                subscribes.dispose();
            }
        }
    }
}
