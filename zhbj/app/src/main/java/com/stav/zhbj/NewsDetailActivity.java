package com.stav.zhbj;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import static android.view.View.VISIBLE;

public class NewsDetailActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.ll_control)
    private LinearLayout ll_control;
    @ViewInject(R.id.ib_back)
    private ImageButton ib_back;
    @ViewInject(R.id.ib_textsize)
    private ImageButton ib_textsize;
    @ViewInject(R.id.ib_share)
    private ImageButton ib_share;
    @ViewInject(R.id.ib_menu)
    private ImageButton ib_menu;
    @ViewInject(R.id.wv_news_detail)
    private WebView wv_news_detail;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ViewUtils.inject(this);

        ll_control.setVisibility(VISIBLE);
        ib_back.setVisibility(VISIBLE);
        ib_menu.setVisibility(View.GONE);

        ib_back.setOnClickListener(this);
        ib_share.setOnClickListener(this);
        ib_textsize.setOnClickListener(this);

        mUrl = getIntent().getStringExtra("url");

//        wv_news_detail.loadUrl("//www.baidu.com");
        wv_news_detail.loadUrl(mUrl);
        WebSettings settings = wv_news_detail.getSettings();
        settings.setBuiltInZoomControls(true);  //显示缩放按钮(wap网页不支持)
        settings.setUseWideViewPort(true);  //支持双击缩放(wap网页不支持)
        settings.setJavaScriptEnabled(true);    //支持js功能
        wv_news_detail.setWebViewClient(new WebViewClient() {
            //开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("开始加载网页了");
                pb_loading.setVisibility(VISIBLE);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pb_loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("网页加载结束了。。。");
                pb_loading.setVisibility(View.INVISIBLE);
            }

            //所有链接的跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("跳转的链接"+url);
                view.loadUrl(url);  //在跳转页面时强制在当前WebView中加载
                return true;
            }
        });
//        wv_news_detail.goBack();    //跳转到上一个页面
//        wv_news_detail.goForward(); //跳转到洗一个页面
        wv_news_detail.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //进度条发生改变
                System.out.println("当前的进度"+newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                //网页的标题
                System.out.println("网页的标题"+title);
                super.onReceivedTitle(view, title);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();   //结束当前页面
                break;
            case R.id.ib_share:
                showShare();
                break;
            case R.id.ib_textsize:
                showChooseDialog();
                break;
            default:
                break;
        }
    }

    private int mTempWhich; //记录临时选择的字体大小（点击确定之前）
    private int mCurrentWhich = 2; //记录当前选择的字体大小（点击确定之后）
    /**
     * 显示字体大小的弹窗
     */
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("z字体设置");
        String[] items = new String[]{"超大号字体","大号字体","正常字体","小号字体","超小号字体"};
        builder.setSingleChoiceItems(items, mCurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich = which;
            }
        });
        //确定事件
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //根据选择的提提来修改网页字体大小
                WebSettings settings = wv_news_detail.getSettings();
                switch (mTempWhich) {
                    case 0:
                        //超大号字体
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
                        break;
                    case 1:
                        //大号字体
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        //正常字体
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        //小号字体
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4:
                        //超小号字体
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                    default:
                        break;
                }
                mCurrentWhich = mTempWhich;
            }
        });
        //取消事件
        builder.setNegativeButton("取消", null);
        builder.show(); //展示弹窗
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
