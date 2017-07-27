package com.stav.mobilesafe.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.stav.mobilesafe.R;

public class BaseCacheClearActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache_clear);
        //1.生成选项卡1
//        View view = View.inflate(this, R.layout.test, null);
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        //2.生成选项卡2
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("sd_card_cache").setIndicator("sd卡清理");
        //3.告知点中选项卡后续操作
        tab1.setContent(new Intent(this,CacheClearActivity.class));
        tab2.setContent(new Intent(this,CacheClearActivity.class));
        //4.将此两个选项卡维护host（选项卡宿主）中去
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }
}
