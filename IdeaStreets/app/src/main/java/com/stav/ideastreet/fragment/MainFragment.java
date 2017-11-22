package com.stav.ideastreet.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.pager.CreativeOrnamentsPager;
import com.stav.ideastreet.ui.CommentListActivity;
import com.stav.ideastreet.ui.LoginActivity;
import com.stav.ideastreet.ui.PublishActivity;
import com.stav.ideastreet.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class MainFragment extends Fragment {
    protected PagerSlidingTabStrip tabs;
    protected ViewPager pager;
    private String[] strings;
    public List<View> viewPagerItems;//每一页显示的View
    private View pagerCreativeOrnament;

    ListView listView;
    EditText et_content;
    ImageButton btn_publish;

    static List<Post> weibos = new ArrayList<Post>();
    MainFragment.MAdapter adapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.pagertab, null);
        //        初始化每一页的内容
        inflater = LayoutInflater.from(getContext());
        pagerCreativeOrnament = inflater.inflate(R.layout.pager_creative_ornament, null);
        pagerCreativeOrnament();
        View view2 = inflater.inflate(R.layout.pager_creative_ornament, null);
        View view3 = inflater.inflate(R.layout.pager_creative_ornament, null);
        View view4 = inflater.inflate(R.layout.pager_creative_ornament, null);
        View view5 = inflater.inflate(R.layout.pager_creative_ornament, null);
        View view6 = inflater.inflate(R.layout.pager_creative_ornament, null);
        View view7 = inflater.inflate(R.layout.pager_creative_ornament, null);

        viewPagerItems = new ArrayList<>();
        viewPagerItems.add(pagerCreativeOrnament);
        viewPagerItems.add(view2);
        viewPagerItems.add(view3);
        viewPagerItems.add(view4);
        viewPagerItems.add(view5);
        viewPagerItems.add(view6);
        viewPagerItems.add(view7);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);

        // Xutils 实现原理，反射AFragment类，找注解变量，findviewbyid找到控件给变量赋值
        // ButterKnife 编译时注解，生成findviewbyId代码
        setTabs();
        return view;
    }

    private void pagerCreativeOrnament() {
        adapter1 = new MainFragment.MAdapter(getActivity());
        listView = (ListView) pagerCreativeOrnament.findViewById(R.id.listview);
        listView.setAdapter(adapter1);

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user == null){
            Toast.makeText(getContext(), "欢迎来到玩转创意街，请您先注册并登录~", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            findWeibos();
        }
    }

    /**
     * 查询微博
     */
    public void findWeibos(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        //等价于下面的sql语句查询
		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt DESC";
		new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

			@Override
			public void done(BmobQueryResult<Post> result, BmobException e) {
				if(e ==null){
					List<Post> list = result.getResults();
					if(list!=null && list.size()>0){
						weibos = list;
						adapter1.notifyDataSetChanged();
						et_content.setText("");
					}else{
						Log.i("smile", "查询成功，无数据返回");
					}
				}else{
					Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
				}
			}
		});

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
        int size= (int) this.getResources().getDimension(R.dimen.text_size_title);
        tabs.setTextSize(size);
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
        pager.setAdapter(new MyAdapter(viewPagerItems));

        tabs.setViewPager(pager);
    }

    class MyAdapter extends PagerAdapter{


        private List<View> views;

        public MyAdapter(List<View> views) {
            this.views = views;
        }


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
            container.addView(views.get(position), 0);
            return views.get(position);
        }
    }

    public class MAdapter extends BaseAdapter {


        private LayoutInflater mInflater;

        private Context mContext;

        public MAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        class ViewHolder {
            TextView tv_content;
            TextView tv_author;
        }

        @Override
        public int getCount() {
            return weibos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MainFragment.MAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_weibo, null);

                holder = new MainFragment.MAdapter.ViewHolder();
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_author = (TextView) convertView.findViewById(R.id.tv_author);

                convertView.setTag(holder);
            } else {
                holder = (MainFragment.MAdapter.ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            final Post weibo = weibos.get(position);
            MyUser user = weibo.getAuthor();
            holder.tv_author.setText("发布人："+(user==null?"":user.getUsername()));

            final String str = weibo.getContent();

            holder.tv_content.setText(str);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentListActivity.class);
                    intent.putExtra("objectId", weibo.getObjectId());
                    mContext.startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
