package com.stav.ideastreet.fragment;

import android.app.Activity;
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
import com.stav.ideastreet.bean.PostOther;
import com.stav.ideastreet.ui.CommentListActivity;
import com.stav.ideastreet.ui.LoginActivity;
import com.stav.ideastreet.ui.SearchUserActivity;
import com.stav.ideastreet.ui.UserInfoActivity;
import com.stav.ideastreet.util.StringUtils;
import com.stav.ideastreet.utils.AnimationTools;
import com.stav.ideastreet.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.stav.ideastreet.base.BaseApplication.showToast;

public class MainFragment extends Fragment {
    protected PagerSlidingTabStrip tabs;
    private CompositeSubscription mCompositeSubscription;
    protected ViewPager pager;
    private String[] strings;
    public List<View> viewPagerItems;//每一页显示的View
    private View pagerCreativeOrnament,pagerCuisine,pagerCreativeDesign,pagerCreativeCeramic,
            pagerCreativeGifts,pagerCreativeLiving,pagerTalentMarket;

    ListView lv1,lv2,lv3,lv4,lv5,lv6,lv7;
    EditText et_content;
    ImageButton btn_publish;

    static List<Post> weibos = new ArrayList<>();
    MainFragment.MAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.pagertab, null);
        //        初始化每一页的内容
        inflater = LayoutInflater.from(getContext());

        pagerCreativeOrnament = inflater.inflate(R.layout.pager_creative_ornament, null);
        pagerCuisine = inflater.inflate(R.layout.pager_cuisine, null);
        pagerCreativeDesign = inflater.inflate(R.layout.pager_creative_design, null);
        pagerCreativeCeramic = inflater.inflate(R.layout.pager_creative_ceramic, null);
        pagerCreativeGifts = inflater.inflate(R.layout.pager_creative_gifts, null);
        pagerCreativeLiving = inflater.inflate(R.layout.pager_creative_living, null);
        pagerTalentMarket = inflater.inflate(R.layout.pager_talent_market, null);

        pagerCreativeOrnament();
