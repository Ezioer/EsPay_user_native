package com.easou.androidsdk.login.service;

public class WxBindCodeInfo {
    private String bindCode;
    private String bindCodeHtml;

    public WxBindCodeInfo(String bindCode, String bindCodeHtml) {
        this.bindCode = bindCode;
        this.bindCodeHtml = bindCodeHtml;
    }

    public String getBindCode() {
        return bindCode;
    }

    public void setBindCode(String bindCode) {
        this.bindCode = bindCode;
    }

    public String getBindCodeHtml() {
        return bindCodeHtml;
    }

    public void setBindCodeHtml(String bindCodeHtml) {
        this.bindCodeHtml = bindCodeHtml;
    }
}
