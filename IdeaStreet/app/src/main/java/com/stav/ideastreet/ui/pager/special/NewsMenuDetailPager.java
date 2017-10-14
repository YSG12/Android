package com.stav.ideastreet.ui.pager.special;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.stav.ideastreet.ui.pager.BaseMenuDetailPager;

/**
 * @author stav
 * @date 2017/9/6 15:29
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {
    public NewsMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {

        //给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("菜单详情页-互动");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }
}
