package com.stav.zhbj.domain;

import java.util.ArrayList;

/**
 * 组图对象
 * Created by Administrator on 2017/7/25.
 */
public class PhotoBean {
    public PhotoData data;

    public class PhotoData {
        public ArrayList<PhotoNews> news;
    }
    public class PhotoNews {
        public int id;
        public String listimage;
        public String title;

    }
}
