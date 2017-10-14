package com.stav.ideastreet.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author stav
 * @date 2017/9/5 10:38
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;   //重写方法 不拦截子控件的方法
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;    //重写此方法，触摸时什么都不做，从而实现对滑动时间
    }
}
