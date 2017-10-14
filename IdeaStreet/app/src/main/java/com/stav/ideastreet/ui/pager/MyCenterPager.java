package com.stav.ideastreet.ui.pager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.activity.MainActivity;
import com.stav.ideastreet.ui.activity.SettingActivity;
import com.stav.ideastreet.ui.fragment.ContentFragment;
import com.stav.ideastreet.utils.UIUtils;

import java.lang.ref.WeakReference;

/**
 * 个人中心界面
 * @author stav
 * @date 2017/9/5 11:18
 */
public class MyCenterPager extends BasePager {
    private WeakReference<Activity> sCurrentActivityWeakRef;
    public MyCenterPager(Activity activity){
        super(activity);
    }
    @Override
    public void initData() {
        View view = View.inflate(UIUtils.getContext(), R.layout.mycenter_pager, null);
        //添加布局
        fl_content.addView(view);
        //修改标题
        tv_title.setText("我");
        //隐藏菜单按钮
        ib_menu.setVisibility(View.INVISIBLE);
        //显示setting按钮
        bt_setting.setVisibility(View.VISIBLE);

        bt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fl_content.removeAllViews();
                tv_title.setText("设置");
                ib_back.setVisibility(View.VISIBLE);
                bt_setting.setVisibility(View.INVISIBLE);

            }
        });

    }


}
