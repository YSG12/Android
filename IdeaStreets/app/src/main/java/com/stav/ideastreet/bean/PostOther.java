package com.stav.ideastreet.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author stav
 * @date 2017/12/2 17:41
 */
public class PostOther extends BmobObject {
    private String name;
    private String address;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}
