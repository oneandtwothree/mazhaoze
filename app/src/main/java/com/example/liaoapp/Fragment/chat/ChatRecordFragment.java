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
import com.example.framework.event.EventManager;
import com.example.framework.event.MessageEvent;
import com.example.framework.gson.TextBean;
import com.example.framework.utils.CommonUtils;
import com.example.framework.utils.LogUtils;
import com.example.liaoapp.R;
import com.example.liaoapp.model.ChatRecordModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;


public class ChatRecordFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View item_empty_view;
    private SwipeRefreshLayout mChatRecordRefreshLayout;
    private RecyclerView mChatRecordView;


    private CommonAdapter<ChatRecordModel> mChatRecordadap;
    private List<ChatRecordModel> mlist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_record, null);
        initView(view);
        return view;
    }

    private void initView(final View view) {
        item_empty_view = view.findViewById(R.id.item_empty_view);
        mChatRecordRefreshLayout = view.findViewById(R.id.mChatRecordRefreshLayout);
        mChatRecordView = view.findViewById(R.id.mChatRecordView);


        mChatRecordRefreshLayout.setOnRefreshListener(this);

        mChatRecordView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatRecordView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        mChatRecordadap = new CommonAdapter<>(mlist, new CommonAdapter.OnBindDataListener<ChatRecordModel>() {
            @Override
            public void onBindViewHolder(ChatRecordModel model, CommonViewHolder commonViewHolder, int type, int position) {
                commonViewHolder.setImgurl(getActivity(),R.id.iv_photo,model.getUrl());
                commonViewHolder.setText(R.id.tv_nickname,model.getNickName());
                commonViewHolder.setText(R.id.tv_content,model.getEndMsg());
                commonViewHolder.setText(R.id.tv_time,model.getTime());


                if(model.getUnReadSize() == 0){
                    commonViewHolder.getView(R.id.tv_un_read).setVisibility(View.GONE);
                }else {
                    commonViewHolder.getView(R.id.tv_un_read).setVisibility(View.VISIBLE);
                    commonViewHolder.setText(R.id.tv_un_read,model.getUnReadSize()+"");

                }

            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_chat_record_item;
            }
        });

        mChatRecordView.setAdapter(mChatRecordadap);

        queryChatRecord();
    }

    private void queryChatRecord() {
        mChatRecordRefreshLayout.setRefreshing(true);
        CloudManager.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                mChatRecordRefreshLayout.setRefreshing(false);
                if (CommonUtils.isEmpty(conversations)) {

                    if (mlist.size() > 0) {
                        mlist.clear();
                    }

                    for (int i = 0; i < conversations.size(); i++) {
                        final Conversation c = conversations.get(i);

                        String targetId = c.getTargetId();
                        BmobManager.getInstance().queryidFriend(targetId, new FindListener<IMUser>() {
                            @Override
                            public void done(List<IMUser> list, BmobException e) {
                                if (e == null) {
                                    if (CommonUtils.isEmpty(list)) {
                                        IMUser imUser = list.get(0);
                                        ChatRecordModel chatRecordModel = new ChatRecordModel();
                                        chatRecordModel.setUrl(imUser.getPhoto());
                                        chatRecordModel.setNickName(imUser.getNickName());
                                        chatRecordModel.setTime(new SimpleDateFormat("HH:mm:ss").format(c.getReceivedTime()));
                                        chatRecordModel.setUnReadSize(c.getUnreadMessageCount());

                                        String name = c.getObjectName();
                                        if (name.equals(CloudManager.MSG_TEXT_NAME)) {

                                            TextMessage latestMessage = (TextMessage) c.getLatestMessage();
                                            String msg = latestMessage.getContent();
                                            TextBean textBean = new Gson().fromJson(msg, TextBean.class);
                                            if(textBean.getType().equals(CloudManager.TYPE_TEXT)){
                                                chatRecordModel.setEndMsg(textBean.getMsg());
                                                mlist.add(chatRecordModel);
                                            }

                                        } else if (name.equals(CloudManager.MSG_IMAGE_NAME)) {

                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_img));
                                            mlist.add(chatRecordModel);

                                        } else if (name.equals(CloudManager.MSG_LOCATION_NAME)) {

                                            chatRecordModel.setEndMsg(getString(R.string.text_chat_record_location));
                                            mlist.add(chatRecordModel);

                                        }

                                        mChatRecordadap.notifyDataSetChanged();

                                        if(mlist.size() > 0){
                                            item_empty_view.setVisibility(View.GONE);
                                            mChatRecordView.setVisibility(View.VISIBLE);
                                        }else {
                                            item_empty_view.setVisibility(View.VISIBLE);
                                            mChatRecordView.setVisibility(View.GONE);
                                        }

                                    }
                                }
                            }
                        });

                    }
                }else {
                    mChatRecordRefreshLayout.setRefreshing(false);
                    item_empty_view.setVisibility(View.VISIBLE);
                    mChatRecordView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                mChatRecordRefreshLayout.setRefreshing(false);
                item_empty_view.setVisibility(View.VISIBLE);
                mChatRecordView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (mChatRecordRefreshLayout.isRefreshing()) {
            queryChatRecord();
        }
    }
}
