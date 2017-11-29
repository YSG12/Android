package com.stav.ideastreet.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stav.ideastreet.R;
import com.stav.ideastreet.base.ParentWithNaviActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.utils.UIHelper;
import com.stav.ideastreet.widget.MyLinkMovementMethod;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.stav.ideastreet.base.BaseApplication.showToast;

/**新朋友
 * @author :smile
 * @project:NewFriendActivity
 * @date :2016-01-25-18:23
 */
public class PublishActivity extends ParentWithNaviActivity {


    EditText et_content;
    ImageButton btn_publish;

    @Override
    protected String title() {
        return "新朋友";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_publish_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
                startActivity(SearchUserActivity.class,null);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initNaviView();
        initUI();
        btn_publish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                publishWeibo(et_content.getText().toString());
                startActivity(new Intent(getApplicationContext(),WriteActivity.class));
            }
        });

//        findWeibos();

//        TextView textView = (TextView) findViewById(R.id.tv);
////		htmltext(textView);
////		clicktext(textView);
//		fontAndSizetext(textView);
////        imgtext(textView);
    }


    private void initUI() {
        et_content = (EditText) findViewById(R.id.et_content);
        btn_publish = (ImageButton) findViewById(R.id.btn_publish);
    }



    /**
     * 发布微博，发表微博时关联了用户类型，是一对一的体现
     */
    private void publishWeibo(String content){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user == null){
            showToast("发布微博前请先登陆");
            return;
        }else if(TextUtils.isEmpty(content)){
            showToast("发布内容不能为空");
            return;
        }
        // 创建微博信息
        Post weibo = new Post();
        weibo.setContent(content);
        weibo.setAuthor(user);
        weibo.save(new SaveListener<String>() {

            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    showToast("发布成功");
                }else{
                    Log.e("tag", "done: "+(e));
                }
            }
        });
    }

    private void imgtext(TextView textView) {
        String source = "周末一小时运动量达标，[饭]";
        SpannableString imgtext = new SpannableString(source);
        ImageSpan imageSpan = new ImageSpan(getApplicationContext(), R.drawable.smiley_61);
        // 参数2：start 包含，参数3：end 不包含
        imgtext.setSpan(imageSpan, source.indexOf("["),
                source.indexOf("]")+1, 0);
        textView.setText(imgtext);
    }

    private void fontAndSizetext(TextView textView){
        String title = "Android属性动画(详解)";
        String source = "发表了博客" + title;//发表了博客Android属性动画(详解)
        SpannableString sp = new SpannableString(source);
        int start = source.indexOf(title);
        int end = source.length();
        sp.setSpan(new AbsoluteSizeSpan(26, true), start, end,
                0);
        sp.setSpan(
                new ForegroundColorSpan(Color.parseColor("#0e5986")),
                start, end, 0);
        textView.setText(sp);
    }

    private void htmltext(TextView textView) {
        String message = "<html> <head></head> <body> 哈哈哈 <a href=\"http://m.oschina.net/u/993896\" class=\"referer\">@itheima</a> 我点你了噢 </body> </html>";
        Spanned fromHtml = Html.fromHtml(message);
        textView.setText(fromHtml);

        URLSpan[] urls = fromHtml.getSpans(0, fromHtml.length(), URLSpan.class);
        SpannableStringBuilder ss = new SpannableStringBuilder(textView.getText());
        for (URLSpan url : urls) {
            // 移除之前的html样式
            ss.removeSpan(url);
            ss.setSpan(new URLSpan(url.getURL()){

                @Override
                public void onClick(View widget) {
                    Toast.makeText(getApplicationContext(), "点击了链接："+getURL(), 0).show();
//					super.onClick(widget);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
//					ds.setUnderlineText(false);
//					ds.setColor(Color.RED);
                }
            }, fromHtml.getSpanStart(url), fromHtml.getSpanEnd(url), 0);
        }
        textView.setText(ss);
//		textView.setMovementMethod(LinkMovementMethod.getInstance());
        // 点击链接选中效果
        textView.setMovementMethod(MyLinkMovementMethod.a());
    }

    private void clicktext(TextView textView) {
        SpannableStringBuilder ss = new SpannableStringBuilder("张三,李四");
        ClickableSpan span = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Toast.makeText(getApplicationContext(), "张三", 0).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 去掉下划线
//				ds.setUnderlineText(false);
            }
        };
        ss.setSpan(span, 0, 2, 0);// start 包含，end 不包含
        ClickableSpan span2 = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Toast.makeText(getApplicationContext(), "李四", 0).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(span2, 3, 5, 0);

        ss.append("觉得很赞");
        textView.setText(ss);
        // 让span 可以点击
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
