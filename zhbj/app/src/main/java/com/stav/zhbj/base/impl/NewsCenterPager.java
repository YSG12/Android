package com.stav.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stav.zhbj.MainActivity;
import com.stav.zhbj.R;
import com.stav.zhbj.base.BaseMenuDetailPager;
import com.stav.zhbj.base.BasePager;
import com.stav.zhbj.base.menu.InteractMenuDetailPager;
import com.stav.zhbj.base.menu.NewsMenuDetailPager;
import com.stav.zhbj.base.menu.PhotosMenuDetailPager;
import com.stav.zhbj.base.menu.TopicMenuDetailPager;
import com.stav.zhbj.domain.NewsMenu;
import com.stav.zhbj.fragment.LeftMenuFragment;
import com.stav.zhbj.global.GlobalContents;

import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.stav.zhbj.utils.CacheUtils;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * 新闻中心
 * Created by Administrator on 2017/7/21.
 */
public class NewsCenterPager extends BasePager {

    private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;    //菜单详情页
    private NewsMenu mNewsData; //分类信息网络数据

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("新闻中心设置完成");
//        //给帧布局填充布局对象
//        TextView view = new TextView(mActivity);
//        view.setText("新闻中心");
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);
//        //添加布局
//        fl_content.addView(view);
        //修改标题
        tv_title.setText("新闻");
        //显示菜单按钮
        ib_menu.setVisibility(View.VISIBLE);

        //先判断有没有缓存，如果有的话就加载缓存
        String cache = CacheUtils.getCache(GlobalContents.CATEGORY_RUL,mActivity);
        if (!TextUtils.isEmpty(cache)) {
            System.out.println("发现缓存了！~~");
            //请求本地缓存数据
            processData(cache);
        }

            //请求服务器，获取数据
            //开源框架：XUtils
            getDataFromServer();
    }

    /**
     * 添加权限
     * 请求服务器，获取数据
     */
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpMethod.GET, GlobalContents.CATEGORY_RUL, new RequestCallBack<String>() {
            //请求成功
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String result = responseInfo.result;    //获取服务器返回结果
                //JsonObject,Gson
                processData(result);
                System.out.println("服务器返回结果！~~"+result);
                //写缓存
                CacheUtils.setCache(GlobalContents.CATEGORY_RUL,result,mActivity);
            }
            //请求失败
            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 解析数据
     */
    protected void processData(String json) {
        Gson gson = new Gson();
        mNewsData = gson.fromJson(json, NewsMenu.class);
        System.out.print("解析结果："+mNewsData);

        //获取侧边栏
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();
        //给侧边栏添加数据
        fragment.setMenuData(mNewsData.data);
        //初始化4个菜单详情页
        mMenuDetailPagers = new ArrayList<>();
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,mNewsData.data.get(0).children));
        mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity,ib_photo));
        mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));
        //将新闻菜单详情页设置为首页面
        setCurrentDetailPager(0);
    }

    /**
     * 设置菜单详情页
     */
    public void setCurrentDetailPager(int position) {
        //重新给FrameLayout添加内容
        BaseMenuDetailPager pager = mMenuDetailPagers.get(position);    //添加当前应该显示的页面
        View view = pager.mRootView;    //当前页面的布局
        //清除之前的布局
        fl_content.removeAllViews();
        fl_content.addView(view);   //给帧布局添加布局

        //初始化页面数据
        pager.initData();
        //更新标题
        tv_title.setText(mNewsData.data.get(position).title);

        //如果是组图页面，需要显示切换按钮
        if (pager instanceof PhotosMenuDetailPager) {

            ib_photo.setVisibility(View.VISIBLE);
        } else {
            //隐藏切换按钮
            ib_photo.setVisibility(View.GONE);
        }
    }
}
