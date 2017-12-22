package com.stav.ideastreet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.stav.ideastreet.R;
import com.stav.ideastreet.adapter.OnRecyclerViewListener;
import com.stav.ideastreet.adapter.SearchUserAdapter;
import com.stav.ideastreet.base.ParentWithNaviActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.model.BaseModel;
import com.stav.ideastreet.model.UserModel;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchPostActivity extends ParentWithNaviActivity implements View.OnClickListener {

    EditText et_find_name;
    Button btn_search;

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
    }

    private void initUI() {
        et_find_name = (EditText) findViewById(R.id.et_find_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
    }


    public void query() {
        String post = et_find_name.getText().toString();
        if (TextUtils.isEmpty(post)) {
            toast("请填写用户名");
            return;
        }

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
}

