package com.runrunfast.homegym.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class DensityUtil {
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    /** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }
    public static DisplayMetrics dm = new DisplayMetrics();

    /**
     *  获取屏幕的宽
      * @Method: getScreenWidth
      * @Description: TODO
      * @param activity
      * @return	
      * 返回类型：int
     */
    public static int getScreenWidth(Context context) {
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    /**
     * 获取屏幕的高
      * @Method: getScreenHeight
      * @Description: TODO
      * @param activity
      * @return	
      * 返回类型：int
     */
    public static int getScreenHeight(Context context) {
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
    
    public static void logScreenInfo(Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; 
        int height = metric.heightPixels; 
        float density = metric.density; 
        int densityDpi = metric.densityDpi; 
        Log.v("DensityUtil", "width = " + width + " , height = " + height + " , density = " + density + " , densityDpi = " + densityDpi);

    }
}
