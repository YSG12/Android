package com.stav.ideastreet.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.NewsList;
import com.stav.ideastreet.fragment.AFragment;
import com.stav.ideastreet.fragment.BFragment;
import com.stav.ideastreet.fragment.CFragment;
import com.stav.ideastreet.fragment.DFragment;
import com.stav.ideastreet.ui.dialog.QuickOptionDialog;
import com.stav.ideastreet.utils.XmlUtils;

import org.apache.http.Header;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends ActionBarActivity {

    private ActionBar mActionBar;
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverflowShowingAlways();


        setContentView(R.layout.activity_main);

        initActionBar();

        //从网络获取数据
        net();

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
                AFragment.class,
                BFragment.class,
                CFragment.class,
                CFragment.class,
                DFragment.class
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


    /**
     * 从网络获取数据
     */
    private void net() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get("http://47.94.129.228/oschina/list/news/page0.xml", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.d("tag",new String(bytes));
                NewsList newsList = XmlUtils.toBean(NewsList.class, bytes);
                Log.d("tag",newsList.getList().get(0).getTitle());
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
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
                Toast.makeText(this, "search", 0).show();
                break;
            case R.id.add:
                Toast.makeText(this, "add", 0).show();
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

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://blog.csdn.net/chenshuyang716/article/details/52464454");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("欢迎加入陶瓷创意街大家庭~");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        oks.setImageUrl("http://suo.im/2vEbsx");
//        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://blog.csdn.net/chenshuyang716/article/details/52464454");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("欢迎来到陶瓷创意街~");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://blog.csdn.net/chenshuyang716/article/details/52464454");

        // 启动分享GUI
        oks.show(this);
    }

}
