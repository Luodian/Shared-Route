package com.example.administrator.bannertest;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by 17990 on 2017/9/23.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView){
        Glide.with(context.getApplicationContext())
                .load(path)
                .into(imageView);
    }
}
