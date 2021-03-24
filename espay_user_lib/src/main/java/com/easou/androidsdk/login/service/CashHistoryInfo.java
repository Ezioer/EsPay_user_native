package com.easou.androidsdk.login.service;

import java.util.List;

public class CashHistoryInfo {
    private int page;
    private int size;
    private int drawSum;
    private int drawSize;
    private List<CashHistoryBean> drawList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDrawSum() {
        return drawSum;
    }

    public void setDrawSum(int drawSum) {
        this.drawSum = drawSum;
    }

    public int getDrawSize() {
        return drawSize;
    }

    public void setDrawSize(int drawSize) {
        this.drawSize = drawSize;
    }

    public List<CashHistoryBean> getDrawList() {
        return drawList;
    }

    public void setDrawList(List<CashHistoryBean> drawList) {
        this.drawList = drawList;
    }
}
