package com.easou.androidsdk.login.service;

import java.util.List;

public class MoneyList {
    private List<MoneyListDetail> bonusList;

    private int count;
    private int page;
    private String size;

    public List<MoneyListDetail> getBonusList() {
        return bonusList;
    }

    public void setBonusList(List<MoneyListDetail> bonusList) {
        this.bonusList = bonusList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
