package com.example.liaoapp.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseBackActivity implements View.OnClickListener {

    private LinearLayout llToContact;
    private EditText etPhone;
    private ImageView ivSearch;

    private RecyclerView mSearchResultView;
    private View include_emptu;

    private AddFriendAdapter addFriendAdapter;
    private List<AddFriendModel> mlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initview();
    }

    private void initview() {
        llToContact = findViewById(R.id.ll_to_contact);
        etPhone = findViewById(R.id.et_phone);
        ivSearch = findViewById(R.id.iv_search);
        mSearchResultView = findViewById(R.id.mSearchResultView);
        include_emptu = findViewById(R.id.include_empty_view);

        llToContact.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        mSearchResultView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        addFriendAdapter = new AddFriendAdapter(this,mlist);
        mSearchResultView.setAdapter(addFriendAdapter);

        addFriendAdapter.setOnClickListener(new AddFriendAdapter.onClickListener() {
            @Override
            public void Onclick(int position) {
                Toast.makeText(AddFriendActivity.this, "position:"+position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_to_contact:
                    if(checkPermissions(Manifest.permission.READ_CONTACTS)){
                        startActivity(new Intent(this,ContactFriendActivity.class));
                    }else {
                        requestPermission(new String[]{Manifest.permission.READ_CONTACTS});
                    }
                break;
            case R.id.iv_search:
                queryPhoneFriend();
                break;
        }
    }




    private void queryPhoneFriend() {
        String phone = etPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }

        String mobilePhoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
        LogUtils.i("mobilePhoneNumber:"+mobilePhoneNumber);
        if(phone.equals(mobilePhoneNumber)){
            Toast.makeText(this, "不能查询自己", Toast.LENGTH_SHORT).show();
            return;
        }


        BmobManager.getInstance().queryFriend(phone, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if(e != null){
                    return;
                }
                if(CommonUtils.isEmpty(list)){
                   IMUser imUser = list.get(0);
                   include_emptu.setVisibility(View.GONE);
                   mSearchResultView.setVisibility(View.VISIBLE);

                   mlist.clear();
                   addTitle("查询结果");
                   addcontext(imUser);
                   addFriendAdapter.notifyDataSetChanged();
                   pushUser();

                }else {
                    include_emptu.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.GONE);
                }
            }
        });

    }

    private void pushUser() {
        BmobManager.getInstance().queryAll(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        addTitle("推荐好友");
                        int num = (list.size() <= 50)?list.size():50;
                        for (int i = 0; i <num ; i++) {
                            String mobilePhoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
                            if(list.get(i).getMobilePhoneNumber().equals(mobilePhoneNumber)){
                                continue;
                            }
                            addcontext(list.get(i));
                        }
                        addFriendAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addTitle(String tit){
        AddFriendModel addFriendModel = new AddFriendModel();
        addFriendModel.setType(AddFriendAdapter.TYPE_TITLE);
        addFriendModel.setTitle(tit);
        mlist.add(addFriendModel);

    }
    private void addcontext(IMUser imUser){
        AddFriendModel addFriendModel = new AddFriendModel();
        addFriendModel.setType(AddFriendAdapter.TYPE_CONTENT);
        addFriendModel.setName(imUser.getNickName());
        addFriendModel.setUserid(imUser.getObjectId());
        addFriendModel.setSex(imUser.isSex());
        addFriendModel.setPhoto(imUser.getPhoto());
        addFriendModel.setDesc(imUser.getDesc());
        addFriendModel.setAge(imUser.getAge());
        mlist.add(addFriendModel);
    }
}
