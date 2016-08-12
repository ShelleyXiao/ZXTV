package com.zx.zx2000onlinevideo.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zx.zx2000onlinevideo.OnlineVideoApp;
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

    public static void clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(OnlineVideoApp.getInstance()).clearDiskCache();
                    }
                });
            } else {
                Glide.get(OnlineVideoApp.getInstance()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public static void clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(OnlineVideoApp.getInstance()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
