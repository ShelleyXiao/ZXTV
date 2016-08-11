package com.zx.zx2000tvfileexploer.utils;

import android.content.Context;

/**
 * 
 * Density transfer Util class
 *
 */
public class DensityUtil
{
    
    /** 
     * 根据手机的分辨率�?? dp 的单�?? 转成�?? px(像素) 
     * @param context context
     * @param dpValue dpValue
     * @return int
     */
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    /** 
     * 根据手机的分辨率�?? px(像素) 的单�?? 转成�?? dp 
     * @param context context
     * @param pxValue pxValue
     * @return int
     */
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    /***
     * 
     * @param context
     * @return 获取屏幕宽度
     */
    public static int getDisplayWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    
    /***
     * 获取屏幕的高�??
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    
}
