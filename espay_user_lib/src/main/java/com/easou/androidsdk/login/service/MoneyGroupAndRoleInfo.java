package com.easou.androidsdk.login.service;

import java.util.List;

public class MoneyGroupAndRoleInfo {
    private MoneyBaseInfo info;
    private List<MoneyGroupInfo> groupInfo;

    public MoneyBaseInfo getInfo() {
        return info;
    }

    public void setInfo(MoneyBaseInfo info) {
        this.info = info;
    }

    public List<MoneyGroupInfo> getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(List<MoneyGroupInfo> groupInfo) {
        this.groupInfo = groupInfo;
    }
}
