package com.runrunfast.homegym.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author xinghuiquan
 * 
 * @version 1.0 create time: Sep 19, 2015 1:05:19 PM
 * 
 */
public class ImageCache {

    private static final String TAG = "ImageCache";
    
    private static final float DEFAULT_MEM_CACHE_PERCENT = 0.25f;//  1 / 4
    
    private LruCache<String, BitmapDrawable> mMemCache;
    
    private Set<SoftReference<Bitmap>> mReusableBitmaps;
    
    public ImageCache() {
        this(DEFAULT_MEM_CACHE_PERCENT);
    }
    
    public ImageCache(float percent) {
        init(percent);
    }
    
//    private static class SingletonHolder {
//        private static ImageCache INSTANCE = new ImageCache();
//    }
//
//    public static ImageCache getInstance() {
//        return SingletonHolder.INSTANCE;
//    }
    
    private ImageCache init(float percent) {
        
        mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        
        int maxSize = getMemCacheSizePercent(percent);
        Log.i(TAG,"LruCache maxSize = " + (maxSize / 1024) + "MB");
        
        // LruCache resize() is a hide method, can't sets the size of the cache;
        mMemCache = new LruCache<String, BitmapDrawable>(maxSize){
            
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                final int bitmapSize = getBitmapSize(value) / 1024;
                int resultSize = (bitmapSize == 0 ? 1 : bitmapSize);
                Log.v(TAG,"LruCache sizeOf  = " + (resultSize) + "KB, key = " + key);
                return resultSize; 
            }
            
            @Override
            protected void entryRemoved(boolean evicted, String key,
                    BitmapDrawable oldValue, BitmapDrawable newValue) {
                Log.i(TAG,"entryRemoved() evicted = " + evicted + ", key = " + key + ", oldValue = " + oldValue + ", newValue = " + newValue + ", mReusableBitmapsSize = " + mReusableBitmaps.size());
                mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
            }
            
        };
        
        return this;
    }
    
    private static int getMemCacheSizePercent(float percent) {
        int memCacheSize = 0;
        if (percent < 0.01f || percent > 0.8f) {
            Log.w(TAG,"percent params is illegal, set to default percent " + DEFAULT_MEM_CACHE_PERCENT);
            percent = DEFAULT_MEM_CACHE_PERCENT;
        }
    
        memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
        
        return memCacheSize;
    }
    
    private static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();
        
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }
    
    public void addBitmapToMemCache(String key, BitmapDrawable value) {
        
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("putBitMapToMemCache() key is empty");
        }
        
        if (value == null) {
            throw new NullPointerException("putBitMapToMemCache() value is NULL, key = " + key);
        }
        
        if (mMemCache == null) {
            throw new NullPointerException("shoud involk init() method!!!");
        }
        
        mMemCache.put(key, value);
    }
    
    public BitmapDrawable getBitmapFromMemCache(String key){
        
        if (mMemCache == null) {
            throw new NullPointerException("shoud involk init() method!!!");
        }
        
        if (TextUtils.isEmpty(key)) {
            Log.e(TAG, "getBitmapFromMemCache() key is empty");
            return null;
        }
        
        return mMemCache.get(key);
    }
    
    public BitmapDrawable removeMemCache(String key) {
        
        if (TextUtils.isEmpty(key)) {
            Log.e(TAG, "removeMemCache() key is empty");
            return null;
        }
        
        if (mMemCache != null) {
            return mMemCache.remove(key);
        }
        
        return null;
    }
    
    public void clearMemCache() {
        if (mMemCache == null) {
            throw new NullPointerException("shoud involk init() method!!!");
        }
        Log.i(TAG, "evictAll() log = " + mMemCache.toString());
        mMemCache.evictAll();
    }
    
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;
        
        if(mReusableBitmaps == null ){
            return null;
        }
        
        if(mReusableBitmaps.isEmpty()){
            return null;
        }
        
        // 虽然mReusableBitmaps是SynchronizedSet类型的，可操作Iterator还是需要加锁
        synchronized (mReusableBitmaps) {
            Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
            Bitmap item;

            while (iterator.hasNext()) {
                item = iterator.next().get();

                if (null != item && item.isMutable()) {
                    if (canUseForInBitmap(item, options)) {
                        bitmap = item;

                        iterator.remove();
                        break;
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        
        return bitmap;
    }
    
    /**
     * https://developer.android.com/intl/zh-cn/training/displaying-bitmaps/manage-memory.html
     * 
     * @param candidate
     * @param targetOptions
     * @return
     */
    static boolean canUseForInBitmap(
            Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    /**
     * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
     * @param candidate - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     *      <code>targetOptions</code>
     */
    static int getBytesPerPixel(Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        } else if (config == Config.RGB_565) {
            return 2;
        } else if (config == Config.ARGB_4444) {
            return 2;
        } else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
    
    public static class ImageCacheParams {
        // multiParams
    }

}
