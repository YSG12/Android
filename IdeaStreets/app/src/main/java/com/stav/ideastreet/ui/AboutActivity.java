package com.stav.ideastreet.ui;

import android.app.Activity;
import android.os.Bundle;

import com.stav.ideastreet.R;
import com.stav.ideastreet.base.ParentWithNaviActivity;

public class AboutActivity extends ParentWithNaviActivity {
    /**
     * 设置actionBar
     * @return
     */
    @Override
    protected String title() {
        return "关于创意街";
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            //退出该页面
            @Override
            public void clickLeft() {
                finish();
            }

            //发表微博
            @Override
            public void clickRight() {
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initNaviView();
    }
}
