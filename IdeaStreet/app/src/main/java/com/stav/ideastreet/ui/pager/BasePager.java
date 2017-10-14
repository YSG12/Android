package com.stav.ideastreet.ui.pager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.activity.MainActivity;

/**
 * @author stav
 * @date 2017/9/5 11:15
 */
public class BasePager{

    public View mRootView; //当前页面的布局对象
    public Activity mActivity;  //该Activity是MainActivity
    public TextView tv_title;
    public ImageButton ib_menu,ib_back;
    public FrameLayout fl_content;  //空的帧布局对象，要动态添加布局
    public ImageButton ib_photo;
    public Button bt_register, bt_login, bt_setting;

    public BasePager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
    }

    public View initView() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        bt_register = (Button) view.findViewById(R.id.bt_register);
        bt_login = (Button) view.findViewById(R.id.bt_login);
        bt_setting = (Button) view.findViewById(R.id.bt_setting);

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
    public void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();   //切换开关侧边栏
    }

    public void initData() {

    }
}
