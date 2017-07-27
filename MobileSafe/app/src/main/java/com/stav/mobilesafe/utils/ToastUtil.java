package com.stav.mobilesafe.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/6/9.
 */

public class ToastUtil {

    /**
     *打印吐司
     * @param ctx 上下文环境
     * @param msg 打印的文本内容
     */
    public static void show(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }


}
