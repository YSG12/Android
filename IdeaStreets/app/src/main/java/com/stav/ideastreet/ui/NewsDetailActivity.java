package com.stav.ideastreet.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.News;
import com.stav.ideastreet.bean.News.Relative;
import com.stav.ideastreet.bean.NewsDetail;
import com.stav.ideastreet.utils.StringUtils;
import com.stav.ideastreet.utils.UIHelper;
import com.stav.ideastreet.utils.XmlUtils;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;

public class NewsDetailActivity extends ActionBarActivity {
	private ActionBar mActionBar;
	private TextView mTvActionTitle;
	private WebView webView;
	private TextView mTvTitle;
	private TextView mTvSource;
	private TextView mTvTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customActionbar();
		setContentView(R.layout.newsdetail);
//		initWebView();
//		getDataFromServer();
	}
	private void customActionbar() {
		mActionBar = getSupportActionBar();
		// 设置使用自定义布局
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		View view = View.inflate(this, R.layout.actionbar_custom_backtitle,
				null);
		View back = view.findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTvActionTitle = (TextView) view
				.findViewById(R.id.tv_actionbar_title);
		mTvActionTitle.setText("发表文字");

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mActionBar.setCustomView(view, params);
	}

	private void getDataFromServer() {
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.get("http://47.94.129.228/oschina/detail/news_detail/64311.xml", new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int status, Header[] headers, byte[] bytes) {
				NewsDetail newsDetail = XmlUtils.toBean(NewsDetail.class, bytes);
				// 解析网络返回的数据，把要展示的内容拼接
				fillWebViewBody(newsDetail.getNews());
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				
			}
		});
	}

	private void initWebView() {
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		mTvSource = (TextView) findViewById(R.id.tv_source);
		mTvTime = (TextView) findViewById(R.id.tv_time);
		webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(15);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        webView.setWebViewClient(getWebViewClient());
        
    }

	public WebViewClient getWebViewClient() {

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	showUrlRedirect(view.getContext(), url);
                return true;// 自己处理html里面link
            }
        };
    }
	
	private void fillWebViewBody(News mNews) {
		mTvTitle.setText(mNews.getTitle());
        mTvSource.setText(mNews.getAuthor());
        mTvTime.setText(StringUtils.friendly_time(mNews.getPubDate()));
        StringBuffer body = new StringBuffer();
        body.append(UIHelper.setHtmlCotentSupportImagePreview(mNews.getBody()));

        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);

        // 更多关于***软件的信息
        String softwareName = mNews.getSoftwareName();
        String softwareLink = mNews.getSoftwareLink();
        if (!StringUtils.isEmpty(softwareName)
                && !StringUtils.isEmpty(softwareLink))
            body.append(String
                    .format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
                            softwareLink, softwareName));

        // 相关新闻
        if (mNews != null && mNews.getRelatives() != null
                && mNews.getRelatives().size() > 0) {
            String strRelative = "";
            for (Relative relative : mNews.getRelatives()) {
                strRelative += String.format(
                        "<a href='%s' style='text-decoration:none'>%s</a><p/>",
                        relative.url, relative.title);
            }
            body.append("<p/><div style=\"height:1px;width:100%;background:#DADADA;margin-bottom:10px;\"/>"
                    + String.format("<br/> <b>相关资讯</b> <div><p/>%s</div>",
                            strRelative));
        }
        body.append("<br/>");
        
        try {
			File file = new File(Environment.getExternalStorageDirectory(), 
					String.format("/OSChina/html/%d.html", mNews.getId()));
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(body.toString().getBytes());
			fos.close();
			System.out.println(body);
			System.out.println("save success! :" + file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        if (webView != null) {
        	webView.loadDataWithBaseURL(null, body.toString(), "text/html",
                    "utf-8", null);
        }
    }
	
	public void showUrlRedirect(Context context, String url) {
		Toast.makeText(context, url, 0).show();
	}
	
}
