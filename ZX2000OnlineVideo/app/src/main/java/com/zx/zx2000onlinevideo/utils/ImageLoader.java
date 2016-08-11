package com.zx.zx2000onlinevideo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zx.zx2000onlinevideo.R;


public class ImageLoader {
    private ImageLoader() {
    }

    public static void loadImage(Context context, String url, ImageView imageView) {

        Glide.with(context).load(url).into(imageView);
    }

    public static void loadVideoThumbImage(Context context, String url ,ImageView imageView) {
        Glide.with(context)
        .load(url)
                .placeholder(R.drawable.lemon_details_def)
                .error(R.drawable.lemon_details_small_def)
                .into(imageView);
    }
}
