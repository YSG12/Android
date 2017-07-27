package com.stav.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;

public class SetupOverActivity extends AppCompatActivity {

    private TextView tv_phone;
    private TextView tv_reset_setup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置完成为true 还没设置则为false
        boolean setup_over = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SETUP_OVER, false);

        if (setup_over) {
            //密码输入成功，并且四个导航界面设置完成， 停留在设置完成更能列表界面
            setContentView(R.layout.activity_setup_over);
            initUI();
        } else {
            //密码输入成功，四个导航界面没有设置完成，跳转到导航界面第一个界面
            Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
            startActivity(intent);
            //开启一个新的界面以后，关闭功能列表界面
            finish();
        }

    }

//    初始化UI
    private void initUI() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        //设置联系人号码
        String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        tv_phone.setText(phone);
        tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
