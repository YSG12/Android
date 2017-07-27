package com.stav.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stav.zhbj.MainActivity;
import com.stav.zhbj.R;
import com.stav.zhbj.base.impl.NewsCenterPager;
import com.stav.zhbj.domain.NewsMenu;



import java.util.ArrayList;


/**
 * 侧边栏
 */
public class LeftMenuFragment extends BaseFragment {
    private ArrayList<NewsMenu.NewsMenuData> mNewsMenuData;
    @ViewInject(R.id.lv_list)
    private ListView lv_list;
    private int mCurrentPos; //当前被选中的item位置
    private LeftMenuAdapter mLeftMenuAdapter;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        lv_list = (ListView) view.findViewById(R.id.lv_list);
        ViewUtils.inject(this,view);    //注入View和事件
        return view;
    }

    @Override
    public void initData() {
    }

    //给侧边栏设置数据
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
        mCurrentPos = 0;    //当前选中的位置要归0
        //更新页面
        mNewsMenuData = data;

        mLeftMenuAdapter = new LeftMenuAdapter();
        lv_list.setAdapter(mLeftMenuAdapter);

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;     //更新当前选中的位置
                mLeftMenuAdapter.notifyDataSetChanged();        //刷新listView
                //收起侧边栏
                toggle();
                //侧边栏点击之后，要修改新闻中心中的FrameLayout中 的内容
                setCurrentDetailPager(position);
            }
        });
    }

    /**
     * 设置当前菜单详情页
     * @param position
     */
    private void setCurrentDetailPager(int position) {
        //获取新闻中心的对象
        MainActivity mainUI = (MainActivity) mActivity;
        //获取ContentFragment
        ContentFragment fragment = mainUI.getContentFragment();
        //获取新闻中心NewsCenterPager
        NewsCenterPager newsCenterPager = fragment.getNewsCenterPager();
        //修改新闻中心的FrameLayout的布局
        newsCenterPager.setCurrentDetailPager(position);
    }

    /**
     * 打开或关闭侧边栏
     */
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();   //切换开关侧边栏
    }

    class LeftMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNewsMenuData.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return mNewsMenuData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(mActivity,R.layout.list_item_left,null);
            TextView tv_menu = (TextView) view.findViewById(R.id.tv_menu);
            NewsMenu.NewsMenuData item = getItem(position);
            tv_menu.setText(item.title);

            if (position == mCurrentPos) {
                //被选中
                tv_menu.setEnabled(true);   //文字变为红色
            } else {
                //未选中
                tv_menu.setEnabled(false);  //文字变温白色
            }

            return view;
        }
    }
}
