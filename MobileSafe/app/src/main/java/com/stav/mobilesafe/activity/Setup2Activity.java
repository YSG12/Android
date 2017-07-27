package com.stav.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.ToastUtil;
import com.stav.mobilesafe.view.SettingItemView;

import org.w3c.dom.Text;

public class Setup2Activity extends AppCompatActivity {

    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        //初始化UI
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        //1.回显 读取已有的绑定状态，只用作显示 sp中是否储存了sim卡的序列号
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, null);
        //2.判断是否序列号为""
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }

        /**
         * 给siv_sim_bound设置点击事件
         */
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3.获取原有的状态（）
                boolean isCheck = siv_sim_bound.isCheck();
                //4.将原有状态取反，状态设置给当前条目
                siv_sim_bound.setCheck(!isCheck);
                if (!isCheck) {
                    //5.储存（序列卡号）
                    //5.1获取sim卡序列号TelephoneManager
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    //5.2获取sim卡的序列号
                    String simSerialNumber = manager.getSimSerialNumber();
                    //5.3储存
                    SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                } else {
                    //6.将存储序列卡号的节点，从sp中删掉
                    SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }

            }
        });
    }

    /**
     * 点击跳转下一页
     * @param v
     */
    public void nextPage(View v) {
        String serialNumber = SpUtil.getString(this,ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(serialNumber)) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            //跳转之后关闭该页面
            finish();

            //开启平移动画
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        } else {
            ToastUtil.show(this,"请绑定sim卡");
        }


    }

    /**
     * 点击跳转上一页
     * @param v
     */
    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        //开启平移动画
        overridePendingTransition(R.anim.pre_out_anim,R.anim.pre_in_anim);

    }
}