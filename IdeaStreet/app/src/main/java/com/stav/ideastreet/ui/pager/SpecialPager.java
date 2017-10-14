package com.stav.ideastreet.ui.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.activity.MainActivity;
import com.stav.ideastreet.ui.fragment.LeftMenuFragment;
import com.stav.ideastreet.ui.pager.special.NewsMenuDetailPager;
import com.stav.ideastreet.ui.pager.special.NewsMenuDetailPager1;
import com.stav.ideastreet.utils.UIUtils;

import java.util.ArrayList;

/**
 * 专栏界面
 * @author stav
 * @date 2017/9/5 11:18
 */
public class SpecialPager extends BasePager {

    private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;
    private String[] mTabNames;

    public SpecialPager(Activity activity){
        super(activity);
    }
    @Override
    public void initData() {
//        System.out.println("专栏设置完成");
//        //给帧布局填充布局对象
//        TextView view = new TextView(mActivity);
//        view.setText("专栏界面");
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);
//        //添加布局
//        fl_content.addView(view);
//
//        //修改标题
//        tv_title.setText("专栏");
//
//        //隐藏菜单按钮
//        ib_menu.setVisibility(View.VISIBLE);



        //修改标题
        tv_title.setText("新闻");
        //显示菜单按钮
        ib_menu.setVisibility(View.VISIBLE);
        //获取侧边栏
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();
        //初始化4个菜单详情页
        mMenuDetailPagers = new ArrayList<>();
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new NewsMenuDetailPager1(mActivity));
        //将新闻菜单详情页设置为首页面
        setCurrentDetailPager(0);
    }

    public void setCurrentDetailPager(int position) {

        //重新给FrameLayout添加布局
        BaseMenuDetailPager pager = mMenuDetailPagers.get(position);
        //添加当前应该显示的页面
        View view = pager.mRootView;
        //清除之前布局
        fl_content.removeAllViews();
        fl_content.addView(view);

        //初始化页面数据
        pager.initData();
        //更新标题
        mTabNames = UIUtils.getStringArray(R.array.menu_names);
        tv_title.setText(mTabNames[position]);
    }
}
