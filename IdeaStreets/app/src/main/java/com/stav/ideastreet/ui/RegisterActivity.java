package com.stav.ideastreet.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobSmsState;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.stav.ideastreet.base.BaseApplication.showToast;

public class RegisterActivity extends Activity implements View.OnClickListener {


    private EditText etUsername, etPassword, etUserPhone,etSmsCode;
    private Button btSmsCode, btRegister;
    private CompositeSubscription mCompositeSubscription;
    private String mUsername,mPassword,mUserPhone,mSmsCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etUserPhone = (EditText) findViewById(R.id.et_user_phone);
        etSmsCode = (EditText) findViewById(R.id.et_sms_code);
        btSmsCode = (Button) findViewById(R.id.bt_sms_code);
        btRegister = (Button) findViewById(R.id.bt_register);

        btSmsCode.setOnClickListener(this);
        btRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_sms_code:
                requestSmsCode();
                break;
            case R.id.bt_register:
                verifySmsCode();
                SignUp();
                break;
            default:
                break;
        }
    }

    @SuppressLint("UseValueOf")
    private void SignUp() {
        mUsername = etUsername.getText().toString();
        mPassword = etPassword.getText().toString();
        mUserPhone = etUserPhone.getText().toString();
        mSmsCode = etSmsCode.getText().toString();
        final MyUser myUser = new MyUser();
        myUser.setUsername(mUsername);
        myUser.setPassword(mPassword);
        myUser.setMobilePhoneNumber(mUserPhone);
//        myUser.setAge(18);
        addSubscription(myUser.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser s, BmobException e) {
                if (e == null) {
                    if (mSmsCode != null){
                        showToast("请输入短信验证码~");
                    } else {
                        showToast("恭喜您，注册成功~");
                        finish();
                    }
                } else {
                    Log.e("", String.valueOf(e));
                }
            }
        }));
    }


    /** 自定义发送短信内容
     * @method requestSmsCode
     * @return void
     * @exception
     */
    private void requestSms(){
        String number = etUserPhone.getText().toString();

        if(!TextUtils.isEmpty(number)){
            SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sendTime = format.format(new Date());
            BmobSMS.requestSMS(number, "您的验证码为123456，请及时验证！",sendTime,new QueryListener<Integer>() {

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

    /** 请求短信验证码
     * @method requestSmsCode
     * @return void
     * @exception
     */
    private void requestSmsCode(){
        String number = etUserPhone.getText().toString();

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

    /** 验证短信验证码
     * @method requestSmsCode
     * @return void
     * @exception
     */
    private void verifySmsCode(){
        String number = etUserPhone.getText().toString();
        String code = etSmsCode.getText().toString();
        if(!TextUtils.isEmpty(number)&&!TextUtils.isEmpty(code)){
            BmobSMS.verifySmsCode(number,code, new UpdateListener() {

                @Override
                public void done(BmobException ex) {
                    if(ex==null){//短信验证码已验证成功
                        showToast("验证通过");
                        finish();
                    }else{
                        showToast("验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    }
                }
            });
        }else{
            showToast("请输入手机号和验证码");
        }
    }

    /** 查询短信状态
     * @method querySmsState
     * @return void
     * @exception
     */
    private void querySmsState(){
        BmobSMS.querySmsState(39086233, new QueryListener<BmobSmsState>() {

            @Override
            public void done(BmobSmsState state, BmobException ex) {
                if(ex==null){
                    showToast("短信状态："+state.getSmsState()+",验证状态："+state.getVerifyState());
                }else{
                    showToast("errorCode = "+ex.getErrorCode()+",errorMsg = "+ex.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * 解决Subscription内存泄露问题
     * @param s
     */
    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

}
