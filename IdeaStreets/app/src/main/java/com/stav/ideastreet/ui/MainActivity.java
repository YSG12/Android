package com.stav.ideastreet.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.stav.ideastreet.R;
import com.stav.ideastreet.db.NewFriendManager;
import com.stav.ideastreet.event.RefreshEvent;
import com.stav.ideastreet.fragment.MainFragment;
import com.stav.ideastreet.fragment.ChatFragment;
import com.stav.ideastreet.fragment.CenterFragment;
import com.stav.ideastreet.fragment.DiscoveryFragment;
import com.stav.ideastreet.ui.dialog.QuickOptionDialog;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.stav.ideastreet.base.BaseApplication.showToast;

public class MainActivity extends ActionBarActivity {

    private ActionBar mActionBar;
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private FragmentTabHost mTabHost;

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOverflowShowingAlways();

        setContentView(R.layout.activity_main);

        initActionBar();

        //使用FragmentTabHost
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        //初始化FragmentTabHost
        mTabHost.setup(this,getSupportFragmentManager(),R.id.realcontent);
        //添加页签
        String[] strs = new String[]{
                "狂一逛",
                "消息",
                "",
                "发现",
                "个人中心"
        };
        int[] ids = new int[]{
                R.drawable.tab_icon_new,
                R.drawable.tab_icon_tweet,
                R.drawable.tab_icon_new,
                R.drawable.tab_icon_explore,
                R.drawable.tab_icon_me
        };

        Class[] clz = new Class[]{
                MainFragment.class,
                ChatFragment.class,
                DiscoveryFragment.class,
                DiscoveryFragment.class,
                CenterFragment.class
        };

        for (int i = 0; i < strs.length; i++) {
            TextView view = (TextView) View.inflate(this, R.layout.tabitem, null);
            view.setText(strs[i]);
            view.setCompoundDrawablesWithIntrinsicBounds(0,ids[i],0,0);
            mTabHost.addTab(mTabHost.newTabSpec(strs[i]).setIndicator(view),clz[i],null);
            //隐藏中间的页签
            if (i==2){
                view.setVisibility(View.INVISIBLE);
            }
        }
        //去掉分割线
        mTabHost.getTabWidget().setShowDividers(0);
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();

        // 让Actionbar左边按钮可以点击
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setLogo(R.drawable.ic_drawer);

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.drawer_open,R.string.drawer_close);
        mActionBarDrawerToggle.syncState();
    }



    //设置虚拟键手指显示Overflow按钮
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onPostCreate(savedInstanceState, persistentState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    //处理菜单按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //让actionBarDrawerToggle响应事件
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(MainActivity.this,SearchPostActivity.class));
                break;
            case R.id.send:
                showShare();
                break;
            case android.R.id.home:
                Toast.makeText(this, "home", 0).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //overflow菜单显示图标
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception ignored) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    public void quickDialog(View v){
        QuickOptionDialog dialog = new QuickOptionDialog(
                this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
                showToast(R.string.tip_double_click_exit);
                firstTime=System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 软件分享
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://47.94.129.228/ideastreet/");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("欢迎加入玩转创意街大家庭~" +
                "戳这里→→http://47.94.129.228/ideastreet/");
//        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://47.94.129.228/ideastreet/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("欢迎来到玩转创意街~");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://47.94.129.228/ideastreet/");
        // 启动分享GUI
        oks.show(this);
    }

    public void ll_find_friend(View v) {
        startActivity(new Intent(getApplicationContext(), SearchUserActivity.class));
    }
    public void rl_search_post(View v) {
        startActivity(new Intent(getApplicationContext(), SearchPostActivity.class));
    }
    public void rl_settings(View v) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.3、通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.5、通知有自定义消息接收
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        checkRedPoint();
    }

    /**
     *
     */
    private void checkRedPoint() {
        //TODO 会话：4.4、获取全部会话的未读消息数量
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            Toast.makeText(this, "您有新的消息~", Toast.LENGTH_SHORT).show();
        }
        //TODO 好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            Toast.makeText(this, "您有好友添加消息~", Toast.LENGTH_SHORT).show();
        }
    }

}
