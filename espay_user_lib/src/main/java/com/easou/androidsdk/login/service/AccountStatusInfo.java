package com.easou.androidsdk.login.service;

public class AccountStatusInfo {
    private int canApply;
    //是否能申请,0不能,1能
    private String remarks;
    private int isRemind;
    //是否提醒过用户,0没有提醒过(默认)，1提醒过用户,当注销工单状态被驳回时，
    //第一次登需要提醒用户
    private Long createTime;
    private int status;
    //1代表系统审核中、2系统审核不通过、3等待注销、4注销驳回、5注销成功、6用户取消、7订单完结。
    private int accountStatus;
    //1初始状态、2发起注销状态、3等待注销状态、4注销驳回状态、5注销完成状态


    public AccountStatusInfo(int canApply, String remarks, int isRemind, Long createTime, int status, int accountStatus) {
        this.canApply = canApply;
        this.remarks = remarks;
        this.isRemind = isRemind;
        this.createTime = createTime;
        this.status = status;
        this.accountStatus = accountStatus;
    }

    public int getCanApply() {
        return canApply;
    }

    public void setCanApply(int canApply) {
        this.canApply = canApply;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(int isRemind) {
        this.isRemind = isRemind;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }
}
