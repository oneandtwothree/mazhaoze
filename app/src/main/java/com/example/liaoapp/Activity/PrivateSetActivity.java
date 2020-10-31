package com.example.liaoapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.PrivateSet;
import com.example.framework.utils.CommonUtils;
import com.example.framework.view.LodingView;
import com.example.liaoapp.R;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PrivateSetActivity extends BaseBackActivity implements View.OnClickListener {

    private Switch swKillContact;
    private LodingView lodingView;

    private boolean isCheck = false;
    private String usid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_set);

        initview();

    }

    private void initview() {
        lodingView = new LodingView(this);
        swKillContact = findViewById(R.id.sw_kill_contact);
        swKillContact.setOnClickListener(this);

        queryPrivateSet();
    }

    private void queryPrivateSet() {
        BmobManager.getInstance().queryPrivateSet(new FindListener<PrivateSet>() {
            @Override
            public void done(List<PrivateSet> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        for (int i = 0; i <list.size() ; i++) {
                            PrivateSet privateSet = list.get(i);
                            if(privateSet.getUserid().equals(BmobManager.getInstance().getUser().getObjectId())){
                                isCheck = true;
                                usid = privateSet.getObjectId();
                                break;
                            }
                        }
                        swKillContact.setChecked(isCheck);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sw_kill_contact:
                isCheck = !isCheck;
                swKillContact.setChecked(isCheck);
                if(isCheck){
                    addPrivateSet();
                }else {
                    delPrivateSet();
                }
                break;
        }
    }

    private void addPrivateSet() {
        lodingView.show("正在打开...");
        BmobManager.getInstance().addPrivateset(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                lodingView.hide();
                if(e == null){
                    usid = s;
                    Toast.makeText(PrivateSetActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void delPrivateSet() {
        lodingView.show("正在关闭...");
        BmobManager.getInstance().delPrivateset(usid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                lodingView.hide();
                if(e == null){
                    Toast.makeText(PrivateSetActivity.this, "关闭成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
