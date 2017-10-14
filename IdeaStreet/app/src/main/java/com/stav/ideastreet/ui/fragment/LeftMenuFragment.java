package com.stav.ideastreet.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.activity.MainActivity;
import com.stav.ideastreet.ui.pager.SpecialPager;
import com.stav.ideastreet.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 侧边栏
 */
public class LeftMenuFragment extends BaseFragment {

    private ListView lvList;
    private int mCurrentPos; //当前被选中的item位置

    @Override
    public View initView() {
        View view = View.inflate(UIUtils.getContext(), R.layout.framgent_left_menu, null);
        ListView lvList = (ListView) view.findViewById(R.id.lv_list);

        List<Map<String,Object>> data = new ArrayList<>();
        String[] from = new String[]{"icon","menu"};
        int[] to = new int[]{R.id.iv_icon, R.id.tv_menu};

//        1.用户中心 2.平台专栏特色 3.发布创意 4.用户创意展示 5.平台互动

        //往map里面添加数据
        HashMap<String, Object> map = new HashMap<>();
        map.put(from[0],android.R.drawable.star_on);
        map.put(from[1],"用户中心");
        data.add(map);
        //往map1里面添加数据
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put(from[0],android.R.drawable.star_on);
        map1.put(from[1],"专栏特色");
        data.add(map1);
        //往map2里面添加数据
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put(from[0],android.R.drawable.star_on);
        map2.put(from[1],"发布创意");
        data.add(map2);
        //往map3里面添加数据
        HashMap<String, Object> map3 = new HashMap<>();
        map3.put(from[0],android.R.drawable.star_on);
        map3.put(from[1],"创意展示");
        data.add(map3);
        //往map4里面添加数据
        HashMap<String, Object> map4 = new HashMap<>();
        map4.put(from[0],android.R.drawable.star_on);
        map4.put(from[1],"平台互动");
        data.add(map4);

        final SimpleAdapter adapter = new SimpleAdapter(UIUtils.getContext(), data, R.layout.list_item_left, from, to);
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
                adapter.notifyDataSetChanged();
                //收起侧边栏
                toggle();
                setCurrentDetailPager(position);
            }
        });

        return view;
    }

    /**
     * 设置当前菜单页
     * @param position
     */
    private void setCurrentDetailPager(int position) {
        MainActivity mainUI = (MainActivity) mActivity;
        ContentFragment fragment = mainUI.getContentFragment();
        SpecialPager specialPager = fragment.getSpecialPager();
        specialPager.setCurrentDetailPager(position);
    }

    @Override
    public void initData() {
    }

    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();
    }



}
