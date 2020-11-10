package com.example.liaoapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.framework.adapter.CommonAdapter;
import com.example.framework.adapter.CommonViewHolder;
import com.example.framework.base.BaseBackActivity;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.Group;
import com.example.framework.utils.CommonUtils;
import com.example.liaoapp.R;
import com.example.liaoapp.model.ChatRecordModel;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imlib.model.UserInfo;

public class GroupListActivity extends BaseBackActivity{

    private ViewStub itemEmptyView;
    private RecyclerView mgrouplistView;

    private List<Group> mygroup;

    private CommonAdapter<Group> mChatRecordAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initview();



    }

    private void initview() {
        mygroup = new ArrayList<>();
        itemEmptyView = findViewById(R.id.item_empty_view);
        mgrouplistView = findViewById(R.id.mgrouplistView);

        mgrouplistView.setLayoutManager(new LinearLayoutManager(this));
        mgrouplistView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        mChatRecordAdapter = new CommonAdapter<>(mygroup, new CommonAdapter.OnBindDataListener<Group>() {
            @Override
            public void onBindViewHolder(Group model, CommonViewHolder commonViewHolder, int type, int position) {
                commonViewHolder.setText(R.id.tv_groupname,model.getGroupname());
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_chat_group_item;
            }
        });


        mgrouplistView.setAdapter(mChatRecordAdapter);

        getlist();



    }

    private void getlist() {
        BmobManager.getInstance().queryMyGroup(BmobManager.getInstance().getUser().getObjectId(),new FindListener<Group>() {
            @Override
            public void done(List<Group> list, BmobException e) {
                if(e == null){
                    if(CommonUtils.isEmpty(list)){
                        mygroup.addAll(list);
                        mChatRecordAdapter.notifyDataSetChanged();
                    }else{
                        itemEmptyView.setVisibility(View.VISIBLE);
                        mgrouplistView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

}
