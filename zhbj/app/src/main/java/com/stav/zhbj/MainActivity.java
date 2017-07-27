package com.stav.zhbj;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.stav.zhbj.fragment.ContentFragment;
import com.stav.zhbj.fragment.LeftMenuFragment;
import com.umeng.analytics.MobclickAgent;


public class MainActivity extends SlidingFragmentActivity {

    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
    private static final String TAG_CONTENT = "TAG_CONTENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去掉标题
        setContentView(R.layout.activity_main);

        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);    //全屏触摸
        slidingMenu.setBehindOffset((int) (getWindowManager().getDefaultDisplay().getWidth()*0.6)); //屏幕预留200像素宽度

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
//        fm.findFragmentByTag(TAG_CONTENT);  //根据标记找到相应的fragment
    }

    //获取侧边栏fragment
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment leftMenuFragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);  //根据标记找到相应的fragment
        return leftMenuFragment;
    }
    //获取内容页fragment
    public ContentFragment getContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment contentFragment = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);  //根据标记找到相应的fragment
        return contentFragment;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
