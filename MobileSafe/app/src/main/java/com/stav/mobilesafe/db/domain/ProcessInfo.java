package com.stav.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/7/7.
 */

public class ProcessInfo {
    public String name; //应用名称
    public Drawable icon; //应用图标
    public long memSize;  //应用已使用的内存数
    public boolean isCheck; //是否被选中
    public boolean isSystem;    //是否为系统应用
    public String packageName; //如果服务没有名称，将其所在的包名作为名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", memSize='" + memSize + '\'' +
                ", isCheck=" + isCheck +
                ", isSystem=" + isSystem +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
