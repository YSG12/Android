package com.stav.zhbj.view;

import android.content.Context;
import java.text.SimpleDateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stav.zhbj.R;

import java.util.Date;

/**
 * 下拉刷新的listView
 * Created by Administrator on 2017/7/23.
 */

public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_PULL_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;
    private View mHeaderView;
    private int mHeaderViewHeight;
    private int startY = -1;
    private int mCurrentState = STATE_PULL_TO_REFRESH;  //当前的状态
    private TextView tv_title;
    private TextView tv_time;
    private ProgressBar pb_loading;
    private ImageView iv_arrow;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private View mFooterView;
    private int mFooterViewHeight;

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_footer, null);
        this.addFooterView(mFooterView);
        //隐藏脚布局
        mFooterView.measure(0,0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0,-mFooterViewHeight,0,0);

        this.setOnScrollListener(this); //设置滑动监听
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {

        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh, null);
        this.addHeaderView(mHeaderView);
        tv_title = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tv_time = (TextView) mHeaderView.findViewById(R.id.tv_time);
        iv_arrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pb_loading = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);

        //隐藏头布局
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
        //初始化箭头的动画
        initAnim();
        setCurrentTime();   //设置上一次刷新时间
    }

    //设置刷新时间
    public void setCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        tv_time.setText("最后刷新时间："+time);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) { //当用户按住新闻头条ViewPager时，ACTION_DOWN会被ViewPager消费掉，导致startY没有赋值，此处需要重新获取一下
                    startY = (int) ev.getY();
                }
                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;
                int firstVisiblePosition = getFirstVisiblePosition();   //当前显示第一个item的位置
                //必须下拉，并且显示的是第一个item
                if (dy > 0 && firstVisiblePosition == 0) {
                    int padding = dy - mHeaderViewHeight;   //计算当前下拉布局控件的padding
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && mCurrentState != STATE_RELEASE_PULL_TO_REFRESH) {
                        //改为松开状态
                        mCurrentState = STATE_RELEASE_PULL_TO_REFRESH;
                        refreshData();
                    } else if (padding < 0 && mCurrentState == STATE_PULL_TO_REFRESH) {
                        //改为下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshData();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (mCurrentState == STATE_RELEASE_PULL_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshData();
                    //完整展示头布局
                    mHeaderView.setPadding(0,0,0,0);
                    //4.进行回调
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    //隐藏头布局
                    mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void initAnim() {
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    /**
     * 根据当前状态刷新界面
     */
    private void refreshData() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tv_title.setText("下拉刷新");
                pb_loading.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                iv_arrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_PULL_TO_REFRESH:
                tv_title.setText("松开刷新");
                pb_loading.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                iv_arrow.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tv_title.setText("正在刷新...");
                iv_arrow.clearAnimation();  //清除箭头动画，否则无法隐藏
                pb_loading.setVisibility(View.VISIBLE);
                iv_arrow.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    //刷新结束，收起控件
    public void onRefreshComplete(boolean success) {
        if (!isLoadingMore) {
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
            mCurrentState = STATE_PULL_TO_REFRESH;
            tv_title.setText("下拉刷新");
            pb_loading.setVisibility(View.INVISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            if (success) {
                setCurrentTime();   //设置上一次刷新时间
            }
        }else {
            //加载更多
            mFooterView.setPadding(0,-mFooterViewHeight,0,0);   //隐藏掉加载更多
            isLoadingMore = false;  //加载结束
        }
    }

    //3.定义成员变量，接受监听对象
    private OnRefreshListener mListener;

    /**
     * 2.暴露接口，设置监听
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public boolean isLoadingMore;

    //滑动状态发生变化
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE){  //空闲状态
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition == getCount()-1 && !isLoadingMore) { //当前显示最后一个item，并且没有正在加载更多
                //到底了
                System.out.println("加载更多、、、");
                isLoadingMore = true;
                //显示加载更多布局
                mFooterView.setPadding(0,0,0,0);
                setSelection(getCount()-1); //将listView的位置设置在最后一个item上，从而加载更多会直接展示出来，无需手动滑动

                //通知主界面加载下一页
                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    //滑动过程回调
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 1.下拉刷新的回调借口
     */
    public interface OnRefreshListener {
        void onRefresh();
        //加载更多
        void onLoadMore();
    }


}
