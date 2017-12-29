package com.stav.ideastreet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.stav.ideastreet.R;
import com.stav.ideastreet.adapter.BaseContentAdapter;
import com.stav.ideastreet.adapter.MAdapter;
import com.stav.ideastreet.base.ParentWithNaviActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.test.TestActivity;
import com.stav.ideastreet.ui.CommentListActivity;
import com.stav.ideastreet.ui.SearchUserActivity;
import com.stav.ideastreet.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.stav.ideastreet.base.BaseApplication.showToast;


public class MyIdeaActivity extends ParentWithNaviActivity {

    private int pageNum;
    private String lastItemTime;

    protected ArrayList<Post> mListItems;
    private PullToRefreshListView mPullRefreshListView;
    private MAdapter mAdapter;
    private ListView actualListView;

    private TextView networkTips;
    private ProgressBar progressbar;
    private boolean pullFromUser;
    public enum RefreshType{
        REFRESH,LOAD_MORE
    }
    private RefreshType mRefreshType = RefreshType.LOAD_MORE;

    @Override
    protected String title() {
        return "我的收藏";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_publish_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
                startActivity(SearchUserActivity.class,null);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        initNaviView();
        pageNum = 0;
        lastItemTime = getCurrentTime();

        mPullRefreshListView = (PullToRefreshListView)
                findViewById(R.id.pull_refresh_list);
        networkTips = (TextView) findViewById(R.id.networkTips);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                pullFromUser = true;
                mRefreshType = RefreshType.REFRESH;
                pageNum = 0;
                lastItemTime = getCurrentTime();
                fetchData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                mRefreshType = RefreshType.LOAD_MORE;
                fetchData();
            }
        });
        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                // TODO Auto-generated method stub

            }
        });

        actualListView = mPullRefreshListView.getRefreshableView();
        mListItems = new ArrayList<Post>();
        mAdapter = getAdapter();
        actualListView.setAdapter(mAdapter);

        if(mListItems.size() == 0){
            fetchData();
        }
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                onListItemClick(parent, view, position, id);
            }
        });

        LinearLayout ll_mian = (LinearLayout) findViewById(R.id.ll_main);
        ll_mian.setTextDirection(0);
    }

    private String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = formatter.format(new Date(System.currentTimeMillis()));
        return times;
    }

    public void fetchData(){
        setState(LOADING);
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("author", user);	// 查询当前用户的所有微博
        query.order("-updatedAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.setSkip(Constant.NUMBERS_PER_PAGE*(pageNum++));
        query.include("author");

        query.findObjects(new FindListener<Post>() {

            @Override
            public void done(List<Post> list, BmobException e) {

                if (e==null) {
                    Log.i("stav","find success."+list.size());
                    if(list.size()!=0&&list.get(list.size()-1)!=null){
                        if(mRefreshType==RefreshType.REFRESH){
                            mListItems.clear();
                        }
                        if(list.size()< Constant.NUMBERS_PER_PAGE){
                            showToast("已加载完所有数据~");
                        }
                        mListItems.addAll(list);
                        mAdapter.notifyDataSetChanged();

                        Log.i("stav","DD"+(mListItems.get(mListItems.size()-1)==null));
                        setState(LOADING_COMPLETED);
                        mPullRefreshListView.onRefreshComplete();
                    }else{
                        showToast("暂无更多数据~");
                        if(list.size()==0&&mListItems.size()==0){

                            networkTips.setText("暂无创意。快去发表几个把~");
                            setState(LOADING_FAILED);
                            pageNum--;
                            mPullRefreshListView.onRefreshComplete();

                            Log.i("stav","SIZE:"+list.size()+"ssssize"+mListItems.size());
                            return;
                        }
                        pageNum--;
                        setState(LOADING_COMPLETED);
                        mPullRefreshListView.onRefreshComplete();
                    }
                } else {
                    Log.i("stav","find failed."+e);
                    pageNum--;
                    setState(LOADING_FAILED);
                    mPullRefreshListView.onRefreshComplete();
                }

            }
        });

    }

    private static final int LOADING = 1;
    private static final int LOADING_COMPLETED = 2;
    private static final int LOADING_FAILED =3;
    private static final int NORMAL = 4;
    public void setState(int state){
        switch (state) {
            case LOADING:
                if(mListItems.size() == 0){
                    mPullRefreshListView.setVisibility(View.GONE);
                    progressbar.setVisibility(View.VISIBLE);
                }
                networkTips.setVisibility(View.GONE);

                break;
            case LOADING_COMPLETED:
                networkTips.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);

                mPullRefreshListView.setVisibility(View.VISIBLE);
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);


                break;
            case LOADING_FAILED:
                if(mListItems.size()==0){
                    mPullRefreshListView.setVisibility(View.VISIBLE);
                    mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    networkTips.setVisibility(View.VISIBLE);
                }
                progressbar.setVisibility(View.GONE);
                break;
            case NORMAL:

                break;
            default:
                break;
        }
    }

    public void onListItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
        // TODO Auto-generated method stub
//		MyApplication.getInstance().setCurrentQiangYu(mListItems.get(position-1));
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CommentListActivity.class);
        intent.putExtra("data", mListItems.get(position-1));
        startActivity(intent);
    }

    public MAdapter getAdapter() {
        // TODO Auto-generated method stub
        return new MAdapter(getApplicationContext(), mListItems);
    }

}
