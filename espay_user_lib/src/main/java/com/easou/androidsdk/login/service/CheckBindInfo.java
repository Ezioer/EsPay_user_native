package com.easou.androidsdk.login.service;

public class CheckBindInfo {
    private String userId;
    private String status;
    private String u;

    public CheckBindInfo(String userId, String status, String u) {
        this.userId = userId;
        this.status = status;
        this.u = u;
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
