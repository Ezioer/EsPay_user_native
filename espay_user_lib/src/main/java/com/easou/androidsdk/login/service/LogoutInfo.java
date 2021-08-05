package com.easou.androidsdk.login.service;

public class LogoutInfo {
    //注销条件
    private String cancellationCondition;
    //注销协议
    private String cancellationAgreement;
    //注销须知
    private String cancellationNotice;

    public LogoutInfo(String cancellationCondition, String cancellationAgreement, String cancellationNotice) {
        this.cancellationCondition = cancellationCondition;
        this.cancellationAgreement = cancellationAgreement;
        this.cancellationNotice = cancellationNotice;
    }

    public String getCancellationCondition() {
        return cancellationCondition;
    }

    public void setCancellationCondition(String cancellationCondition) {
        this.cancellationCondition = cancellationCondition;
    }

    public String getCancellationAgreement() {
        return cancellationAgreement;
    }

    public void setCancellationAgreement(String cancellationAgreement) {
        this.cancellationAgreement = cancellationAgreement;
    }

    public String getCancellationNotice() {
        return cancellationNotice;
    }

    public void setCancellationNotice(String cancellationNotice) {
        this.cancellationNotice = cancellationNotice;
    }
}
