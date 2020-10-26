package com.example.liaoapp.Fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.framework.adapter.CommonAdapter;
import com.example.framework.adapter.CommonViewHolder;
import com.example.framework.base.BaseFragment;
import com.example.framework.bmob.BmobManager;
import com.example.framework.bmob.Friend;
import com.example.framework.bmob.IMUser;
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.liaoapp.Activity.UserInfoActivity;
import com.example.liaoapp.R;
import com.example.liaoapp.model.AllFriendModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class AllFriendFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mAllFriendRefreshLayout;
    private RecyclerView mAllFriendView;

    private View item_empty_view;
    private CommonAdapter<AllFriendModel> mallfriendadap;
    private List<AllFriendModel> modelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_record, null);
        initView(view);
        return view;
    }

    private void initView(final View view) {
        item_empty_view = view.findViewById(R.id.item_empty_view);
        mAllFriendRefreshLayout = view.findViewById(R.id.mAllFriendRefreshLayout);
        mAllFriendView = view.findViewById(R.id.mAllFriendView);

        mAllFriendView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllFriendView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        mallfriendadap = new CommonAdapter<>(modelList, new CommonAdapter.OnBindDataListener<AllFriendModel>() {
            @Override
            public void onBindViewHolder(final AllFriendModel model, CommonViewHolder commonViewHolder, int type, int position) {
                commonViewHolder.setImgurl(getActivity(),R.id.iv_photo,model.getUrl());
                commonViewHolder.setText(R.id.tv_nickname,model.getNickname());
                commonViewHolder.setText(R.id.tv_desc,model.getDesc());
                commonViewHolder.setImgsex(R.id.iv_sex,model.isSex()?R.drawable.img_boy_icon:R.drawable.img_girl_icon);

                commonViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.startActivity(getActivity(),model.getId());
                    }
                });

            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_all_friend_item;
            }
        });

        mAllFriendView.setAdapter(mallfriendadap);

        mAllFriendRefreshLayout.setOnRefreshListener(this);
        queryMyFriends();
    }

    private void queryMyFriends(){
        mAllFriendRefreshLayout.setRefreshing(true);
        BmobManager.getInstance().queryallFriend(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                mAllFriendRefreshLayout.setRefreshing(false);
                    if(e == null){
                        if(CommonUtils.isEmpty(list)){
                            item_empty_view.setVisibility(View.GONE);
                            mAllFriendView.setVisibility(View.VISIBLE);
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

                                                AllFriendModel allFriendModel = new AllFriendModel();
                                                allFriendModel.setId(imUser.getObjectId());
                                                allFriendModel.setUrl(imUser.getPhoto());
                                                allFriendModel.setNickname(imUser.getNickName());
                                                allFriendModel.setDesc("签名："+imUser.getDesc());
                                                allFriendModel.setSex(imUser.isSex());
                                                modelList.add(allFriendModel);
                                                mallfriendadap.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                            }
                        }else {
                            item_empty_view.setVisibility(View.VISIBLE);
                            mAllFriendView.setVisibility(View.GONE);
                        }
                    }
            }
        });
    }

    @Override
    public void onRefresh() {
        if(mAllFriendRefreshLayout.isRefreshing()){
            queryMyFriends();
        }
    }
}
