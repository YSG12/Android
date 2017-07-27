package com.stav.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stav.mobilesafe.R;

/**
 * Created by Administrator on 2017/6/10.
 */

public class SettingItemView extends RelativeLayout {

    private String mDestitle;
    private String mDesoff;
    private String mDeson;
    private CheckBox cb_box;
    private TextView tv_des;
    private TextView tv_title;
    private static final String tag = "SettingItemView";
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.stav.mobilesafe";

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml--->view 将设置界面的条目转换成view对象，直接添加到当前SettingItemView对应的view中
//        View view = View.inflate(context, R.layout.setting_item_view, null);
//        this.addView(view);
        View view = View.inflate(context, R.layout.setting_item_view, this);
        //自定义组合空间中的标题描述
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_des = (TextView) this.findViewById(R.id.tv_des);
        cb_box = (CheckBox) this.findViewById(R.id.cb_box);

        //获取自定义以及原生属性的操作，AttributeSet attrs对象中获取
        initAttrs(attrs);
        tv_title.setText(mDestitle);
    }

    /**
     * 返回属性集合中自定义属性集合
     * @param attrs 构造方法中维护好的属性集合
     */
    private void initAttrs(AttributeSet attrs) {
//        //获取属性的总个数
//        Log.i(tag,"attrs.getAttributeCount()="+attrs.getAttributeCount());
//        //获取属性名称以及属性值
//        for (int i=0; i<attrs.getAttributeCount(); i++){
//            String attributeName = attrs.getAttributeName(i);
//        }
        //通过名空间+属性名称获取属性值
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");

    }

    /**
     * 判断是否开启的方法
     * @return 返回当前settingItemView是否选中状态 true 开启 false 关闭
     */
    public boolean isCheck(){
        //由checkBox的选中结果，决定当前条目是否开启
        return cb_box.isChecked();
    }

    /**
     *
     * @param isCheck 是否作为开启的变量，由点击过程去传递
     */
    public void setCheck(boolean isCheck) {
        //当前条目在选择的过程中，cb_box选中状态也跟随（isCheck）变化
        cb_box.setChecked(isCheck);
        if (isCheck) {
            tv_des.setText(mDeson);
        } else {
            tv_des.setText(mDesoff);
        }
    }

}
