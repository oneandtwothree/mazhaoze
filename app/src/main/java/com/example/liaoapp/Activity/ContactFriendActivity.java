package com.example.liaoapp.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.IMUser;
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
    private AddFriendAdapter addFriendAdapter;

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

        addFriendAdapter = new AddFriendAdapter(this,mlist);
        mContactView.setAdapter(addFriendAdapter);

        loadContact();
        loadUser();
    }

    private void loadUser() {
        if(map.size() > 0){
            for (final Map.Entry<String,String> entry:map.entrySet()){
                BmobManager.getInstance().queryFriend(entry.getValue(), new FindListener<IMUser>() {
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
