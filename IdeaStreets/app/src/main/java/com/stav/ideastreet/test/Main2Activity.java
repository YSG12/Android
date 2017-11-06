package com.stav.ideastreet.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.stav.ideastreet.R;
import com.stav.ideastreet.base.AppContext;
import com.stav.ideastreet.base.BaseActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.ui.empty.EmptyLayout;
import com.stav.ideastreet.utils.XmlUtils;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.File;

public class Main2Activity extends ActionBarActivity {


    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;

    private ImageView mUserFace;

    private TextView mName, mJoinTime, mFrom, mPlatFrom, mFocus;


    private EmptyLayout mErrorLayout;

    private MyUser mUser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customActionbar();
        setContentView(R.layout.fragment_my_information_detail);

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

}