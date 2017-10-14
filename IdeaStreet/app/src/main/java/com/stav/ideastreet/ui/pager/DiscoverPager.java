package com.stav.ideastreet.ui.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * 发现界面
 * @author stav
 * @date 2017/9/5 11:18
 */
public class DiscoverPager extends BasePager {

    public DiscoverPager(Activity activity){
        super(activity);
    }
    @Override
    public void initData() {
        System.out.println("发现设置完成");
        //给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("发现界面");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        //添加布局
        fl_content.addView(view);

        //修改标题
        tv_title.setText("发现");

        //隐藏菜单按钮
        ib_menu.setVisibility(View.INVISIBLE);
    }
}
