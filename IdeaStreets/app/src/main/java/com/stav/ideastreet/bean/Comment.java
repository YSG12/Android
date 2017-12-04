package com.stav.ideastreet.bean;


import cn.bmob.v3.BmobObject;
/**
 * 评论实体
 * @author stav
 * @date 2017/11/1 11:17
 */
public class Comment extends BmobObject {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论的用户
     */
    private MyUser user;

    /**
     *  所评论的帖子
     */
    private Post post; //一个评论只能属于一个微博

    public Post getPost() {
        return post;
    }
    public void setPost(Post post) {
        this.post = post;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public MyUser getUser() {
        return user;
    }
    public void setUser(MyUser user) {
        this.user = user;
    }

}
