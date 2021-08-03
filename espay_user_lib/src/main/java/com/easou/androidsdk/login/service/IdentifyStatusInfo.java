package com.easou.androidsdk.login.service;

/**
 * created by xiaoqing.zhou
 * on 2020/8/10
 * fun
 */
public class IdentifyStatusInfo {
    private boolean isHoliday;
    private int autoRegister;
    private int userIdentityStatus;

    public IdentifyStatusInfo(boolean isHoliday, int autoRegister, int userIdentityStatus) {
        this.isHoliday = isHoliday;
        this.autoRegister = autoRegister;
        this.userIdentityStatus = userIdentityStatus;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public int getAutoRegister() {
        return autoRegister;
    }

    public void setAutoRegister(int autoRegister) {
        this.autoRegister = autoRegister;
    }

    public int getUserIdentityStatus() {
        return userIdentityStatus;
    }

    public void setUserIdentityStatus(int userIdentityStatus) {
        this.userIdentityStatus = userIdentityStatus;
    }
}
