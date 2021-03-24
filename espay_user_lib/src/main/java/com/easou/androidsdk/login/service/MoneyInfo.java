package com.easou.androidsdk.login.service;

public class MoneyInfo {
    private int id;
    private String money;
    private String desc;
    private int status;//领取状态(0待完成，1已完成，2已领取,-1已过期)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
