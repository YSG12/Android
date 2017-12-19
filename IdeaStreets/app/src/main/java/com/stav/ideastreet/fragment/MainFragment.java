package com.stav.ideastreet.fragment;

import android.app.Activity;
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
import com.stav.ideastreet.db.DatabaseUtil;
import com.stav.ideastreet.ui.CommentListActivity;
import com.stav.ideastreet.ui.LoginActivity;
import com.stav.ideastreet.ui.UserInfoActivity;
import com.stav.ideastreet.util.StringUtils;
import com.stav.ideastreet.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
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
    static List<PostOther> postOthers = new ArrayList<>();
    //    MainFragment.MAdapter adapter;
    MAdapter adapter;

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
        adapter = new MAdapter();
        lv7 = (ListView) pagerTalentMarket.findViewById(R.id.listview);
        lv7.setAdapter(adapter);
        findWeibo_g();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意家居pager
     */
    private void pagerCreativeLiving() {
        adapter = new MAdapter();
        lv6 = (ListView) pagerCreativeLiving.findViewById(R.id.listview);
        lv6.setAdapter(adapter);
        findWeibo_f();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意礼物pager
     */
    private void pagerCreativeGifts() {
        adapter = new MAdapter();
        lv5 = (ListView) pagerCreativeGifts.findViewById(R.id.listview);
        lv5.setAdapter(adapter);
        findWeibo_e();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意陶瓷pager
     */
    private void pagerCreativeCeramic() {
        adapter = new MAdapter();
        lv4 = (ListView) pagerCreativeCeramic.findViewById(R.id.listview);
        lv4.setAdapter(adapter);
        findWeibo_d();
        adapter.notifyDataSetChanged();
    }
    /**
     * 创意设计pager
     */
    private void pagerCreativeDesign() {
        adapter = new MAdapter();
        lv3 = (ListView) pagerCreativeDesign.findViewById(R.id.listview);
        lv3.setAdapter(adapter);
        findWeibo_c();
        adapter.notifyDataSetChanged();

    }
    /**
     * 创意美食pager
     */
    private void pagerCuisine() {
        adapter = new MAdapter();
        lv2 = (ListView) pagerCuisine.findViewById(R.id.listview);
        lv2.setAdapter(adapter);
        findWeibo_b();
        adapter.notifyDataSetChanged();
    }

    /**
     * 创意饰品pager
     */
    private void pagerCreativeOrnament() {
        adapter = new MAdapter();
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


    public class MAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(),R.layout.list_item_weibo, null);

                holder = new ViewHolder();
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
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            final Post weibo = weibos.get(position);
            final MyUser user = weibo.getAuthor();
            holder.tv_author.setText(user == null ? "" : user.getUsername()); //发布人
            holder.tv_createAt.setText(weibo.getCreatedAt());   //创意发布时间
            holder.tv_selector.setText(weibo.getSelector());    //创意分类

            //当点赞数为0时不显示
            if (holder.tv_like_num.getText() == 0 + "") {
                holder.tv_like_num.setVisibility(View.INVISIBLE);
            }
            holder.tv_like_num.setText(weibo.getLove() + "");  //点赞数

            if (DatabaseUtil.getInstance(getActivity()).isLoved(weibo)) {
                holder.tv_like_num.setTextColor(Color.parseColor("#D95555"));
                holder.ib_like.setBackgroundResource(R.drawable.ic_likeed);
            } else {
                holder.tv_like_num.setTextColor(Color.parseColor("#000000"));
                holder.ib_like.setBackgroundResource(R.drawable.ic_unlike);
            }
            //点击点赞按钮
            holder.ib_like.setOnClickListener(new View.OnClickListener() {
                boolean oldFav = weibo.isMyFav();

                @Override
                public void onClick(View v) {
                    if (user == null) {
                        showToast("请先登录。");
                        return;
                    }
                    if (weibo.isMyLove()) {
                        showToast("您已赞过啦");
                        return;
                    }

                    if (DatabaseUtil.getInstance(getActivity()).isLoved(weibo)) {
                        showToast("您已赞过啦");
                        weibo.setMyLove(true);
//                        weibo.setLove(weibo.getLove() + 1);
//                        holder.tv_like_num.setTextColor(Color.parseColor("#D95555"));
//                        holder.tv_like_num.setText(weibo.getLove() + "");
                        return;
                    }

                    weibo.setLove(weibo.getLove() + 1);
                    holder.tv_like_num.setTextColor(Color.parseColor("#D95555"));
                    holder.ib_like.setBackgroundResource(R.drawable.ic_likeed);
                    holder.tv_like_num.setText(weibo.getLove() + "");

                    weibo.increment("love", 1);
                    if (weibo.isMyFav()) {
                        weibo.setMyFav(false);
                    }
                    weibo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                            if (e == null) {
                                weibo.setMyLove(true);
                                weibo.setMyFav(oldFav);
                                DatabaseUtil.getInstance(getActivity()).insertFav(weibo);
                                Log.i("stav", "点赞成功~");
                            } else {
                                weibo.setMyLove(true);
                                weibo.setMyFav(oldFav);
                            }
                        }
                    });
                }
            });
// && DatabaseUtil.getInstance(getActivity()).isLoved(weibo)
            if (weibo.isMyFav()) {
                holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_p);
            } else {
                holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_n);
            }
            //点击收藏按钮
            holder.ib_enshrine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickFav(v, weibo, holder);
                }
            });


            final String str = weibo.getContent();
            // 特殊文字处理,将表情等转换一下
            holder.tv_content.setText(StringUtils.getEmotionContent(getActivity(), holder.tv_content, str)); //发布创意内容

            //点击进入创意评论页
            holder.ib_commment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CommentListActivity.class);
                    intent.putExtra("objectId", weibo.getObjectId());
                    getActivity().startActivity(intent);
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

        private void onClickFav(View v, final Post weibo, ViewHolder holder) {
            // TODO Auto-generated method stub
            MyUser user = BmobUser.getCurrentUser(MyUser.class);
            if (user != null && user.getSessionToken() != null) {
                BmobRelation favRelaton = new BmobRelation();

                weibo.setMyFav(!weibo.isMyFav());
                if (weibo.isMyFav()) {
                    holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_p);

//                    ((ImageView) v).setImageResource(R.mipmap.base_action_bar_enshrine_bg_p);
                    favRelaton.add(weibo);
                    user.setFavorite(favRelaton);
                    user.update(new UpdateListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                DatabaseUtil.getInstance(getActivity()).insertFav(weibo);
                                Log.i("stav", "收藏成功。");
                                weibo.setMyFav(true);
                                weibo.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            } else {
                                // TODO Auto-generated method stub
                                Log.i("stav", "收藏失败。请检查网络~");
                                showToast("收藏失败。请检查网络~" + e);
                            }
                        }
                    });

                } else {
                    holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_n);

//                    ((ImageView) v).setImageResource(R.mipmap.base_action_bar_enshrine_bg_n);
                    favRelaton.remove(weibo);
                    user.setFavorite(favRelaton);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                DatabaseUtil.getInstance(getActivity()).deleteFav(weibo);
                                Log.i("stav", "取消收藏。");
                                weibo.setMyFav(false);
                                weibo.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            } else {
                                showToast("取消收藏失败。请检查网络~" + e);
                            }
                        }
                    });
                }

            } else {
                // 前往登录注册界面
                showToast("收藏前请先登录。");
            }
        }

        public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), target);
            if (bundle != null)
                intent.putExtra(getActivity().getPackageName(), bundle);
            getActivity().startActivity(intent);
        }
    }



}
