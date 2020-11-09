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
import com.example.framework.bmob.IMUser;
import com.example.framework.cloud.CloudManager;
import com.example.framework.gson.TextBean;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.framework.utils.SpUtils;
import com.example.liaoapp.Activity.ChatActivity;
import com.example.liaoapp.R;
import com.example.liaoapp.model.AllFriendModel;
import com.example.liaoapp.model.ChatRecordModel;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

public class ChatGroupFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener  {
    private SwipeRefreshLayout mGroupRecordRefreshLayout;
    private RecyclerView mGroupRecordView;
    private View item_empty_view;

    private CommonAdapter<ChatRecordModel> mChatRecordAdapter;
    private List<ChatRecordModel> mList = new ArrayList<>();
    private int ji = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_record, null);
        initView(view);
        int xiaoji = SpUtils.getInstance().getint("xiaogroupji", 0);
        ji = xiaoji;
        return view;
    }

    private void initView(View view) {
        item_empty_view = view.findViewById(R.id.item_empty_view);
        mGroupRecordRefreshLayout = view.findViewById(R.id.mGroupRecordRefreshLayout);
        mGroupRecordView = view.findViewById(R.id.mGroupRecordView);

        mGroupRecordView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupRecordView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));


        mChatRecordAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<ChatRecordModel>() {
            @Override
            public void onBindViewHolder(final ChatRecordModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImgurl(getActivity(), R.id.iv_photo, model.getUrl());
                viewHolder.setText(R.id.tv_nickname, model.getNickName());
                viewHolder.setText(R.id.tv_content, model.getEndMsg());
                viewHolder.setText(R.id.tv_time, model.getTime());

                if(ji == model.getUnReadSize()){
                    viewHolder.getView(R.id.tv_un_read).setVisibility(View.GONE);
                }else{
                    viewHolder.getView(R.id.tv_un_read).setVisibility(View.VISIBLE);
                    viewHolder.setText(R.id.tv_un_read, model.getUnReadSize()-ji+ "");
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ChatActivity.startActivity(getActivity(), model.getUserId(),model.getNickName(),model.getUrl());
                        ji = model.getUnReadSize();
                    }
                });

            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_chat_record_item;
            }
        });

        mGroupRecordView.setAdapter(mChatRecordAdapter);

    }


    private void queryMyGroup() {
        mGroupRecordRefreshLayout.setRefreshing(true);
        CloudManager.getInstance().getGroupList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                mGroupRecordRefreshLayout.setRefreshing(false);
                if (CommonUtils.isEmpty(conversations)) {
                    if (mList.size() > 0) {
                        mList.clear();
                    }
                    for (int i = 0; i < conversations.size(); i++) {
                        final Conversation c = conversations.get(i);
                        String id = c.getTargetId();
                        //查询对象的信息
                        BmobManager.getInstance().queryidFriend(id, new FindListener<IMUser>() {
                            @Override
                            public void done(List<IMUser> list, BmobException e) {
                                if (e == null) {
                                    if (CommonUtils.isEmpty(list)) {
                                        IMUser imUser = list.get(0);
                                        ChatRecordModel chatRecordModel = new ChatRecordModel();
                                        chatRecordModel.setUserId(imUser.getObjectId());
                                        chatRecordModel.setUrl(imUser.getPhoto());
                                        chatRecordModel.setNickName(imUser.getNickName());
                                        chatRecordModel.setTime(new SimpleDateFormat("HH:mm:ss")
                                                .format(c.getReceivedTime()));
                                        chatRecordModel.setUnReadSize(c.getUnreadMessageCount());

                                        String objectName = c.getObjectName();
                                        if (objectName.equals(CloudManager.MSG_TEXT_NAME)) {
                                            TextMessage textMessage = (TextMessage) c.getLatestMessage();
                                            String msg = textMessage.getContent();
                                            TextBean textBean = new Gson().fromJson(msg, TextBean.class);
                                            if (textBean.getType().equals(CloudManager.TYPE_TEXT)) {
                                                chatRecordModel.setEndMsg(textBean.getMsg());
                                                LogUtils.i(chatRecordModel.toString());
                                                mList.add(chatRecordModel);
                                            }
                                        } else if (objectName.equals(CloudManager.MSG_IMAGE_NAME)) {
                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_img));
                                            mList.add(chatRecordModel);
                                        } else if (objectName.equals(CloudManager.MSG_LOCATION_NAME)) {
                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_location));
                                            mList.add(chatRecordModel);
                                        }
                                        mChatRecordAdapter.notifyDataSetChanged();

                                        if(mList.size() > 0){
                                            item_empty_view.setVisibility(View.GONE);
                                            mGroupRecordView.setVisibility(View.VISIBLE);
                                        }else{
                                            item_empty_view.setVisibility(View.VISIBLE);
                                            mGroupRecordView.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }else{
                    mGroupRecordRefreshLayout.setRefreshing(false);
                    item_empty_view.setVisibility(View.VISIBLE);
                    mGroupRecordView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.i("onError" + errorCode);
                mGroupRecordRefreshLayout.setRefreshing(false);
                item_empty_view.setVisibility(View.VISIBLE);
                mGroupRecordView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (mGroupRecordRefreshLayout.isRefreshing()) {
            queryMyGroup();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryMyGroup();
    }
    @Override
    public void onPause() {
        super.onPause();
        SpUtils.getInstance().putint("xiaogroupji",ji);
    }
}
