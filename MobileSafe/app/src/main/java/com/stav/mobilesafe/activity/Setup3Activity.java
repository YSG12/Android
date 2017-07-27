package com.stav.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends AppCompatActivity {

    private EditText et_phone_number;
    private Button bt_select_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        //初始化UI
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        //显示电话号码的输入框
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        //获取联系人的电话号码回显过程
        String phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        et_phone_number.setText(phone);
        bt_select_number = (Button) findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * 返回到当前界面的时候，接收结果的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone");
            //将特殊字符过滤(中划线转换成空字符串)
            phone = phone.replace("-", "").replace(" ", "").trim();
            et_phone_number.setText(phone);

            //储存联系人至sp中
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 点击跳转下一页
     * @param v
     */
    public void nextPage(View v) {
        //点击按钮以后，需要获取输入框中的联系人，再做下一页操作
        String phone = et_phone_number.getText().toString();

        //在sp储存了相关联系人后才可以跳转到下一页面
//        String contact_phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);

            //跳转之后关闭该页面
            finish();

            //开启平移动画
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

            //如果是输入的代码，则需要保存
            SpUtil.putString(getApplicationContext(),ConstantValue.CONTACT_PHONE,phone);
        }else {
            ToastUtil.show(this,"请输入电话号码");
        }

    }

    /**
     * 点击跳转上一页
     * @param v
     */
    public void prePage(View v) {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        //开启平移动画
        overridePendingTransition(R.anim.pre_out_anim,R.anim.pre_in_anim);

    }

}
