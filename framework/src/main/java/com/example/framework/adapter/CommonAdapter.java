package com.example.framework.adapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    private List<T> mlist;

    private OnBindDataListener<T> onBindDataListener;
    private OnMoreBindDataListener<T> onMoreBindDataListener;

    public CommonAdapter(List<T> mlist, OnBindDataListener<T> onBindDataListener) {
        this.mlist = mlist;
        this.onBindDataListener = onBindDataListener;
    }

    public CommonAdapter(List<T> mlist, OnMoreBindDataListener<T> onMoreBindDataListener) {
        this.mlist = mlist;
        this.onBindDataListener = onMoreBindDataListener;
        this.onMoreBindDataListener = onMoreBindDataListener;
    }

    public interface OnBindDataListener<T>{
        void onBindViewHolder(T model,CommonViewHolder commonViewHolder,int type,int position);
        int getLayoutId(int type);
    }

    public interface OnMoreBindDataListener<T> extends OnBindDataListener<T>{
        int getItemType(int position);
    }

    @Override
    public int getItemViewType(int position) {
        if(onMoreBindDataListener != null){
            return onMoreBindDataListener.getItemType(position);
        }
        return 0;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = onBindDataListener.getLayoutId(viewType);
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(parent, layoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        onBindDataListener.onBindViewHolder(mlist.get(position),holder,getItemViewType(position),position);
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size() ;
    }
}
