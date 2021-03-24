package com.easou.androidsdk.login.service;

/**
 * created by xiaoqing.zhou
 * on 2020/8/12
 * fun
 */
public class GiftInfo {
    private long activityId;
    private String code;
    private long codeId;
    private String giftinfo;
    private String giftname;
    //0未领取 1领取
    private int status;
    private int ps;
    private int us;

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getUs() {
        return us;
    }

    public void setUs(int us) {
        this.us = us;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    public String getGiftinfo() {
        return giftinfo;
    }

    public void setGiftinfo(String giftinfo) {
        this.giftinfo = giftinfo;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
