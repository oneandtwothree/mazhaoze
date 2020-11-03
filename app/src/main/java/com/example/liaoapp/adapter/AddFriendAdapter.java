package com.example.liaoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.framework.helper.GlideHelper;
import com.example.liaoapp.R;
import com.example.liaoapp.model.AddFriendModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_CONTENT = 1;


    private Context context;
    private List<AddFriendModel> mlist;
    private LayoutInflater layoutInflater;

    private onClickListener onClickListener;

    public void setOnClickListener(AddFriendAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public AddFriendAdapter(Context context, List<AddFriendModel> mlist) {
        this.context = context;
        this.mlist = mlist;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        if(viewType == TYPE_TITLE){
            return new TitleViewHolder(layoutInflater.inflate(R.layout.layout_search_title_item,null));
        }else if(viewType == TYPE_CONTENT){
            return new ContentViewHolder(layoutInflater.inflate(R.layout.layout_search_user_item,null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AddFriendModel addFriendModel = mlist.get(position);
        if(addFriendModel.getType() == TYPE_TITLE){
            ((TitleViewHolder)holder).textView.setText(addFriendModel.getTitle());
        }else if(addFriendModel.getType() == TYPE_CONTENT){
            GlideHelper.setimg(context,addFriendModel.getPhoto(), (ImageView) ((ContentViewHolder)holder).ivPhoto);
            ((ContentViewHolder)holder).ivSex.setImageResource(addFriendModel.isSex()?R.drawable.img_boy_icon:R.drawable.img_girl_icon);
            ((ContentViewHolder)holder).tvNickname.setText(addFriendModel.getName());
            ((ContentViewHolder)holder).tvAge.setText(addFriendModel.getAge()+"岁");
            ((ContentViewHolder)holder).tvDesc.setText(addFriendModel.getDesc());

            if(addFriendModel.isContact()){
                ((ContentViewHolder)holder).llContactInfo.setVisibility(View.VISIBLE);
                ((ContentViewHolder)holder).tvContactName.setText(addFriendModel.getCotactname());
                ((ContentViewHolder)holder).tvContactPhone.setText(addFriendModel.getCotactPhone());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null){
                    onClickListener.Onclick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mlist.get(position).getType();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title);
        }

    }
    class ContentViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView ivPhoto;
        private ImageView ivSex;
        private TextView tvNickname;
        private TextView tvAge;
        private TextView tvDesc;

        private LinearLayout llContactInfo;
        private TextView tvContactName;
        private TextView tvContactPhone;



        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivSex = itemView.findViewById(R.id.iv_sex);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvDesc = itemView.findViewById(R.id.tv_desc);

            llContactInfo = itemView.findViewById(R.id.ll_contact_info);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            tvContactPhone = itemView.findViewById(R.id.tv_contact_phone);

        }
    }

    public interface onClickListener{
        void Onclick(int position);
    }

}
