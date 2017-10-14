package com.stav.ideastreet.fragment;

import butterknife.ButterKnife;
import com.stav.ideastreet.R;
import com.stav.ideastreet.widget.PagerSlidingTabStrip;
import android.R.raw;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AFragment extends Fragment {
	protected PagerSlidingTabStrip tabs;
	protected ViewPager pager;
	private String[] strings;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.pagertab, null);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);
		// Xutils 实现原理，反射AFragment类，找注解变量，findviewbyid找到控件给变量赋值
		// ButterKnife 编译时注解，生成findviewbyId代码
		setTabs();
		return view;
	}
	private void setTabs() {
		// 平分页签
		tabs.setShouldExpand(true);
		// 去掉分割线
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置滑动条的颜色
		tabs.setIndicatorColor(0xff0b9a27);
		// 设置滑动条的高度
		tabs.setIndicatorHeight(3);
		// 设置文字大小
		tabs.setTextSize(40);
		// 设置文字字体
		tabs.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		
//		tabs.setTextColorResource(R.color.viewpage_selector_slide_title);
		// 提供设置选中页签颜色的方法
		tabs.setSelectedTabColor(0xff0b9a27);
		
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		strings = getActivity().getResources().getStringArray(R.array.news_viewpage_arrays);
		pager.setAdapter(new MyAdapter());
		
		tabs.setViewPager(pager);
	}
	
	class MyAdapter extends PagerAdapter{
		
		@Override
		public CharSequence getPageTitle(int position) {
			return strings[position];
		}

		@Override
		public int getCount() {
			return strings.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TextView textView = new TextView(getActivity());
			textView.setTextSize(18);
			textView.setText(strings[position]);
			container.addView(textView);
			return textView;
		}
		
		
	}
}
