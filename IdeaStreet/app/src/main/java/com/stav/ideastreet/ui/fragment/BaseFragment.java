package com.stav.ideastreet.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author stav
 * @date 2017/9/4 18:07
 */

public abstract class BaseFragment extends Fragment {
    public Activity mActivity;

    //Fragment创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity(); //获取当前fragment所依赖的activity
    }

    //初始化Fragment的布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    //fragment 所依赖的activity的onCreate方法执行结束
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据
        initData();
    }

    //初始化布局，必须由子类实现
    public abstract View initView();
    //初始化数据，必须由子类实现
    public abstract void initData();
}

