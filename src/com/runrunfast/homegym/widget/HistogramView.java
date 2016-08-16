package com.runrunfast.homegym.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.runrunfast.homegym.R;

import java.util.ArrayList;
 
/**
*
* @author Lyloou
*/
 
public class HistogramView extends View {
	private final String TAG = "HistogramView";
	
	private Resources mResources;
	
    private Paint mPaint;
    private Rect mRect;
 
    private int mWidth;
    private int mHeight;
 
    private int mPaddingStart;
    private int mPaddingEnd;
    private int mPaddingTop;
    private int mPaddingBottom;
 
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
 
    private Context mContext;
 
    private ArrayList<Bar> mBarLists;
    
    private boolean mNotDestroy = true;
    private int mCount = 0;
    private MyThread myThread;
    private boolean mStartChange = true;
 
    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
    }
 
    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public HistogramView(Context context) {
        this(context, null);
    }
 
    private void initData() {
    	mResources = getResources();
    	
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mRect = new Rect();
 
        // default data
        mBarLists = new ArrayList<Bar>();
    }
 
    public void setBarLists(ArrayList<Bar> barLists){
        mBarLists = barLists;
        postInvalidate();
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getSizeFromMeasureSpec(widthMeasureSpec, 480);
        mHeight = getSizeFromMeasureSpec(heightMeasureSpec, 480);
 
        mPaddingStart = getPaddingStart();
        mPaddingEnd = getPaddingEnd();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
 
        mLeft = mPaddingStart;
        mTop = mPaddingTop;
        mRight = mWidth - mPaddingEnd;
        mBottom = mHeight - mPaddingBottom;
 
        setMeasuredDimension(mWidth, mHeight);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	if (null == myThread) {
            myThread = new MyThread();
            myThread.start();
        }
    	
        // set background
//        canvas.drawColor(Color.RED);
        mRect.set(mLeft, mTop, mRight, mBottom);
        mPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(mRect, mPaint);
        //*/
 
        // 设置底部文字属性
        mPaint.setTextSize(sp2Px(mContext, 11));
        mPaint.setTextAlign(Paint.Align.CENTER);
        FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int fontHeight = (int) Math.ceil(fontMetricsInt.bottom - fontMetricsInt.top);
 
        int N = mBarLists.size();
//        int UNIT_WIDTH = (mRight - mLeft) / (2 * N + 1);
        
        int UNIT_WIDTH = (int) mResources.getDimension(R.dimen.chat_column_width);
        
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
 
        // 逐个画bar
        for (int i = 0; i < N; i++) {
            Bar bar = mBarLists.get(i);
 
            // 画 bar 底部文字
            left = (int) (mLeft + (i * 2 + 0.5f) * UNIT_WIDTH);
            right = left + UNIT_WIDTH * 2;
            top = mBottom - fontHeight;
            bottom = mBottom;
            mRect.set(left, top, right, bottom);
            int baseLine = (mRect.top + mRect.bottom - fontMetricsInt.top - fontMetricsInt.bottom) / 2;
            mPaint.setColor(Color.BLACK);
            canvas.drawText(bar.bootomText, mRect.centerX(), baseLine, mPaint);
 
            // 画 bar 图形
            left = mLeft + (i * 2 + 1) * UNIT_WIDTH;
            right = left + UNIT_WIDTH;
            bottom = mBottom - fontHeight;
            top = bottom - (int) ((mBottom - mTop - fontHeight * 2) * bar.ratio);
            mRect.set(left, top, right, bottom);
            mPaint.setColor(bar.color);
            canvas.drawRect(mRect, mPaint);
 
            // 画 bar 顶部文字
            left = (int) (mLeft + (i * 2 + 0.5f) * UNIT_WIDTH);
            right = left + UNIT_WIDTH * 2;
            bottom = top;
            top = top - fontHeight;
            mRect.set(left, top, right, bottom);
            baseLine = (mRect.top + mRect.bottom - fontMetricsInt.top - fontMetricsInt.bottom) / 2;
            mPaint.setColor(Color.BLACK);
            canvas.drawText(bar.topText, mRect.centerX(), baseLine, mPaint);
        }

        // 画横线
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(mLeft, mBottom - fontHeight, mRight, mBottom - fontHeight, mPaint);
        // 画竖线
        canvas.drawLine(mLeft, mBottom - fontHeight, mLeft, mTop, mPaint);
 
        super.onDraw(canvas);
    }
    
    public class Bar {
        public int id;
        public float ratio;
        public int color;
        public String bootomText;
        public String topText;
 
 
        public Bar(int id, float ratio, int color, String bootomText, String topText) {
            this.id = id;
            this.ratio = ratio;
            this.color = color;
            this.bootomText = bootomText;
            this.topText = topText;
        }
        
        public void setColor(int color){
        	this.color = color;
        }
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	int x,y;
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = (int) event.getX();
			y = (int) event.getY();
			
			Log.d(TAG, "onTouchEvent, x = " + x + ", y = " + y);
			
			if(0 == mInTouchEventCount.touchCount){ // 第一次按下时,开始统计
				postDelayed(mInTouchEventCount, 500);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			// 一次点击事件要有按下和抬起, 有抬起必有按下, 所以只需要在ACTION_UP中处理
            mInTouchEventCount.touchCount++;
            // 如果是长按操作, 则Handler的消息,不能将touchCount置0, 需要特殊处理
            if(mInTouchEventCount.isLongClick) {
                mInTouchEventCount.touchCount = 0;
                mInTouchEventCount.isLongClick = false;
            }
			break;

		default:
			break;
		}
    	return true;
    }
    
    // 统计500ms内的点击次数
    TouchEventCountThread mInTouchEventCount = new TouchEventCountThread();
    // 根据TouchEventCountThread统计到的点击次数, perform单击还是双击事件
    TouchEventHandler mTouchEventHandler = new TouchEventHandler();
    
    public class TouchEventCountThread implements Runnable {
        public int touchCount = 0;
        public boolean isLongClick = false;

        @Override
        public void run() {
            Message msg = new Message();
            if(0 == touchCount){ // long click
                isLongClick = true;
            } else {
                msg.arg1 = touchCount;
                mTouchEventHandler.sendMessage(msg);
                touchCount = 0;
            }
        }
    }

    public class TouchEventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(mContext, "touch " + msg.arg1 + " time.", Toast.LENGTH_SHORT).show();
        }
    }
    
    class MyThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (mNotDestroy) {
                if (mStartChange) {
                    postInvalidate();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNotDestroy = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        mNotDestroy = false;
        super.onDetachedFromWindow();
    }
    
    // 工具类
    public static int getSizeFromMeasureSpec(int measureSpec, int defaultSize) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        } else {
            result = defaultSize;
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(defaultSize, size);
            }
        }
        return result;
    }
    
    public static float sp2Px(Context context, float sp){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        float px = metrics.scaledDensity;
        return sp * px;
    }
    
}
