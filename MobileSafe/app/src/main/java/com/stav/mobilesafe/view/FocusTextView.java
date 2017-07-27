package com.stav.mobilesafe.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 能够获取自定义TextView
 * Created by Administrator on 2017/6/10.
 */
public class FocusTextView extends TextView {
    //使用在通过Java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }

    //由系统调用（带属性+上下文环境构造方法）
    public FocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //由系统调用（带属性+上下文环境构造方法+布局文件中定义样式文件的方法）
    public FocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写获取焦点的方法，有系统调用，调用的时候默认就能获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
