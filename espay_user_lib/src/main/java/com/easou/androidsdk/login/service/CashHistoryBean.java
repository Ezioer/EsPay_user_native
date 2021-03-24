package com.easou.androidsdk.login.service;

public class CashHistoryBean {
    private int id;
    private String money;
    private long drawTime;
    private String drawCode;

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

    public long getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(long drawTime) {
        this.drawTime = drawTime;
    }

    public String getDrawCode() {
        return drawCode;
    }

    public void setDrawCode(String drawCode) {
        this.drawCode = drawCode;
    }
}
