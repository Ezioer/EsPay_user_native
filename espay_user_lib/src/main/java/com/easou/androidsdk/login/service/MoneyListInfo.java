package com.easou.androidsdk.login.service;

import java.util.List;

public class MoneyListInfo {
    private int luckyMoneySum;
    private int luckyMoneySize;
    private List<MoneyInfo> luckyMoneyList;

    public int getLuckyMoneySum() {
        return luckyMoneySum;
    }

    public void setLuckyMoneySum(int luckyMoneySum) {
        this.luckyMoneySum = luckyMoneySum;
    }

    public int getLuckyMoneySize() {
        return luckyMoneySize;
    }

    public void setLuckyMoneySize(int luckyMoneySize) {
        this.luckyMoneySize = luckyMoneySize;
    }

    public List<MoneyInfo> getLuckyMoneyList() {
        return luckyMoneyList;
    }

    public void setLuckyMoneyList(List<MoneyInfo> luckyMoneyList) {
        this.luckyMoneyList = luckyMoneyList;
    }
}
