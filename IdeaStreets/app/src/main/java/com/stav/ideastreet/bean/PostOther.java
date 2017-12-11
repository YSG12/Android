package com.stav.ideastreet.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author stav
 * @date 2017/12/2 17:41
 */
public class PostOther extends BmobObject {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 评论的用户
     */
    private String user;
    /**
     *  是否点赞
     */
    private boolean zanFocus;

    /**
     *  是否点赞
     */
    private boolean enshrine;

    /**
     *  所评论的帖子
     */
    private String post; //一个评论只能属于一个微博

    public String getPost() {
        return post;
    }
    public void setPost(String post) {
        this.post = post;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public boolean isZanFocus() {
        return zanFocus;
    }
    public void setZanFocus(boolean zanFocus) {
        this.zanFocus = zanFocus;
    }
    public boolean isEnshrine() {
        return enshrine;
    }
    public void setEnshrine(boolean enshrine) {
        this.enshrine = enshrine;
    }

}
