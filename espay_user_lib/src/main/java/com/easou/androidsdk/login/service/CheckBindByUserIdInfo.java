package com.easou.androidsdk.login.service;

public class CheckBindByUserIdInfo {
    private String userId;
    private String status;
    private String u;
    private String openId;

    public CheckBindByUserIdInfo(String userId, String status, String u, String openId) {
        this.userId = userId;
        this.status = status;
        this.u = u;
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
