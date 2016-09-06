package com.runrunfast.homegym.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.runrunfast.homegym.R;

public class PopupWindows extends PopupWindow {

	private View view;
	private View parent;
	private Context mContext;
	private View layout;

	public View getLayout() {
		return layout;
	}

	public void setLayout(View layout) {
		this.layout = layout;
	}

	public PopupWindows(Context mContext, View parent) {
		this.mContext = mContext;
		this.parent = parent;
	}

	public void show() {
		view = layout;
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_in));

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.CENTER, 0, 0);
		update();
	}
}
