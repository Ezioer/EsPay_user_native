package com.easou.androidsdk.login.service;

public class MoneyListDetail {
    private int id;
    private int activityId;
    private String desc;
    private Long gainDay;
    private int money;
    private int status;//1未完成 0:完成还没领取，2：完成已经领取
    private int tsort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getGainDay() {
        return gainDay;
    }

    public void setGainDay(Long gainDay) {
        this.gainDay = gainDay;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTsort() {
        return tsort;
    }

    public void setTsort(int tsort) {
        this.tsort = tsort;
    }
}
