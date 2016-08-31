package com.runrunfast.homegym.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author xinghuiquan
 * @version create time: Sep 21, 2015 11:33:03 AM
 *
 */
public class ImageWorker {

    private static final String TAG = "ImageWorker";
    
    private static final int FADE_IN_TIME = 200;
    
    private ImageCache mImageCache;

    private Bitmap mLoadingBitmap;// mDefaultBitmap

    private boolean mFadeInBitmap = true;

    private boolean mExitTasks = false;

    protected boolean mPauseWork = false;
    
    private final Object mPauseWorkLock = new Object();

    protected Resources mResources;
    
    private ExecutorService mImageWorkerPools;
    
    private ThreadFactory mImageWorkerThreadFactory;
    
    private int mReqImageWidth = Integer.MAX_VALUE;
    private int mReqImageHeight = Integer.MAX_VALUE;

    public ImageWorker(Context context) {
        if (context == null) {
            throw new NullPointerException("context is NULL");
        }
        mResources = context.getResources();
        mImageWorkerThreadFactory = new ImageWorkerThreadFactory();
        mImageWorkerPools = Executors.newCachedThreadPool(mImageWorkerThreadFactory);
    }
    
    public void loadImage(String path, ImageView imageView) {
        
        PreCondition.checkUIThread();
        
        if (imageView == null) {
            Log.e(TAG, "loadImage() imageView is NULL, path = " + path);
            return;
        }
        
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "loadImage() path is empty");
            if (mLoadingBitmap != null) {
                imageView.setImageBitmap(mLoadingBitmap);
            }
            return;
        }

        BitmapDrawable value = null;

        if (mImageCache != null) {
            value = mImageCache.getBitmapFromMemCache(path);
        }
        
        if (value != null) {
            Log.v(TAG, "loadImage() bitmap from MemCache, path = " + path);
            imageView.setImageDrawable(value);
        } else if (canclePotentialWork(path, imageView)) {
            
            final BitmapWorkerTask task = new BitmapWorkerTask(path, imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.executeOnExecutor(mImageWorkerPools);
            
            Log.v(TAG, "loadImage() create task and start process Bitmap, path = " + path + ", executor = " + AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.v(TAG, "loadImage() processing Bitmap now! path = " + path);
        }
    }

    
    public void setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
    }
    
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }
    
    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
    }
    
    public void setImageCachePercent(float percent) {
        mImageCache = new ImageCache(percent);
    }
    
    public void enableImageCache() {
        mImageCache = new ImageCache();
    }
    
    public ImageCache getImageCache() {
        return mImageCache;
    }
    
    public void setExitTasks(boolean exitTasks) {
        mExitTasks = exitTasks;
        setPauseWork(false);
    }
    
    public void setImageSize(int width, int height) {
        mReqImageWidth = width;
        mReqImageHeight = height;
    }
    
    public void setImageSize(int size) {
        setImageSize(size, size);
    }
    
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask task = getBitmapWorkerTask(imageView);
        if (task != null) {
            Log.v(TAG,"cancel task of " + imageView);
            task.cancel(true);
        }
    }
    
    private static boolean canclePotentialWork(String path, ImageView imageView) {
        final BitmapWorkerTask task = getBitmapWorkerTask(imageView);
        if (task != null) {
            final String taskPath = task.mPath;
            Log.v(TAG,"cancelPotentialWork() path = " + path + ", taskPath = " + taskPath);
            if (TextUtils.isEmpty(path) || !taskPath.equalsIgnoreCase(path)) {
                task.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }
    
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
    
    public Bitmap processBitmap(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return null;
        }
        if (!FileUtils.isFileExist(imagePath)) {
            return null;
        }
        
        return decodeSampledBitmapFromFile(imagePath, mReqImageWidth, mReqImageHeight, mImageCache);
    }
    
    public static Bitmap decodeSampledBitmapFromFile(String imagePath,
            int reqWidth, int reqHeight, ImageCache cache) {
        
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, opts);
        
        // TODO 输出内存对比数据。预期：加载大图且显示在小宽高的布局上，占用内存较小
        addInSampleSizeOption(opts, reqWidth, reqHeight);

        // TODO 输出内存对比数据。预期：内存波动小
        addInBitmapOptions(opts, cache);
        
        printOptionsOperationLog(opts, imagePath);

        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, opts);
    }
    
    private static void printOptionsOperationLog(BitmapFactory.Options opts, String imagePath){
        StringBuilder sb = new StringBuilder();
        sb.append("decodeSampledBitmapFromFile actually inSampleSize = ");
        sb.append(opts.inSampleSize);
        sb.append(", inBitmap set ");
        sb.append((opts.inBitmap == null ? "success!": "failed_"));
        sb.append(" ------- imagePath = ");
        sb.append(imagePath);
        Log.v(TAG, sb.toString());
    }
    
    private static void addInSampleSizeOption(BitmapFactory.Options opts,
            int reqWidth, int reqHeight) {
        opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        
    }
    
    private static void addInBitmapOptions(BitmapFactory.Options options,
            ImageCache cache) {
        
        options.inMutable = true;

        if (cache == null) {
            return;
        }

        Bitmap inBitmap = cache.getBitmapFromReusableSet(options);
        
        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
    }
    
    /**
     * 获取缩小比例
     * 
     * 如：inSampleSize = 2;
     * 
     * 宽高缩小一半，内存缩小1/4(格式为默认的Bitmap.Config.ARGB_8888下)
     * 
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        
        final int width = options.outWidth;
        final int height = options.outHeight;
        Log.i(TAG, "calculateInSampleSize() reqWidth = " + reqWidth + ", reqHeight = " + reqHeight + ", width = " + width + ", height = " + height);

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 确保sample后的图片宽高不小于请求宽高的两倍
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            
            // 如果缩小后的图片像素数比请求的像素数两倍还多，就继续缩小图片。
            // 适用于宽高比例很大的长长长的图片
//            long totalPixels = width * height / inSampleSize;
//            final long totalReqPixcelsCap = reqHeight * reqWidth * 2;
//            
//            while (totalPixels > totalReqPixcelsCap) {
//                inSampleSize *= 2;
//                totalPixels /= 2;
//            }
        }

        return inSampleSize;
    }
    
    private class BitmapWorkerTask extends AsyncTask<Void, Void, BitmapDrawable>{
        
        private final WeakReference<ImageView> imageViewReference;
        private String mPath;
        
        public BitmapWorkerTask(String path, ImageView imageView){
            mPath = path;
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected BitmapDrawable doInBackground(Void... params) {
            
            Bitmap bitmap = null;
            BitmapDrawable drawable = null;
            
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
//            if (!isCancelled() && getAttachedImageView() != null && !mExitTasks) {
            if (shouldProcess()) {
                bitmap = processBitmap(mPath);
            }
            
            if (bitmap != null) {
                drawable = new BitmapDrawable(mResources, bitmap);
                
                if(mImageCache != null) {
                    mImageCache.addBitmapToMemCache(mPath, drawable);
                }
            }
            
            return drawable;
        }
        
        private boolean shouldProcess() {
            if (isCancelled()) {
                Log.v(TAG,"shouldProcess() this task is cancelled!");
                return false;
            }
            if (getAttachedImageView() == null) {
                Log.v(TAG,"shouldProcess() getAttachedImageView is NULL!");
                return false;
            }
            if (mExitTasks) {
                Log.v(TAG,"shouldProcess() exit task!");
                return false;
            }
            Log.v(TAG,"shouldProcess!");
            return true;
        }
        
        
        @Override
        protected void onPostExecute(BitmapDrawable value) {
            if (isCancelled() || mExitTasks) {
                Log.v(TAG, "onPostExecute isCancelled() = " + isCancelled() + ", mExitsTasks = " + mExitTasks);
                value = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (value != null && imageView != null) {
                setImageDrawable(imageView, value);
            }
        }

        @Override
        protected void onCancelled(BitmapDrawable value) {
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
            super.onCancelled(value);
        }
        
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();

            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }
            return null;
        }
    }
    
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    
    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        Log.v(TAG, "setImageDrawable! mFadeInBitmap = " + mFadeInBitmap);
        if (mFadeInBitmap) {
            final TransitionDrawable td = new TransitionDrawable(
                    new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            drawable });
            
            imageView.setBackground(new BitmapDrawable(mResources, mLoadingBitmap));

            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }
    
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }
    
    public BitmapDrawable removeCache(String key){
        if (mImageCache != null) {
            return mImageCache.removeMemCache(key);
        }
        return null;
    }
    
    public void clearCache() {
        if (mImageCache != null) {
            long time = System.currentTimeMillis();
            mImageCache.clearMemCache();
            Log.v(TAG,"evictAll() cost time is " + (System.currentTimeMillis() - time));
        }
        if (mImageWorkerPools != null) {
            mImageWorkerPools.shutdownNow();
        }
    }
    
    private static class ImageWorkerThreadFactory implements ThreadFactory{
        
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("ImageWorker#" + threadNumber.getAndIncrement());
            return t;
        }
    }
}
