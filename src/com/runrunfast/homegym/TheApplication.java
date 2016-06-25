package com.runrunfast.homegym;

import com.runrunfast.homegym.utils.Globle;

import android.app.Application;

public class TheApplication extends Application{
	@Override
	public void onCreate() {
		Globle.gApplicationContext = this;
	}
}
