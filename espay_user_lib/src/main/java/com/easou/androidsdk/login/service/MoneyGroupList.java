package com.easou.androidsdk.login.service;

import java.util.List;

public class MoneyGroupList {
    private int count;
    private List<MoneyGroupInfo> groupList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<MoneyGroupInfo> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<MoneyGroupInfo> groupList) {
        this.groupList = groupList;
    }
}
