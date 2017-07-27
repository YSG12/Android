package com.stav.zhbj.fragment;


import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stav.zhbj.MainActivity;
import com.stav.zhbj.R;
import com.stav.zhbj.base.BasePager;
import com.stav.zhbj.base.impl.GovAffairsPager;
import com.stav.zhbj.base.impl.HomePager;
import com.stav.zhbj.base.impl.NewsCenterPager;
import com.stav.zhbj.base.impl.SettingPager;
import com.stav.zhbj.base.impl.SmartServicePager;
import com.stav.zhbj.view.NoScrollViewPager;

import java.util.ArrayList;

/**
 * 主界面activity
 */
public class ContentFragment extends BaseFragment {

    private NoScrollViewPager vp_content;
    private ArrayList<BasePager> mPagers;   //五个标签页的集合
    private ContentAdapter mContentAdapter;
    private RadioGroup rg_group;

    @Override
    public View initView() {

        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        vp_content = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mPagers = new ArrayList<>();
        //添加五个标签页
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mContentAdapter = new ContentAdapter();
        vp_content.setAdapter(mContentAdapter);

        //底栏标签切换监听
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        //首页
                        vp_content.setCurrentItem(0);
                        //vp_content.setCurrentItem(0,false);     //参2是否平滑的滑动
                        break;
                    case R.id.rb_news:
                        //新闻中心
                        vp_content.setCurrentItem(1);
                        break;
                    case R.id.rb_smart:
                        //智慧服务
                        vp_content.setCurrentItem(2);
                        break;
                    case R.id.rb_gov:
                        //政务
                        vp_content.setCurrentItem(3);
                        break;
                    case R.id.rb_setting:
                        //设置
                        vp_content.setCurrentItem(4);
                        break;
                    default:
                        break;
                }
            }
        });

        vp_content.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mPagers.get(position);
                //初始化数据
                pager.initData();
                if (position == 0 || position == mPagers.size()-1) {
                    setSlidingMenuEnable(false);
                }
                else {
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //手动加载第一个页面
        mPagers.get(0).initData();
        //首页禁用侧边栏
        setSlidingMenuEnable(false);

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


    class ContentAdapter extends PagerAdapter {

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

            BasePager pager = mPagers.get(position);
            View view = pager.mRootView;    //获取当前页面的布局
            //初始化数据
            //pager.initData();     //viewpager会默认加载下一个页面，为了节省流量和性能，不要再次加载调用初始化数据的方法
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    /**
     * 获取新闻中心页面
     */
    public NewsCenterPager getNewsCenterPager() {

        NewsCenterPager pager = (NewsCenterPager) mPagers.get(1);
        return pager;
    }
}
