package com.stav.zhbj.base.menu;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stav.zhbj.MainActivity;
import com.stav.zhbj.R;
import com.stav.zhbj.base.BaseMenuDetailPager;
import com.stav.zhbj.base.TabDetailPager;
import com.stav.zhbj.domain.NewsMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/22.
 */

/**
 * 菜单详情页-新闻
 * ViewPagerIndicator的使用过程
 * 1.引入库
 * 2.从例子中拷贝布局文件
 * 3.从例子程序中拷贝相关代码（指示器和viewPager绑定，重写getPageTitle方法）
 * 4.在清单文件中添加样式
 * 5.修改背景为白色
 * 6.样式的修改
 *
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {
    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager vp_news_menu_datail;
    @ViewInject(R.id.tpi_indicator)
    private TabPageIndicator tpi_indicator;
    private ArrayList<NewsMenu.NewsTabData> mTabData; //页签网络数据
    private ArrayList<TabDetailPager> mPagers; //页签网络数据
    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);
        mTabData = children;
    }


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        //初始化数据
        mPagers = new ArrayList<>();
        for (int i=0;i<mTabData.size();i++) {
            TabDetailPager pager = new TabDetailPager(mActivity,mTabData.get(i));
            mPagers.add(pager);
        }
        vp_news_menu_datail.setAdapter(new NewsMenuDetailAdapter());
        tpi_indicator.setViewPager(vp_news_menu_datail);    //将viewPager和指示器绑定在一起，必须在viewPager设置完数据之后
        //设置滑动监听
//        vp_news_menu_datail.setOnPageChangeListener(this);
        //必须给指示器设置页面监听
        tpi_indicator.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (position == 0) {
            //开启侧边栏
            setSlidingMenuEnable(true);

        } else {
            //禁用侧边栏
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class NewsMenuDetailAdapter extends PagerAdapter {

        //指定指示器的标题
        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData data = mTabData.get(position);
            return data.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mPagers.get(position);
            View view = pager.mRootView;
            container.addView(view);
            pager.initData();
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 开启或关闭侧边栏
     * @param enable
     */
    public void setSlidingMenuEnable(boolean enable) {
        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        if (enable) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    @OnClick(R.id.ib_next)
    public void newtPage(View view) {
        //跳转到下一个页面
        int currentItem = vp_news_menu_datail.getCurrentItem();
        currentItem++;
        vp_news_menu_datail.setCurrentItem(currentItem);
    }

}
