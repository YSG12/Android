package com.stav.mobilesafe.activity;

import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stav.mobilesafe.R;

public class TrafficActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        //获取流量（R 手机下载流量）
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //获取流量（T 手机总流量 （上传和下载））
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        //总流量（R 手机下载流量总和）
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        //总流量（T 手机总流量 （上传和下载））
        long totalTxBytes = TrafficStats.getTotalTxBytes();



    }


}
