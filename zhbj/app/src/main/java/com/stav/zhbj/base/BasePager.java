package com.stav.zhbj.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stav.zhbj.MainActivity;
import com.stav.zhbj.R;

/**
 * Created by Administrator on 2017/7/21.
 */

/**
 * 五个标签页的基类
 */
public class BasePager {

    public View mRootView; //当前页面的布局对象
    public Activity mActivity;  //该Activity是MainActivity
    public TextView tv_title;
    public ImageButton ib_menu;
    public FrameLayout fl_content;  //空的帧布局对象，要动态添加布局
    public ImageButton ib_photo;

    public BasePager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
    }
    public View initView() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        ib_photo = (ImageButton) view.findViewById(R.id.ib_photo);

        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();  //收起侧边栏
            }
        });

        return view;
    }
    /**
     * 打开或关闭侧边栏
     */
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();   //切换开关侧边栏
    }
    public void initData(){

    }
}
