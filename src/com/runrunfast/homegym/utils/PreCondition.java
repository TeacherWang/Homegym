package com.runrunfast.homegym.utils;

import android.os.Looper;

/**
 * @author xinghuiquan
 * @version 创建时间：2015年8月21日 上午11:04:43
 * 
 */

public class PreCondition {

    private PreCondition() {}
    
    /**
     * 检查t是否为空
     * @param t
     * @param description 对t的描述
     */
    public static <T> T checkNull(T t, String description) {
        if (t == null) {
            throw new NullPointerException(description + " is NULL");
        }
        return t;
    }
    

    /**
     * 检查是否是UI线程
     */
    public static void checkUIThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("NOT UI Thread!");
        }
    }
    
}


