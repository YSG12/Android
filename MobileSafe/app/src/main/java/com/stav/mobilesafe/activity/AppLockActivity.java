package com.stav.mobilesafe.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.db.dao.AppLockDao;
import com.stav.mobilesafe.db.domain.AppInfo;
import com.stav.mobilesafe.engine.AppInfopProvider;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {
    private Button bt_unlock,bt_lock;
    private TextView tv_unlock,tv_lock;
    private ListView lv_unlock,lv_lock;
    private LinearLayout ll_unlock,ll_lock;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mLockList,mUnLockList;
    private AppLockDao mDao;
    private MyAdapter mLockAdapter;
    private MyAdapter mUnLockAdapter;
    private TranslateAnimation mTranslateAnimation;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //6.接收到消息，填充已加锁和未加锁数据适配器
            mLockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mLockAdapter);

            mUnLockAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(mUnLockAdapter);
        }
    };

    class MyAdapter extends BaseAdapter {
        private boolean isLock;

        /**
         * @param isLock 用于区分已加锁和未加锁应用的标识
         */
        public MyAdapter(boolean isLock) {
            this.isLock = isLock;

        }
        @Override
        public int getCount() {
            if (isLock) {
                tv_lock.setText("已加锁应用:"+mLockList.size()+"个");
                return mLockList.size();
            } else {
                tv_unlock.setText("未加锁应用:"+mUnLockList.size()+"个");
                return mUnLockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockList.get(position);
            } else {
                return mUnLockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final AppInfo appInfo = getItem(position);
            final View animationView =convertView;
            holder.iv_icon.setBackgroundDrawable(appInfo.icon);
            holder.tv_name.setText(appInfo.name);
            if (isLock) {
                holder.iv_lock.setBackgroundResource(R.drawable.lock);
            } else {
                holder.iv_lock.setBackgroundResource(R.drawable.unlock);
            }
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加动画效果
                    animationView.startAnimation(mTranslateAnimation);
                    //对动画的执行过程事件监听，要等到动画执行完成后，在执行其他操作
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //动画执行开始时调用的方法

                        }

                        //动画执行结束后调用的方法
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock) {
                                //已加锁----->未加锁过程
                                //1.已加锁集合删除一个，未加锁集合减少一个，对象就是getItem方法获取的对象
                                mLockList.remove(appInfo);
                                mUnLockList.add(appInfo);
                                //2.从已加锁的数据库中删除一条数据
                                mDao.delete(appInfo.packageName);
                                //3.刷新数据适配器
                                mLockAdapter.notifyDataSetChanged();
                            } else{
                                //已加锁----->未加锁过程

                                //1.已加锁集合删除一个，未加锁集合减少一个，对象就是getItem方法获取的对象
                                mLockList.add(appInfo);
                                mUnLockList.remove(appInfo);
                                //2.从已加锁的数据库中插入删除一条数据
                                mDao.insert(appInfo.packageName);
                                //3.刷新数据适配器
                                mUnLockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            //动画执行重复调用的方法
                        }
                    });

                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
        initData();
        initAnimation();
    }

    /**
     * 初始化平滑动画(平移自身的宽度的大小)
     */
    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
        mTranslateAnimation.setDuration(500);
    }

    /**
     * 区分已加锁和未加锁的方法
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //1.获取所有手机中的应用
                mAppInfoList = AppInfopProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁应用和未加锁应用
                mLockList = new ArrayList<>();
                mUnLockList = new ArrayList<>();
                //3.获取数据库中已加锁应用包名的结合
                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo appInfo : mAppInfoList){
                   if (lockPackageList.contains(appInfo.packageName)) {
                       mLockList.add(appInfo);
                   } else {
                       mUnLockList.add(appInfo);
                   }
                 }
                //5.告知主线程,可以使用维护的数据
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        bt_unlock = (Button) findViewById(R.id.bt_unlock);
        bt_lock = (Button) findViewById(R.id.bt_lock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);

        bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.已加锁列表隐藏，未加锁列表显示
                ll_unlock.setVisibility(View.VISIBLE);
                ll_lock.setVisibility(View.GONE);
                //2.已加锁变成浅色图片，未加锁变成深色图片
                bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                bt_lock.setBackgroundResource(R.drawable.tab_right_default);

            }
        });

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.已加锁列表显示，未加锁列表隐藏
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
                //2.未加锁变成浅色图片，已加锁变成深色图片
                bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
                bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
            }
        });
    }
}
