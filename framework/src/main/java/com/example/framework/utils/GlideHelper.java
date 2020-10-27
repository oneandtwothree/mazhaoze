package com.example.framework.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.framework.entity.Constants;

import java.io.File;

public class GlideHelper {

    public static void setimg (Context context, String url, ImageView imageView){
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }
    public static void setimg (Context context, File file, ImageView imageView){
        Glide.with(context).load(file).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

}
