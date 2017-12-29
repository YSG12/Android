package com.stav.ideastreet.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smile.filechoose.api.ChooserType;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;
import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.Avatar;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.ui.empty.EmptyLayout;
import com.stav.ideastreet.utils.ConstantValue;
import com.stav.ideastreet.utils.PrefUtils;
import com.stav.ideastreet.widget.CircleImageView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.ProgressCallback;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.stav.ideastreet.base.BaseApplication.showToast;
import static com.stav.ideastreet.utils.TLog.log;

public class UpdateInfoActivity extends ActionBarActivity implements FileChooserListener {


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
    private ImageView ivAvatar;
    private FileChooserManager fm;


    private String motto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customActionbar();
        setContentView(R.layout.fragment_my_information_detail);
        initUI();
        initData();

    }

    public void pickFile() {
        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);
        try {
            fm.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(String arg0) {
        showToast(arg0);
    }

    ChosenFile choosedFile;

    @Override
    public void onFileChosen(final ChosenFile file) {
        choosedFile = file;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("life", choosedFile.getFilePath());
//                showFileDetails(file);
                File avatar = new File(choosedFile.getFilePath());
                uploadMovoieFile(avatar);
            }
        });
    }

    private static String url="";

    ProgressDialog dialog =null;
    /** 上传指定路径下的电影文件
     * @param file
     * @return void
     */
    private void uploadMovoieFile(File file) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final BmobFile bmobFile = new BmobFile(file);
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);
        bmobFile.uploadObservable(new ProgressCallback() {//上传文件操作
            @Override
            public void onProgress(Integer value, long total) {
                log("uploadMovoieFile-->onProgress:"+value);
                dialog.setProgress(value);
            }
        }).doOnNext(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                url = bmobFile.getUrl();
                log("上传成功："+url+","+bmobFile.getFilename());

                Avatar avatar = new Avatar(user.getUsername(), bmobFile);
                final MyUser user = BmobUser.getCurrentUser(MyUser.class);
                user.setIvAvatar(avatar.getFile());
                user.setAvatar(url);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null) {

                        } else {
                            Log.d("stav1",e+"");
                        }
                    }
                });
            }
        }).concatMap(new Func1<Void, Observable<String>>() {//将bmobFile保存到movie表中
            @Override
            public Observable<String> call(Void aVoid) {
                return saveObservable(new Avatar(user.getUsername(),bmobFile));
            }
        }).concatMap(new Func1<String, Observable<String>>() {//下载文件
            @Override
            public Observable<String> call(String s) {
                return bmobFile.downloadObservable(new ProgressCallback() {
                    @Override
                    public void onProgress(Integer value, long total) {
                        log("download-->onProgress:"+value+","+total);

                    }
                });
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                log("--onCompleted--");
            }

            @Override
            public void onError(Throwable e) {
                log("--onError--:"+e.getMessage());
                dialog.dismiss();
                choosedFile=null;
            }

            @Override
            public void onNext(String s) {
                dialog.dismiss();
                choosedFile=null;
                log("download的文件地址："+s);
            }
        });
    }
    /**
     * save的Observable
     * @param obj
     * @return
     */
    private Observable<String> saveObservable(BmobObject obj){
        return obj.saveObservable();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
                fm.setFileChooserListener(this);
            }
            Log.i("TAG", "Probable file size: " + fm.queryProbableFileSize(data.getData(), this));
            fm.submit(requestCode, data);
        }
    }

    //初始化数据
    private void initData() {
        BmobQuery<MyUser> userInfo = new BmobQuery<>();
        addSubscription(userInfo.getObject(mObjectId, new QueryListener<MyUser>() {
                    @Override
                    public void done(final MyUser myUser, BmobException e) {
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
//                                            ivAvatar.setBackgroundResource(R.mipmap.head);
                                            ImageOptions options=new ImageOptions.Builder()
                                                    //设置加载过程中的图片
                                                    .setLoadingDrawableId(R.mipmap.default_head)
                                                    //设置加载失败后的图片
                                                    .setFailureDrawableId(R.mipmap.default_head)
                                                    //设置使用缓存
                                                    .setUseMemCache(true)
                                                    //设置显示圆形图片
                                                    .setCircular(false)
                                                    //设置支持gif
                                                    .setIgnoreGif(false)
                                                    .build();

                                            x.image().bind(ivAvatar, myUser.getAvatar(), options);

                                        }

                                    });
                                }
                            }.start();

                        } else {
                            Log.d("",e+"");
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
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        etNum = (EditText) findViewById(R.id.et_num);
        etSex = (EditText) findViewById(R.id.et_sex);
        etAge = (EditText) findViewById(R.id.et_age);
        etJoin = (EditText) findViewById(R.id.et_join_time);
        etUpdate = (EditText) findViewById(R.id.et_update_time);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataWithOne();
            }
        });
    }

    private void insertDataWithOne(){
        if(choosedFile ==null){
            showToast("请先选择文件");
            pickFile();
            return;
        }
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
                    Log.d("",e+"");
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