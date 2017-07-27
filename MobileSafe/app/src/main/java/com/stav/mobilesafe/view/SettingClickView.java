package com.stav.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stav.mobilesafe.R;

/**
 * Created by Administrator on 2017/6/10.
 */

public class SettingClickView extends RelativeLayout {

    private ImageView iv_image;
    private TextView tv_des;
    private TextView tv_title;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml--->view 将设置界面的条目转换成view对象，直接添加到当前SettingItemView对应的view中
//        View view = View.inflate(context, R.layout.setting_item_view, null);
//        this.addView(view);
        View.inflate(context, R.layout.setting_click_view, this);
        //自定义组合空间中的标题描述
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_des = (TextView) this.findViewById(R.id.tv_des);
        iv_image = (ImageView) this.findViewById(R.id.iv_image);


    }

    /**
     * 设置标题的内容
     * @param title 标题
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    /**
     * 设置描述内容
     * @param des 描述内容
     */
    public void setDes (String des) {
        tv_des.setText(des);
    }

}
