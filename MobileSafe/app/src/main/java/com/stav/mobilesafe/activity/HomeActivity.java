package com.stav.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.Md5Util;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.ToastUtil;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class HomeActivity extends AppCompatActivity {
    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrawable;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 实例化广告条
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        // 获取要嵌入广告条的布局
        LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
        // 将广告条加入到布局中
        adLayout.addView(adView);

        //初始化UI
        initUI();
        //初始化数据
        initData();
    }


    private void initData() {
        //准备数据（文字（9）和图片（9））
        mTitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量管理", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };

        mDrawable = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };

        //九宫格设置数据适配器（等同于listView数据适配器）
        gv_home.setAdapter(new MyAdapter());

        //注册九宫格单个条目的点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             *
             * @param parent
             * @param view
             * @param position 条目的索引
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //开启对话框
                        showDialog();
                        break;
                    case 1:
                        //跳转到通信卫士模块
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));

                        break;
                    case 2:

                        //跳转到应用管理
                        startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 3:

                        //跳转到进程管理
                        startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(),TrafficActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(),BaseCacheClearActivity.class));
                        break;
                    case 7:
                        //跳转到高级工具功能列表界面
                        startActivity(new Intent(getApplicationContext(),AToolsActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断是否存储密码（sp 字符串）
        String pwd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PWD, "");
        if (TextUtils.isEmpty(pwd)) {
            //1.初始设置密码对话框
            showSetPwdDialog();
        } else {
            //2.确认密码对话框
            showConfirmPwdDialog();
        }
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPwdDialog() {
        //因为需要自己去定义对话框要展示的样式，所以需要调用dialog.setView(view)
        //view是由自己编写的xml转换成的view对象 ---.view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        view = View.inflate(this, R.layout.dialog_confirm_pwd, null);
        //让一个对话框显示一个自己定义的对话框界面效果
        dialog.setView(view);
        //        兼容低版本 屏蔽边距
//        dialog.setView(view,0,0,0,0);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        /**
         * 点击确认按钮保存密码
         */
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_pwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
                String confirmPwd = et_confirm_pwd.getText().toString();
                if ( !TextUtils.isEmpty(confirmPwd)) {
                    //将存储在sp中32位的密码，获取出来 然后将输入的密码同样进行md5，然后与sp中储存密码匹配
                    String pwd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD, "");
                    if (pwd.equals(Md5Util.encoder(confirmPwd))) {
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //跳转到新的洁面之后需要隐藏对话框
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(),"确认密码有误");
                    }

                } else {
                    //提示用户密码输入为空的情况
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }

            }
        });

        /**
         * 点击取消按钮让对话框消失
         */
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    /**
     * 初次设置密码对话框
     */
    private void showSetPwdDialog() {
        //因为需要自己去定义对话框要展示的样式，所以需要调用dialog.setView(view)
        //view是由自己编写的xml转换成的view对象 ---.view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        view = View.inflate(this, R.layout.dialog_set_pwd, null);
        //让一个对话框显示一个自己定义的对话框界面效果
        dialog.setView(view);
//        兼容低版本 屏蔽边距
//        dialog.setView(view,0,0,0,0);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        /**
         * 点击确认按钮保存密码
         */
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_pwd = (EditText) view.findViewById(R.id.et_set_pwd);
                EditText et_confirm_pwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
                String pwd = et_set_pwd.getText().toString();
                String confirmPwd = et_confirm_pwd.getText().toString();

                if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(confirmPwd)) {
                    //进入手机防盗模块
                    if (pwd.equals(confirmPwd)) {
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //跳转到新的洁面之后需要隐藏对话框
                        dialog.dismiss();
                        SpUtil.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PWD, Md5Util.encoder(confirmPwd));
                    } else {
                        ToastUtil.show(getApplicationContext(),"确认密码有误");
                    }

                } else {
                    //提示用户密码输入为空的情况
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }

            }
        });

        /**
         * 点击取消按钮让对话框消失
         */
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 初始化UI
     */
    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    /**
     * 给GritView添加数据
     */
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            //条目的总数
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(getApplicationContext(), R.layout.gridtview_item, null);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText(mTitleStr[position]);
            iv_icon.setBackgroundResource(mDrawable[position]);
            return view;
        }
    }
}
