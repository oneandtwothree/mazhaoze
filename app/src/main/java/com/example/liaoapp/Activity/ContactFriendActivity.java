package com.example.liaoapp.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.adapter.CommonAdapter;
import com.example.framework.adapter.CommonViewHolder;
import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
import com.example.framework.entity.Constants;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.liaoapp.R;
import com.example.liaoapp.adapter.AddFriendAdapter;
import com.example.liaoapp.model.AddFriendModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ContactFriendActivity extends BaseBackActivity {
    private RecyclerView mContactView;
    private Map<String,String>  map = new HashMap<>();
    private List<AddFriendModel> mlist = new ArrayList<>();
    private CommonAdapter addFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_friend);

        initview();

    }

    private void initview() {
        mContactView = findViewById(R.id.mContactView);
        mContactView.setLayoutManager(new LinearLayoutManager(this));
        mContactView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        addFriendAdapter = new CommonAdapter<>(mlist, new CommonAdapter.OnMoreBindDataListener<AddFriendModel>() {
            @Override
            public int getItemType(int position) {
                return mlist.get(position).getType();
            }

            @Override
            public void onBindViewHolder(final AddFriendModel model, CommonViewHolder commonViewHolder, int type, int position) {
                if(model.getType() == AddFriendActivity.TYPE_TITLE){
                    commonViewHolder.setText(R.id.tv_title,model.getTitle());
                }else if(model.getType() == AddFriendActivity.TYPE_CONTENT) {
                    //设置头像
                    commonViewHolder.setImgurl(ContactFriendActivity.this, R.id.iv_photo, model.getPhoto());
                    //设置性别
                    commonViewHolder.setImgsex(R.id.iv_sex,
                            model.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                    //设置昵称
                    commonViewHolder.setText(R.id.tv_nickname, model.getName());
                    //年龄
                    commonViewHolder.setText(R.id.tv_age, model.getAge() + getString(R.string.text_search_age));
                    //设置描述
                    commonViewHolder.setText(R.id.tv_desc, model.getDesc());

                    commonViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserInfoActivity.startActivity(ContactFriendActivity.this,model.getUserid());
                        }
                    });
                }
            }

            @Override
            public int getLayoutId(int type) {
                if(type == AddFriendActivity.TYPE_TITLE){
                    return R.layout.layout_search_title_item;
                }else if(type == AddFriendActivity.TYPE_CONTENT){
                    return R.layout.layout_search_user_item;
                }
                return 0;
            }
        });
        mContactView.setAdapter(addFriendAdapter);

        loadContact();
        loadUser();
    }

    private void loadUser() {
        if(map.size() > 0){
            for (final Map.Entry<String,String> entry:map.entrySet()){
                BmobManager.getInstance().queryPhoneFriend(entry.getValue(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> list, BmobException e) {
                        if(e == null){
                            if(CommonUtils.isEmpty(list)){
                                IMUser imUser = list.get(0);
                                addcontext(imUser,entry.getKey(),entry.getValue());
                            }
                        }
                    }
                });
            }
        }
    }
    private void addcontext(IMUser imUser,String name,String phone) {
        AddFriendModel addFriendModel = new AddFriendModel();
        addFriendModel.setType(AddFriendAdapter.TYPE_CONTENT);
        addFriendModel.setName(imUser.getNickName());
        addFriendModel.setUserid(imUser.getObjectId());
        addFriendModel.setSex(imUser.isSex());
        addFriendModel.setPhoto(imUser.getPhoto());
        addFriendModel.setDesc(imUser.getDesc());
        addFriendModel.setAge(imUser.getAge());

        addFriendModel.setContact(true);
        addFriendModel.setCotactname(name);
        addFriendModel.setCotactPhone(phone);

        mlist.add(addFriendModel);
        addFriendAdapter.notifyDataSetChanged();
    }
    private void loadContact() {
        Cursor query = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String name;
        String phone;

        while(query.moveToNext()){
            name = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phone = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ","").replace("-","");
            map.put(name,phone);
        }

    }
}
