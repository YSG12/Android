package com.stav.ideastreet.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.fragment.ContentFragment;
import com.stav.ideastreet.ui.fragment.LeftMenuFragment;

public class MainActivity extends SlidingFragmentActivity {

    private static final String TAG_CONTENT = "TAG_CONTENT";
    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加主界面
        setContentView(R.layout.activity_main);
        //添加菜单栏
        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        //全屏触摸
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //给菜单栏设置屏幕宽度的60%
        slidingMenu.setBehindOffset((int) (getWindowManager().getDefaultDisplay().getWidth()*0.4));
        initFragment();

    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();    //开始事务
        transaction.replace(R.id.fl_main, new ContentFragment(), TAG_CONTENT);     //用fragment替换帧布局，1.帧布局的id，2是要替换的fragment
        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU);     //用fragment替换帧布局，1.帧布局的id，2是要替换的fragment
        transaction.commit();   //提交事务
    }

    //获取侧边栏fragment
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        return (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);
    }
    //获取内容页fragment
    public ContentFragment getContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        return (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
    }

}
