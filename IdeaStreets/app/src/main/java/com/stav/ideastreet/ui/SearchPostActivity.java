package com.stav.ideastreet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.stav.ideastreet.R;
import com.stav.ideastreet.adapter.MAdapter;
import com.stav.ideastreet.adapter.OnRecyclerViewListener;
import com.stav.ideastreet.adapter.SearchUserAdapter;
import com.stav.ideastreet.base.ParentWithNaviActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.fragment.MainFragment;
import com.stav.ideastreet.model.BaseModel;
import com.stav.ideastreet.model.UserModel;
import com.stav.ideastreet.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

import static com.stav.ideastreet.base.BaseApplication.showToast;
import static com.stav.ideastreet.ui.SearchPostActivity.RefreshType.*;

public class SearchPostActivity extends ParentWithNaviActivity implements View.OnClickListener {

    private EditText et_find_name;
    private Button btn_search;
    private int pageNum;
    private String lastItemTime;

    protected ArrayList<Post> mListItems;
    private PullToRefreshListView mPullRefreshListView;
    private MAdapter mAdapter;
    private ListView actualListView;
    static List<Post> weibos = new ArrayList<>();
    private TextView networkTips;
    private ProgressBar progressbar;
    private boolean pullFromUser;
    public enum RefreshType{
        REFRESH,LOAD_MORE
    }
    private RefreshType mRefreshType = RefreshType.LOAD_MORE;
    @Override
    protected String title() {
        return "搜索创意";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);
        //初始化标题栏
        initNaviView();
        //初始化UI
        initUI();

        pageNum = 0;
        lastItemTime = getCurrentTime();

        mPullRefreshListView = (PullToRefreshListView)
                findViewById(R.id.pull_refresh_list);
        networkTips = (TextView) findViewById(R.id.networkTips);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

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
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                onListItemClick(parent, view, position, id);
            }
        });

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
                        if(mRefreshType== RefreshType.REFRESH){
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

    public void onListItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
        // TODO Auto-generated method stub
//		MyApplication.getInstance().setCurrentQiangYu(mListItems.get(position-1));
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CommentListActivity.class);
        intent.putExtra("data", mListItems.get(position-1));
        startActivity(intent);
    }

    private String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = formatter.format(new Date(System.currentTimeMillis()));
        return times;
    }

    private void initUI() {
        et_find_name = (EditText) findViewById(R.id.et_find_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
    }


    public void query() {
        final String post = et_find_name.getText().toString();
        if (TextUtils.isEmpty(post)) {
            toast("请填写用户名");
            return;
        }
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("selector",post);
//        query.addWhereMatches("content",post);
        query.order("-updatedAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    Log.i("stav", "find success." + list.size());
                    if (list.size() != 0 && list.get(list.size() - 1) != null) {
                        if (mRefreshType == RefreshType.REFRESH) {
                            mListItems.clear();
                        }
                        if (list.size() < Constant.NUMBERS_PER_PAGE) {
                            showToast("已加载完所有数据~");
                        }
                        weibos = list;
                        mListItems.addAll(list);
                        mAdapter.notifyDataSetChanged();

                        Log.i("stav", "DD" + (mListItems.get(mListItems.size() - 1) == null));
                        setState(LOADING_COMPLETED);
                        mPullRefreshListView.onRefreshComplete();
                    } else {
                        showToast("暂无更多数据~");
                        if (list.size() == 0 && mListItems.size() == 0) {

                            networkTips.setText("没有与"+post+"相关的创意~");
                            setState(LOADING_FAILED);
                            pageNum--;
                            mPullRefreshListView.onRefreshComplete();

                            Log.i("stav", "SIZE:" + list.size() + "ssssize" + mListItems.size());
                            return;
                        }
                        pageNum--;
                        setState(LOADING_COMPLETED);
                        mPullRefreshListView.onRefreshComplete();
                    }
                } else {
                    Log.i("stav", "find failed." + e);
                    pageNum--;
                    setState(LOADING_FAILED);
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                query();
                break;
            default:
                break;
        }

    }

    public MAdapter getAdapter() {
        // TODO Auto-generated method stub
        return new MAdapter(getApplicationContext(), mListItems);
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
}

