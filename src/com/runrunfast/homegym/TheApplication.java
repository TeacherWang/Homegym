package com.runrunfast.homegym;

import com.runrunfast.homegym.utils.Globle;

import org.xutils.x;

import android.app.Application;

public class TheApplication extends Application{
	@Override
	public void onCreate() {
		Globle.gApplicationContext = this;
		
		x.Ext.init(this);
	    x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
	}
}
