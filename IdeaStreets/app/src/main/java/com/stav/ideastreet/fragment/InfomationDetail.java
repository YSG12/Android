package com.stav.ideastreet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stav.ideastreet.R;
import com.stav.ideastreet.base.BaseActivity;
import com.stav.ideastreet.base.BaseFragment;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * 个人资料详情页
 * @author stav
 * @date 2017/11/6 14:29
 */
public class InfomationDetail extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getApplicationContext(), R.layout.activity_main2, null);
        return view;
    }
}
