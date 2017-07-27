package com.stav.zhbj.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by Administrator on 2017/7/22.
 */

public abstract class BaseMenuDetailPager {
    public Activity mActivity;
    public final View mRootView;   //菜单详情页跟布局

    public BaseMenuDetailPager(Activity activity) {

        mActivity = activity;
        mRootView = initView();
    }
    //抽象方法必须子类实现
    public abstract View initView();
    //初始化数据
    public void initData() {

    }
}
