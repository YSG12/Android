package com.stav.ideastreet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.stav.ideastreet.base.BaseApplication.showToast;
import static com.stav.ideastreet.utils.TLog.log;

public class SmsLoginActivity extends Activity implements View.OnClickListener {

    private EditText mUserPhone,mSmsCode;
    private Button btSmsCode,btLogin;
    private CompositeSubscription mCompositeSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_login);
        //初始化UI
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        btLogin = (Button) findViewById(R.id.bt_login);
        btSmsCode  = (Button) findViewById(R.id.bt_sms_code);
        mSmsCode = (EditText) findViewById(R.id.et_sms_code);
        mUserPhone = (EditText) findViewById(R.id.et_user_phone);
        btLogin.setOnClickListener(this);
        btSmsCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                //通过短信验证码登录
                loginByPhoneCode();
                break;
            case R.id.bt_sms_code:
                requestSmsCode();
                break;
            default:
                break;
        }
    }

    /**
     * 通过短信验证码登录
     */
    private void loginByPhoneCode() {

        String number = mUserPhone.getText().toString();
        String code = mSmsCode.getText().toString();
        //2、使用验证码进行登陆
        addSubscription(BmobUser.loginBySMSCode(number, code, new LogInListener<MyUser>() {

            @Override
            public void done(MyUser user, BmobException e) {
                if (user != null) {
                    showToast("登录成功");
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    log("" + user.getUsername() + "-" + user.getAge() + "-" + user.getObjectId() + "-" + user.getEmail());
                } else {
                    showToast("错误码：" + e.getErrorCode() + ",错误原因：" + e.getLocalizedMessage());
                }
            }
        }));
    }

    /** 请求短信验证码
     * @method requestSmsCode
     * @return void
     * @exception
     */
    private void requestSmsCode(){
        String number = mUserPhone.getText().toString();

        if(!TextUtils.isEmpty(number)){
            BmobSMS.requestSMSCode(number, "ideastreet",new QueryListener<Integer>() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if(ex==null){//验证码发送成功
                        showToast("验证码发送成功，短信id："+smsId);//用于查询本次短信发送详情
                    }else{
                        showToast("errorCode = "+ex.getErrorCode()+",errorMsg = "+ex.getLocalizedMessage());
                    }
                }
            });
        }else{
            showToast("请输入手机号码");
        }
    }

    /**
     * 解决Subscription内存泄露问题
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }



}
