package com.easou.androidsdk.login.service;

import java.util.List;

/**
 * created by xiaoqing.zhou
 * on 2020/8/12
 * fun
 */
public class GiftBean {
    private String msg;
    private int resultCode;
    private List<GiftInfo> rows;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public List<GiftInfo> getRows() {
        return rows;
    }

    public void setRows(List<GiftInfo> rows) {
        this.rows = rows;
    }
}
