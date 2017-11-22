package com.stav.ideastreet.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import com.stav.ideastreet.R;
import com.stav.ideastreet.adapter.OnRecyclerViewListener;
import com.stav.ideastreet.adapter.SearchUserAdapter;
import com.stav.ideastreet.base.ParentWithNaviActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.model.BaseModel;
import com.stav.ideastreet.model.UserModel;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 搜索用户
 *
 * @author :smile
 * @project:SearchUserActivity
 * @date :2016-01-25-18:23
 */
public class SearchUserActivity extends ParentWithNaviActivity implements View.OnClickListener {

    EditText et_find_name;
    SwipeRefreshLayout sw_refresh;
    Button btn_search;
    RecyclerView rc_view;
    LinearLayoutManager layoutManager;
    SearchUserAdapter adapter;

    @Override
    protected String title() {
        return "搜索用户";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        //初始化标题栏
        initNaviView();
        //初始化UI
        initUI();
        adapter = new SearchUserAdapter();
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rc_view.setAdapter(adapter);
        rc_view.setLayoutManager(layoutManager);
        sw_refresh.setEnabled(true);
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                MyUser user = adapter.getItem(position);
                bundle.putSerializable("u", user);
                startActivity(UserInfoActivity.class, bundle, false);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return true;
            }
        });
    }

    private void initUI() {
        rc_view = (RecyclerView) findViewById(R.id.rc_view);
        sw_refresh = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        et_find_name = (EditText) findViewById(R.id.et_find_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
    }


    public void query() {
        String name = et_find_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("请填写用户名");
            sw_refresh.setRefreshing(false);
            return;
        }
        UserModel.getInstance().queryUsers(name, BaseModel.DEFAULT_LIMIT,
                new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        if (e == null) {
                            sw_refresh.setRefreshing(false);
                            adapter.setDatas(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            sw_refresh.setRefreshing(false);
                            adapter.setDatas(null);
                            adapter.notifyDataSetChanged();
                            Logger.e(e);
                        }
                    }
                }


        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                sw_refresh.setRefreshing(true);
                query();
                break;
            default:
                break;
        }

    }
}
