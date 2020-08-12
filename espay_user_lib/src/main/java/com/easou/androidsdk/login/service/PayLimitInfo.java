package com.easou.androidsdk.login.service;

/**
 * created by xiaoqing.zhou
 * on 2020/8/10
 * fun
 */
public class PayLimitInfo {
    private int id;
    private int minAge;
    private int maxAge;
    private long sPay;
    private long cPay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public long getsPay() {
        return sPay;
    }

    public void setsPay(long sPay) {
        this.sPay = sPay;
    }

    public long getcPay() {
        return cPay;
    }

    public void setcPay(long cPay) {
        this.cPay = cPay;
    }
}
