package com.stav.ideastreet.ui.fragment;


import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.activity.MainActivity;
import com.stav.ideastreet.ui.pager.BasePager;
import com.stav.ideastreet.ui.pager.DiscoverPager;
import com.stav.ideastreet.ui.pager.HomePager;
import com.stav.ideastreet.ui.pager.MyCenterPager;
import com.stav.ideastreet.ui.pager.PublishPager;
import com.stav.ideastreet.ui.pager.SpecialPager;
import com.stav.ideastreet.ui.view.NoScrollViewPager;
import com.stav.ideastreet.utils.UIUtils;

import java.util.ArrayList;

/**
 * 主界面activity
 */
public class ContentFragment extends BaseFragment {

    private NoScrollViewPager vpContent;
    private RadioGroup rgGroup;
    private ArrayList<BasePager> mPager;

    @Override
    public View initView() {
        View view = View.inflate(UIUtils.getContext(), R.layout.fregment_content, null);
        vpContent = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mPager = new ArrayList<>();

        //添加五个标签页
        mPager.add(new HomePager(mActivity));
        mPager.add(new SpecialPager(mActivity));
        mPager.add(new PublishPager(mActivity));
        mPager.add(new DiscoverPager(mActivity));
        mPager.add(new MyCenterPager(mActivity));

        vpContent.setAdapter(new ContentAdapter());

        //底边栏标签切换监听事件
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        //首页
                        vpContent.setCurrentItem(0);    //参2是否平滑移动 默认为false
                        break;
                    case R.id.rb_special:
                        //新闻中心
                        vpContent.setCurrentItem(1);    //参2是否平滑移动 默认为false
                        break;
                    case R.id.rb_publish:
                        //首页
                        vpContent.setCurrentItem(2);    //参2是否平滑移动 默认为false
                        break;
                    case R.id.discover:
                        //首页
                        vpContent.setCurrentItem(3);    //参2是否平滑移动 默认为false
                        break;
                    case R.id.rb_center:
                        //首页
                        vpContent.setCurrentItem(4);    //参2是否平滑移动 默认为false
                        break;
                    default:
                        break;
                }
            }
        });

        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mPager.get(position);
                //初始化数据
                pager.initData();
                if (position == 1) {
                    setSlidingMenuEnable(true);
                }
                else {
                    setSlidingMenuEnable(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //手动加载第一个页面
        mPager.get(0).initData();
        //首页禁用侧边栏
        setSlidingMenuEnable(false);

    }

    /**
     * 开启或关闭侧边栏
     * @param enable 是否开启侧边栏
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
            return mPager.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager pager = mPager.get(position);
            View view = pager.mRootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 获取新闻中心的页面
     */
    public SpecialPager getSpecialPager() {
        return (SpecialPager) mPager.get(1);
    }

}
