package com.stav.ideastreet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stav.ideastreet.R;
import com.stav.ideastreet.fragment.DFragment;
import com.stav.ideastreet.utils.ConstantValue;
import com.stav.ideastreet.utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import rx.Subscriber;

import static com.stav.ideastreet.base.BaseApplication.showToast;

public class LoginActivity extends Activity implements View.OnClickListener {
    private Button btLogin,btRegister;
    private EditText etUsername,etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化UI
        initUI();

    }

    private void initUI() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btLogin=(Button) findViewById(R.id.bt_login);
        btRegister=(Button) findViewById(R.id.bt_register);

        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                login();
                break;
            case R.id.bt_register:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 注意下如果返回206错误 一般是多设备登录导致
     */
    private void login() {
        final BmobUser user = new BmobUser();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        user.setUsername(username);
        user.setPassword(password);
        //login回调
        /*user.login(new SaveListener<BmobUser>() {

			@Override
			public void done(BmobUser bmobUser, BmobException e) {
				if(e==null){
					toast(user.getUsername() + "登陆成功");
					testGetCurrentUser();
				}else{
					loge(e);
				}
			}
		});*/
        //v3.5.0开始新增加的rx风格的Api
        user.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
            @Override
            public void onCompleted() {
                Log.e("","----onCompleted----");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("", String.valueOf(new BmobException(e)));

                showToast("请输入正确的用户名和密码~");
            }

            @Override
            public void onNext(BmobUser bmobUser) {
                showToast(bmobUser.getUsername() + "登陆成功");
                testGetCurrentUser();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 获取本地用户
     */
    private void testGetCurrentUser() {
//		MyUser myUser = BmobUser.getCurrentUser(this, MyUser.class);
//		if (myUser != null) {
//			log("本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
//					+ ",age = "+ myUser.getAge());
//		} else {
//			toast("本地用户为null,请登录。");
//		}
        //V3.4.5版本新增加getObjectByKey方法获取本地用户对象中某一列的值
        String username = (String) BmobUser.getObjectByKey("username");
        Integer age = (Integer) BmobUser.getObjectByKey("age");
        Boolean sex = (Boolean) BmobUser.getObjectByKey("sex");
        JSONArray hobby = (JSONArray) BmobUser.getObjectByKey("hobby");
        JSONArray cards = (JSONArray) BmobUser.getObjectByKey("cards");
        JSONObject banker = (JSONObject) BmobUser.getObjectByKey("banker");
        JSONObject mainCard = (JSONObject) BmobUser.getObjectByKey("mainCard");
        Log.e("", "username：" + username + ",\nage：" + age + ",\nsex：" + sex);
        Log.e("", "hobby:" + (hobby != null ? hobby.toString() : "为null") + "\ncards:" + (cards != null ? cards.toString() : "为null"));
        Log.e("", "banker:" + (banker != null ? banker.toString() : "为null") + "\nmainCard:" + (mainCard != null ? mainCard.toString() : "为null"));
    }

}
