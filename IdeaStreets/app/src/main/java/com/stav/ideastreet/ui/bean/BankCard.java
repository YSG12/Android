package com.stav.ideastreet.ui.bean;

/**
 * @author stav
 * @date 2017/10/19 21:26
 */
public class BankCard{

    private String cardNumber;
    private String bankName;

    public BankCard(String bankName, String cardNumber){
        this.bankName = bankName;
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

}