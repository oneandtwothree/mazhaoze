package com.example.framework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.framework.R;
import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.List;

public class CloudTagAdapter extends TagsAdapter {

    private Context context;
    private List<String> mlist;
    private LayoutInflater layoutInflater;

    private TextView tvStarName;
    private ImageView ivStarIcon;



    public CloudTagAdapter(Context context, List<String> mlist) {
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

        tvStarName.setText(mlist.get(position));
        switch (position % 10){
            case 0:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_1);
                break;
            case 1:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_2);
                break;
            case 2:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_3);
                break;
            case 3:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_4);
                break;
            case 4:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_5);
                break;
            case 5:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_6);
                break;
            default:
                ivStarIcon.setImageResource(R.drawable.img_guide_star_7);
                break;
        }


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
