package com.easou.androidsdk.login.service;

import java.util.List;

public class CashHistoryInfo {
    private int page;
    private int size;
    private int count;
    private List<CashHistoryBean> logList;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CashHistoryBean> getLogList() {
        return logList;
    }

    public void setLogList(List<CashHistoryBean> logList) {
        this.logList = logList;
    }
}
