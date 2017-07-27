package com.stav.zhbj.test;

/**
 * Created by Administrator on 2017/7/26.
 */

public class Jni {

    static {
        System.loadLibrary("demo");
    }
    public native String getString();
}