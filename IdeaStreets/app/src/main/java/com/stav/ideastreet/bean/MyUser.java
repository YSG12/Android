package com.stav.ideastreet.bean;

import java.sql.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * @author stav
 * @date 2017/10/19 21:25
 */
public class MyUser extends BmobUser {


    private static final long serialVersionUID = 1L;
    private Integer age;
    private Integer num;
    private String sex;

//    private Date createdAt;
//    private Date updatedAt;

    private List<String> hobby;		// 对应服务端Array类型：String类型的集合
    private List<BankCard> cards;	// 对应服务端Array类型:Object类型的集合

    private BankCard mainCard;      //主卡
    private Person banker;          //银行工作人员

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    private String motto;          //座右铭



    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public List<String> getHobby() {
        return hobby;
    }
    public void setHobby(List<String> hobby) {
        this.hobby = hobby;
    }
    public List<BankCard> getCards() {
        return cards;
    }
    public void setCards(List<BankCard> cards) {
        this.cards = cards;
    }
    public Integer getNum() {
        return num;
    }
    public void setNum(Integer num) {
        this.num = num;
    }
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    public BankCard getMainCard() {
        return mainCard;
    }
    public void setMainCard(BankCard mainCard) {
        this.mainCard = mainCard;
    }
    public Person getBanker() {
        return banker;
    }
    public void setBanker(Person banker) {
        this.banker = banker;
    }

    @Override
    public String toString() {
        return getUsername()+"\n"+getObjectId()+"\n"+age+"\n" + motto +
                "\n"+num+"\n"+getSessionToken()+"\n"+getEmailVerified();
    }

}
