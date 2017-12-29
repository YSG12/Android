package com.stav.ideastreet.bean;

/**
 * @author stav
 * @date 2017/10/31 10:09
 */

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

import com.stav.ideastreet.bean.MyUser;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 *
 * @ClassName: 帖子
 * @Description: 帖子实体
 * @author smile
 * @date 2014年4月17日 上午11:10:44
 *
 */
public class Post extends BmobObject {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String title;   //帖子标题
    private int test;   //帖子分类
    private int love;   //点赞个数

    private int comment;   //评论个数
    private String selector;    //帖子分类
    private String content; //帖子内容
    private MyUser author;  // 微博发布者
    private BmobFile image; //微博图片


    private String authorName; //作者名字


    /**
     *  一对多关系：用于存储喜欢该帖子的所有用户
     */
    private BmobRelation relation;
    private boolean myFav;//收藏
    private boolean myLove;//赞
    private boolean isPass;
    private String updownImg;//创意图片

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public BmobFile getImage() {
        return image;
    }
    public void setImage(BmobFile image) {
        this.image = image;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public MyUser getAuthor() {
        return author;
    }
    public void setAuthor(MyUser author) {
        this.author = author;
    }
    public int getTest() {
        return test;
    }
    public void setTest(int test) {
        this.test = test;
    }
    public String getSelector() {
        return selector;
    }
    public void setSelector(String selector) {
        this.selector = selector;
    }
    public int getLove() {
        return love;
    }
    public void setLove(int love) {
        this.love = love;
    }
    public boolean isMyFav() {
        return myFav;
    }
    public void setMyFav(boolean myFav) {
        this.myFav = myFav;
    }
    public boolean isMyLove() {
        return myLove;
    }
    public void setMyLove(boolean myLove) {
        this.myLove = myLove;
    }
    public boolean isPass() {
        return isPass;
    }
    public void setPass(boolean isPass) {
        this.isPass = isPass;
    }
    public BmobRelation getRelation() {
        return relation;
    }
    public void setRelation(BmobRelation relation) {
        this.relation = relation;
    }
    public String getUpdownImg() {
        return updownImg;
    }
    public void setUpdownImg(String updownImg) {
        this.updownImg = updownImg;
    }
    public int getComment() {
        return comment;
    }
    public void setComment(int comment) {
        this.comment = comment;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
