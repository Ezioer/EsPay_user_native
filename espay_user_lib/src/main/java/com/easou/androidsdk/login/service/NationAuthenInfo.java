package com.easou.androidsdk.login.service;

public class NationAuthenInfo {
    private String identityNum;
    private String identityName;
    private int identityStatus;

    public NationAuthenInfo(String identityNum, String identityName, int identityStatus) {
        this.identityNum = identityNum;
        this.identityName = identityName;
        this.identityStatus = identityStatus;
    }

    public String getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(String identityNum) {
        this.identityNum = identityNum;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public int getIdentityStatus() {
        return identityStatus;
    }

    public void setIdentityStatus(int identityStatus) {
        this.identityStatus = identityStatus;
    }
}
