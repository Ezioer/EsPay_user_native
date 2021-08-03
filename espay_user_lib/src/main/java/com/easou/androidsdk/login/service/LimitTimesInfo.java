package com.easou.androidsdk.login.service;

/**
 * created by xiaoqing.zhou
 * on 2020/8/10
 * fun
 */
public class LimitTimesInfo {
    private int todayTimes;
    private int totalTimes;

    public int getTodayTimes() {
        return todayTimes;
    }

    public void setTodayTimes(int todayTimes) {
        this.todayTimes = todayTimes;
    }

    public int getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(int totalTimes) {
        this.totalTimes = totalTimes;
    }
}
