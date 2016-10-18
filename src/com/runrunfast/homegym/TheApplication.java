package com.runrunfast.homegym;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.course.DataIniter;
import com.runrunfast.homegym.utils.Globle;
import com.tencent.bugly.crashreport.CrashReport;

import io.vov.vitamio.Vitamio;

import org.xutils.x;

public class TheApplication extends Application{
	@Override
	public void onCreate() {
		Globle.gApplicationContext = this;
		
		x.Ext.init(this);
//	    x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
		x.Ext.setDebug(false);
	    
	    Vitamio.isInitialized(getApplicationContext());
	    
	    AccountMgr.getInstance().loadUserInfo();
	    
	    DataIniter.getInstance().initData();
	    
	    initImageLoader();
	    // 建议在测试阶段建议设置成true，发布时设置为false。
	    CrashReport.initCrashReport(getApplicationContext(), "900051312", false); 
	}

	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(480, 800)
				// default=device screen dimensions
				.threadPoolSize(5)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(this)) // default
				.imageDecoder(new BaseImageDecoder()).build();

		// Initialize ImageLoader with configuration
		ImageLoader.getInstance().init(config);
	}
	
	public void onTerminate() {
		ImageLoader.getInstance().clearMemoryCache();
	};

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
