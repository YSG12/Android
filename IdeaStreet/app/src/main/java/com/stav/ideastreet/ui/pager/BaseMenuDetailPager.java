package com.stav.ideastreet.ui.pager;

import android.app.Activity;
import android.view.View;

/**
 * @author stav
 * @date 2017/9/6 15:18
 */
public abstract class BaseMenuDetailPager {
    public Activity mActivity;
    public final View mRootView;

    public BaseMenuDetailPager(Activity activity) {

        mActivity = activity;
        mRootView = initView();
    }

    //初始化布局
    public abstract View initView();

    //初始化数据
    public void initData() {

    }

}
