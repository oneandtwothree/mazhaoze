package com.example.liaoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.framework.R;
import com.example.framework.utils.GlideHelper;
import com.example.liaoapp.model.StarModel;
import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.List;

public class CloudTagAdapter extends TagsAdapter {

    private Context context;
    private List<StarModel> mlist;
    private LayoutInflater layoutInflater;

    private TextView tvStarName;
    private ImageView ivStarIcon;



    public CloudTagAdapter(Context context, List<StarModel> mlist) {
        this.context = context;
        this.mlist = mlist;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.layout_star_view_item, null);
        tvStarName = view.findViewById(R.id.tv_star_name);
        ivStarIcon = view.findViewById(R.id.iv_star_icon);
        StarModel starModel = mlist.get(position);

        GlideHelper.setimg(context,starModel.getPhotoUrl(),ivStarIcon);
        tvStarName.setText(starModel.getNickName());

        return view;
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }
}