//        pagerCuisine();
//        pagerCreativeDesign();
//        pagerCreativeCeramic();
//        pagerCreativeGifts();
//        pagerCreativeLiving();
//        pagerTalentMarket();


        viewPagerItems = new ArrayList<>();
        viewPagerItems.add(pagerCreativeOrnament);
        viewPagerItems.add(pagerCuisine);
        viewPagerItems.add(pagerCreativeDesign);
        viewPagerItems.add(pagerCreativeCeramic);
        viewPagerItems.add(pagerCreativeGifts);
        viewPagerItems.add(pagerCreativeLiving);
        viewPagerItems.add(pagerTalentMarket);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);


        // Xutils 实现原理，反射AFragment类，找注解变量，findviewbyid找到控件给变量赋值
        // ButterKnife 编译时注解，生成findviewbyId代码
        setTabs();
        return view;
    }

    /**
     * 人才市场pager
     */
    private void pagerTalentMarket() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv7 = (ListView) pagerTalentMarket.findViewById(R.id.listview);
        lv7.setAdapter(adapter);
        findWeibo_g();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意家居pager
     */
    private void pagerCreativeLiving() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv6 = (ListView) pagerCreativeLiving.findViewById(R.id.listview);
        lv6.setAdapter(adapter);
        findWeibo_f();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意礼物pager
     */
    private void pagerCreativeGifts() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv5 = (ListView) pagerCreativeGifts.findViewById(R.id.listview);
        lv5.setAdapter(adapter);
        findWeibo_e();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意陶瓷pager
     */
    private void pagerCreativeCeramic() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv4 = (ListView) pagerCreativeCeramic.findViewById(R.id.listview);
        lv4.setAdapter(adapter);
        findWeibo_d();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意设计pager
     */
    private void pagerCreativeDesign() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv3 = (ListView) pagerCreativeDesign.findViewById(R.id.listview);
        lv3.setAdapter(adapter);
        findWeibo_c();
        adapter.notifyDataSetChanged();

    }
    /**
     * 创意美食pager
     */
    private void pagerCuisine() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv2 = (ListView) pagerCuisine.findViewById(R.id.listview);
        lv2.setAdapter(adapter);
        findWeibo_b();
        adapter.notifyDataSetChanged();
    }

    /**
     * 创意饰品pager
     */
    private void pagerCreativeOrnament() {
        adapter = new MainFragment.MAdapter(getActivity());
        lv1 = (ListView) pagerCreativeOrnament.findViewById(R.id.listview);
        lv1.setAdapter(adapter);

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user == null){
            Toast.makeText(getContext(), "欢迎来到玩转创意街，请您先注册并登录~", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            findWeibo_a();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 查询创意分类页面
     */
    public void findWeibo_a(){

//        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//        BmobQuery<Post> query = new BmobQuery<Post>();
//        query.addWhereEqualTo("author", user);	// 查询当前用户的所有微博
//        query.order("-updatedAt");
//        query.include("author");// 希望在查询微博信息的同时也把发布人的信息查询出来，可以使用include方法
//        query.findObjects(new FindListener<Post>() {
//            @Override
//            public void done(List<Post> object, BmobException e) {
//                if(e==null){
//                    weibos = object;
//                    adapter.notifyDataSetChanged();
//                    et_content.setText("");
//                }else{
//                    Log.d("",""+e);
//                }
//            }
//
//        });
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        //等价于下面的sql语句查询
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
		String sql = "select include author,* from Post where test = 0 ORDER BY updatedAt ASC";
		new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

			@Override
			public void done(BmobQueryResult<Post> result, BmobException e) {
				if(e ==null){
					List<Post> list = result.getResults();
					if(list!=null && list.size()>0){
						weibos = list;
						adapter.notifyDataSetChanged();
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
    public void findWeibo_b(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
        String sql = "select include author,* from Post where test = 1 ORDER BY updatedAt ASC";
        new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

            @Override
            public void done(BmobQueryResult<Post> result, BmobException e) {
                if(e ==null){
                    List<Post> list = result.getResults();
                    if(list!=null && list.size()>0){
                        weibos = list;
                        adapter.notifyDataSetChanged();
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
    public void findWeibo_c(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
        String sql = "select include author,* from Post where test = 2 ORDER BY updatedAt ASC";
        new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

            @Override
            public void done(BmobQueryResult<Post> result, BmobException e) {
                if(e ==null){
                    List<Post> list = result.getResults();
                    if(list!=null && list.size()>0){
                        weibos = list;
                        adapter.notifyDataSetChanged();
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
    public void findWeibo_d(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
        String sql = "select include author,* from Post where test = 3 ORDER BY updatedAt ASC";
        new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

            @Override
            public void done(BmobQueryResult<Post> result, BmobException e) {
                if(e ==null){
                    List<Post> list = result.getResults();
                    if(list!=null && list.size()>0){
                        weibos = list;
                        adapter.notifyDataSetChanged();
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
    public void findWeibo_e(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
        String sql = "select include author,* from Post where test = 4 ORDER BY updatedAt ASC";
        new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

            @Override
            public void done(BmobQueryResult<Post> result, BmobException e) {
                if(e ==null){
                    List<Post> list = result.getResults();
                    if(list!=null && list.size()>0){
                        weibos = list;
                        adapter.notifyDataSetChanged();
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
    public void findWeibo_f(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
        String sql = "select include author,* from Post where test = 5 ORDER BY updatedAt ASC";
        new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

            @Override
            public void done(BmobQueryResult<Post> result, BmobException e) {
                if(e ==null){
                    List<Post> list = result.getResults();
                    if(list!=null && list.size()>0){
                        weibos = list;
                        adapter.notifyDataSetChanged();
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
    public void findWeibo_g(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
//		String sql = "select include author,* from Post where author = pointer('_User', "+"'"+user.getObjectId()+"') ORDER BY updatedAt ASC LIMIT 3";
        String sql = "select include author,* from Post where test = 6 ORDER BY updatedAt ASC";
        new BmobQuery<Post>().doSQLQuery(sql, new SQLQueryListener<Post>(){

            @Override
            public void done(BmobQueryResult<Post> result, BmobException e) {
                if(e ==null){
                    List<Post> list = result.getResults();
                    if(list!=null && list.size()>0){
                        weibos = list;
                        adapter.notifyDataSetChanged();
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        strings = getActivity().getResources().getStringArray(R.array.news_viewpage_arrays);
        pager.setAdapter(new MyAdapter(viewPagerItems));

        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        pagerCreativeOrnament();
                        break;
                    case 1:
                        pagerCuisine();
                        break;
                    case 2:
                        pagerCreativeDesign();
                        break;
                    case 3:
                        pagerCreativeCeramic();
                        break;
                    case 4:
                        pagerCreativeGifts();
                        break;
                    case 5:
                        pagerCreativeLiving();
                        break;
                    case 6:
                        pagerTalentMarket();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    public class MyAdapter extends PagerAdapter{


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
            TextView tv_selector;
            TextView tv_createAt;
//            TextView tv_comment_num;
            TextView tv_like_num;
            ImageButton ib_enshrine;
            ImageButton ib_author;
            ImageButton ib_commment;
            ImageButton ib_like;
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
                holder.tv_selector = (TextView) convertView.findViewById(R.id.tv_selector);
                holder.tv_createAt = (TextView) convertView.findViewById(R.id.tv_createAt);
//                holder.tv_comment_num = (TextView) convertView.findViewById(R.id.tv_comment_num);
                holder.tv_like_num = (TextView) convertView.findViewById(R.id.tv_like_num);
                holder.ib_enshrine = (ImageButton) convertView.findViewById(R.id.ib_enshrine);
                holder.ib_author = (ImageButton) convertView.findViewById(R.id.ib_author);
                holder.ib_commment = (ImageButton) convertView.findViewById(R.id.ib_commment);
                holder.ib_like = (ImageButton) convertView.findViewById(R.id.ib_like);

                convertView.setTag(holder);
            } else {
                holder = (MainFragment.MAdapter.ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            final Post weibo = weibos.get(position);
            final MyUser user = weibo.getAuthor();
            holder.tv_author.setText(user==null?"":user.getUsername()); //发布人
            holder.tv_createAt.setText(weibo.getCreatedAt());   //创意发布时间
            holder.tv_selector.setText(weibo.getSelector());    //创意分类

            holder.tv_like_num.setText(weibo.getLikeNum()+"");  //点赞数
            notifyDataSetChanged();
            //当点赞数为0时不显示
            if (holder.tv_like_num.getText() == 0+"") {
                holder.tv_like_num.setVisibility(View.INVISIBLE);
            }
            //点击点击按钮
            holder.ib_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int likeNum = weibo.getLikeNum();
                    boolean flag = weibo.isZanFocus();
                    // 判断当前flag是点赞还是取消赞,是的话就给bean值减1，否则就加1
                    if (flag) {
                        weibo.setLikeNum(likeNum - 1);
                        holder.tv_like_num.setText((likeNum-1)+"");
                        holder.ib_like.setBackgroundResource(R.drawable.ic_unlike);
                    } else {
                        weibo.setLikeNum(likeNum + 1);
                        holder.tv_like_num.setText((likeNum+1)+"");
                        holder.ib_like.setBackgroundResource(R.drawable.ic_likeed);
                    }
                    // 反向存储记录，实现取消点赞功能
                    weibo.setZanFocus(!flag);
                    AnimationTools.scale(holder.ib_like);

                    addSubscription(weibo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                showToast("");
                            } else {
                                Log.d("",e+"");
                            }
                        }
                    }));
                }
            });

            if (weibo.isEnshrine()) {
                holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_p);
            } else {
                holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_n);
            }
            //点击收藏按钮
            holder.ib_enshrine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = weibo.isEnshrine();
                    // 判断当前flag是点赞还是取消赞,是的话就给bean值减1，否则就加1
                    if (flag) {
                        holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_n);
                        weibo.setEnshrine(false);
                    } else {
                        holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_p);
                        weibo.setEnshrine(true);
                    }
                    // 反向存储记录，实现取消点赞功能
                    weibo.setEnshrine(!flag);
                    AnimationTools.scale(holder.ib_enshrine);

                    addSubscription(weibo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                showToast("");
                            } else {
                                Log.d("",e+"");
                            }
                        }
                    }));
                }
            });



            final String str = weibo.getContent();
            // 特殊文字处理,将表情等转换一下
            holder.tv_content.setText(StringUtils.getEmotionContent(getContext(), holder.tv_content, str)); //发布创意内容

            //点击进入创意评论页
            holder.ib_commment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentListActivity.class);
                    intent.putExtra("objectId", weibo.getObjectId());
                    mContext.startActivity(intent);
                }
            });

            //点击进入添加好友页
            holder.ib_author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    MyUser user = weibo.getAuthor();
                    bundle.putSerializable("u", user);
                    startActivity(UserInfoActivity.class, bundle, false);
                }
            });

            return convertView;
        }
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

    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), target);
        if (bundle != null)
            intent.putExtra(getActivity().getPackageName(), bundle);
        startActivity(intent);
    }



}
