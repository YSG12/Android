package com.stav.ideastreet.fragment;

import com.stav.ideastreet.R;
import com.stav.ideastreet.widget.MyLinkMovementMethod;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
//		htmltext(textView);
//		clicktext(textView);
//		fontAndSizetext(textView); 
		imgtext(textView);
		return textView;
	}
	private void imgtext(TextView textView) {
		String source = "周末一小时运动量达标，[饭]";
		SpannableString imgtext = new SpannableString(source);
		ImageSpan imageSpan = new ImageSpan(getActivity(), R.drawable.smiley_61);
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
					Toast.makeText(getActivity(), "点击了链接："+getURL(), 0).show();
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
				Toast.makeText(getActivity(), "张三", 0).show();
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
				Toast.makeText(getActivity(), "李四", 0).show();
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
