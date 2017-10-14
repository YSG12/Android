package com.stav.ideastreet.ui.pager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.stav.ideastreet.ui.activity.SettingActivity;
import com.stav.ideastreet.utils.UIUtils;

/**
 * 逛一逛界面
 * @author stav
 * @date 2017/9/5 11:18
 */
public class HomePager extends BasePager {

    public HomePager(Activity activity){
        super(activity);
    }
    @Override
    public void initData() {
        System.out.println("首页设置完成");
        //给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("逛一逛界面");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        //添加布局
        fl_content.addView(view);

        //修改标题
        tv_title.setText("逛一逛");
        //显示注册登录按钮
        bt_register.setVisibility(View.VISIBLE);
        bt_login.setVisibility(View.VISIBLE);

        //隐藏菜单按钮
        ib_menu.setVisibility(View.INVISIBLE);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
