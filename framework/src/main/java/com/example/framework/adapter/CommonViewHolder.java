package com.example.framework.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.utils.GlideHelper;

public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mviews;
    private View mcontentview;


    public CommonViewHolder(@NonNull View itemView) {
        super(itemView);
        mviews = new SparseArray<>();
        mcontentview = itemView;
    }

    public static CommonViewHolder getViewHolder(ViewGroup parent,int layoutId){
        return new CommonViewHolder(View.inflate(parent.getContext(),layoutId,null));
    }

    public <T extends View> T getView(int viewid){
        View view = mviews.get(viewid);
        if(view == null){
            view = mcontentview.findViewById(viewid);
            mviews.put(viewid,view);
        }
        return (T)view;
    }

    public CommonViewHolder setText(int viewid,String text){
        TextView view = getView(viewid);
        view.setText(text);
        return this;
    }

    public CommonViewHolder setImgurl(Context context,int viewid, String url){
        ImageView view = getView(viewid);
        GlideHelper.setimg(context,url,view);
        return this;
    }

    public CommonViewHolder setImgsex(int viewid, int resid){
        ImageView view = getView(viewid);
        view.setImageResource(resid);
        return this;
    }

    public CommonViewHolder setBackGroundColor(int viewid, int resid){
        ImageView view = getView(viewid);
        view.setBackgroundColor(resid);
        return this;
    }
}
