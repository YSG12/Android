package com.stav.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取手机的经纬度坐标
        //1.获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.以最优的方式获取经纬度（）
        Criteria criteria = new Criteria();
        //允许花费
        criteria.setCostAllowed(true);
        //指定获取经纬度的精确度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        //3.在一定时间间隔，移动一定距离后获取经纬度坐标
        MyLocationListener myLocationListener = new MyLocationListener();

        lm.requestLocationUpdates(bestProvider, 2000, 1, myLocationListener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            //4.发送短信
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("15555218135",null,"Longitude="+longitude+" ,Latitude="+latitude,null,null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //gps状态发生切换的事件监听
        }

        @Override
        public void onProviderEnabled(String provider) {

            //gps开启的时候的事件监听
        }

        @Override
        public void onProviderDisabled(String provider) {
            //gps关闭的时候的监听事件

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
