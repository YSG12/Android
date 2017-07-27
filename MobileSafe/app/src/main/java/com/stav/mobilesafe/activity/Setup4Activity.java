package com.stav.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.ToastUtil;

public class Setup4Activity extends AppCompatActivity {

    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        cb_box = (CheckBox) findViewById(R.id.cb_box);
        //1.是否选中状态的回显过程
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cb_box.setChecked(open_security); //回显
        //2.根据状态，修改checkbox后续的文字显示修改
        if (open_security) {
            cb_box.setText("安全设置已开启");
        }else {
            cb_box.setText("安全设置已关闭");
        }
        //3.点击过程中，监听选中状态发生改变的过程
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //4.isChecked点击后的状态 储存点击后状态的储存
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                //根据开启关闭状态，去修改显示的文字
                if (isChecked){
                    cb_box.setText("安全设置已开启");
                } else {
                    cb_box.setText("安全设置已关闭");
                }
            }
        });

      }

    /**
     * 点击跳转下一页
     * @param v
     */
    public void nextPage(View v) {
        boolean open_security = SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);

            //跳转之后关闭该页面
            finish();

            //开启平移动画
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

            //将设置结果保存到SpUtil中
            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
        } else {
            ToastUtil.show(this,"请开启防盗保护");
        }


    }

    /**
     * 点击跳转上一页
     * @param v
     */
    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);

        //开启平移动画
        overridePendingTransition(R.anim.pre_out_anim,R.anim.pre_in_anim);

    }
}
