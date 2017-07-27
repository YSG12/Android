package com.stav.zhbj.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.INotificationSideChannel;
import android.support.v4.view.PagerAdapter;

import com.stav.zhbj.NewsDetailActivity;
import com.stav.zhbj.utils.PrefUtils;
import com.stav.zhbj.view.PullToRefreshListView;
import com.stav.zhbj.view.TopNewsViewPager;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stav.zhbj.R;
import com.stav.zhbj.domain.NewsMenu;
import com.stav.zhbj.domain.NewsTabBean;
import com.stav.zhbj.global.GlobalContents;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.stav.zhbj.utils.CacheUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * 页签页对象
 * Created by Administrator on 2017/7/22.
 */

public class TabDetailPager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData mTabData;  //单个页签的网络数据
//    private TextView view;
    @ViewInject(R.id.vp_top_news)
    private TopNewsViewPager vp_top_news;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.lv_news)
    private PullToRefreshListView lv_news;
    @ViewInject(R.id.cpi_indicator)
    private CirclePageIndicator cpi_indicator;
    private final String mUrl;
    private NewsTabBean newsTabBean;
    private ArrayList<NewsTabBean.TopNews> mTopnews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;
    private NewsAdapter mNewsAdapter;
    private String mMoreUrl;    //下一页数据链接
    private Handler mHandler;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
        mUrl = GlobalContents.SERVER_RUL+mTabData.url;
    }

    @Override
    public View initView() {

//        //给帧布局填充布局对象
//        view = new TextView(mActivity);
//        //view.setText(mTabData.title); // 此处空指针异常
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);

        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        ViewUtils.inject(this,view);

        //给listView添加头布局
        final View mHeaderView = View.inflate(mActivity,R.layout.list_item_header,null);
        ViewUtils.inject(this,mHeaderView);     //必须将头布局也注入
        lv_news.addHeaderView(mHeaderView);

        //5.前段界面设置回调
        lv_news.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                //判断是否有下一页数据
                if (mMoreUrl != null) {
                    //有下一页
                    getMoreDataFromServer();
                } else {
                    //没有下一页
                    Toast.makeText(mActivity, "没有数据啦~", Toast.LENGTH_SHORT).show();
                    //收起下拉控件
                    lv_news.onRefreshComplete(true );
                }

            }
        });

        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = lv_news.getHeaderViewsCount();   //获取头布局的数量
                position = position - headerViewsCount;
                System.out.println("第"+position+"个被点击了");
                NewsTabBean.NewsData news = mNewsList.get(position);
                //read_ids：1101,1102,1105
                String readIds = PrefUtils.getString(mActivity,"read_ids","");
                //只有不包含该字符串时，才继续添加
                if (!readIds.contains(news.id+"")){
                    readIds = readIds + news.id + ",";
                    PrefUtils.setString(mActivity,"read_ids",readIds);
                }
                //要将被点击的item的文字颜色改为灰色，局部刷新，view对象就是要点击的对象
                TextView tv_news_title = (TextView) view.findViewById(R.id.tv_news_title);
                tv_news_title.setTextColor(Color.GRAY);

//                mNewsAdapter.notifyDataSetChanged();    //全局刷新，性能比较低

                //跳转到新闻详情页面
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url",news.url);
                mActivity.startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 加载下一页数据
     */
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,true);
                //收起下拉控件
                lv_news.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
                //收起下拉控件
                lv_news.onRefreshComplete(false);
            }
        });
    }


    @Override
    public void initData() {
//        view.setText(mTabData.title);
        String cache = CacheUtils.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache,false);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,false);
                CacheUtils.setCache(mUrl,result,mActivity);
                //收起下拉控件
                lv_news.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
                //收起下拉控件
                lv_news.onRefreshComplete(false);
            }
        });
    }

    private void processData(String result,boolean isMore) {
        Gson gson = new Gson();
        newsTabBean = gson.fromJson(result, NewsTabBean.class);

        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            mMoreUrl = GlobalContents.SERVER_RUL + moreUrl;
        } else {
            mMoreUrl = null;
        }
        if (!isMore) {
            //图片新闻
            mTopnews = newsTabBean.data.topnews;
            if (mTabData!=null) {
                vp_top_news.setAdapter(new TopNewsAdapter());

                cpi_indicator.setViewPager(vp_top_news);
                cpi_indicator.setSnap(true);    //快照方式展示

                //事件设施给Indicator
                cpi_indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        //更新头条新闻的标题
                        String title = mTopnews.get(position).title;
                        tv_title.setText(title);
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                //更新第一个头条新闻标题
                tv_title.setText(mTopnews.get(0).title);
                cpi_indicator.onPageSelected(0);    //默认让第一个选中，解决页面销毁后重新初始化时页面仍然保留上次原点的bug
            }
            //列表新闻
            mNewsList = newsTabBean.data.news;
            if (mNewsList != null) {
                mNewsAdapter = new NewsAdapter();
                lv_news.setAdapter(mNewsAdapter);
            }

            if (mHandler == null) {
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        int currentItem = vp_top_news.getCurrentItem();
                        currentItem++;
                        if (currentItem > mTopnews.size()-1) {
                            currentItem = 0;    //如果已经到了最后一个页面，跳转到第一页
                        }
                        vp_top_news.setCurrentItem(currentItem);
                        mHandler.sendEmptyMessageDelayed(0,2500);   //继续发送延时3s的消息
                    }
                };
                mHandler.sendEmptyMessageDelayed(0,2500);   //发送延时3s的消息
                vp_top_news.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //停止广告自动轮播
                                //删除handler到的所有消息
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_CANCEL: //取消事件，当按下viewpager后，直接滑动listView导致抬起事件，不会响应，则会触发该事件
                                //取消事件
                                mHandler.sendEmptyMessageDelayed(0,2500);
                                break;
                            case MotionEvent.ACTION_UP:
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0,2500);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }

        } else {
            //加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews); //将数据加载到原来的listview中
            //刷新listView
            mNewsAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 头条新闻数据适配器
     */
    class TopNewsAdapter extends PagerAdapter {
        private BitmapUtils mBitmapUtils;

        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.mipmap.topnews_item_default);  //加载中的默认图片
        }

        @Override
        public int getCount() {
            return mTopnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView view = new ImageView(mActivity);
            //view.setImageResource(R.mipmap.topnews_item_default);
            view.setScaleType(ImageView.ScaleType.FIT_XY);  //设置图片缩放方式，宽高填充父控件
            //下载图片->将图片设置给ImageView，避免内存溢出 缓存
            //BitmapUtils -- XUtils
            String imageUrl = mTopnews.get(position).topimage;
            mBitmapUtils.display(view,imageUrl);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
             container.removeView((View) object);
        }
    }

    class NewsAdapter extends BaseAdapter {

        private BitmapUtils mBitmapUtils;

        public NewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.mipmap.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_news_title = (TextView) convertView.findViewById(R.id.tv_news_title);
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            NewsTabBean.NewsData news = getItem(position);
            holder.tv_news_title.setText(news.title);
            holder.tv_date.setText(news.pubdate);

            //根据本地记录来更改已读未读
            String readIds = PrefUtils.getString(mActivity,"read_ids","");
            if (readIds.contains(news.id+"")) {
                holder.tv_news_title.setTextColor(Color.GRAY);
            } else {
                holder.tv_news_title.setTextColor(Color.BLACK);
            }

            mBitmapUtils.display(holder.iv_icon,news.listimage);
            return convertView;
        }
    }
    public static class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_news_title;
        public TextView tv_date ;
    }

}




