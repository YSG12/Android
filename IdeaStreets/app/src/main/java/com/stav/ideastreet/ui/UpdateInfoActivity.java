package com.stav.ideastreet.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.ui.empty.EmptyLayout;
import com.stav.ideastreet.utils.ConstantValue;
import com.stav.ideastreet.utils.PrefUtils;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.stav.ideastreet.base.BaseActivity.loge;
import static com.stav.ideastreet.base.BaseApplication.showToast;

public class UpdateInfoActivity extends ActionBarActivity {


    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    private CompositeSubscription mCompositeSubscription;

    private ImageView mUserFace;

    private TextView mName, mJoinTime, mFrom, mPlatFrom, mFocus;


    private EmptyLayout mErrorLayout;

    private boolean isChangeFace = false;

    private String theLarge;

    private final static int CROP = 200;

    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/OSChina/Portrait/";
    private Uri origUri;
    private Uri cropUri;
    private File protraitFile;
    private Bitmap protraitBitmap;
    private String protraitPath;

    private ActionBar mActionBar;
    private TextView mTvActionTitle;
    private EditText etMotto,etUsername,etNum,etSex,etAge,etJoin,etUpdate;
    private MyUser mUser;
    private String mToken,mObjectId;

    private String motto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customActionbar();
        setContentView(R.layout.fragment_my_information_detail);
        initUI();
        initData();

    }

    //初始化数据
    private void initData() {
        BmobQuery<MyUser> userInfo = new BmobQuery<>();
        addSubscription(userInfo.getObject(mObjectId, new QueryListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (e == null) {
                            new Thread() {
                                public void run() {
                                    //这儿是耗时操作，完成之后更新UI；
                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run() {
                                            //更新UI
                                            etUsername.setText(myUser.getUsername());
                                            etMotto.setText(myUser.getMotto());
                                            etSex.setText(myUser.getSex());
                                            etAge.setText(myUser.getAge()+"");
                                            etNum.setText(myUser.getMobilePhoneNumber()+"");
                                            etJoin.setText(myUser.getCreatedAt());
                                            etUpdate.setText(myUser.getUpdatedAt());
                                        }

                                    });
                                }
                            }.start();

                        } else {
                            loge(e);
                        }
                    }
                })
        );
    }

    //初始化UI
    private void initUI() {

        mUser = new MyUser();

        mToken = PrefUtils.getString(getApplicationContext(), ConstantValue.TOKEN, "");
        mObjectId = PrefUtils.getString(getApplicationContext(), ConstantValue.OBJECT_ID, "");
        etMotto = (EditText) findViewById(R.id.et_motto);
        etUsername = (EditText) findViewById(R.id.et_username);
        etNum = (EditText) findViewById(R.id.et_num);
        etSex = (EditText) findViewById(R.id.et_sex);
        etAge = (EditText) findViewById(R.id.et_age);
        etJoin = (EditText) findViewById(R.id.et_join_time);
        etUpdate = (EditText) findViewById(R.id.et_update_time);
    }

    public void modify_infomation (View v) {
        mUser.setObjectId(mObjectId);
        mUser.setSessionToken(mToken);
        String motto = etMotto.getText().toString();
        String sex = etSex.getText().toString();
        Integer age = Integer.valueOf(String.valueOf(etAge.getText()));
        mUser.setMotto(motto);
        mUser.setSex(sex);
        mUser.setAge(age);
        addSubscription(mUser.update(mObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("修改成功！");
                } else {
                    loge(e);
                }
            }
        }));
    }


    private void customActionbar() {
        mActionBar = getSupportActionBar();
        // 设置使用自定义布局
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        View view = View.inflate(this, R.layout.actionbar_custom,
                null);
        View back = view.findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvActionTitle = (TextView) view
                .findViewById(R.id.tv_actionbar_title);
        mTvActionTitle.setText("修改资料");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        mActionBar.setCustomView(view, params);
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