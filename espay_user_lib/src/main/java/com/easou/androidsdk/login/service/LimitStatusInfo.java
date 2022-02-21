package com.easou.androidsdk.login.service;

/**
 * created by xiaoqing.zhou
 * on 2020/8/6
 * fun
 */
public class LimitStatusInfo {
    private int us = 0;
    private int ps = 0;
    private int ls = 0;
    private int ns = 0;
    private int rns = 0;//国家实名认证开关
    private int lus = 0;//用户上下线日志上报开关
    private int cs = 0;//注销功能开关

    public int getRns() {
        return rns;
    }


    public int getCs() {
        return cs;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public void setRns(int rns) {
        this.rns = rns;
    }

    public int getLus() {
        return lus;
    }

    public void setLus(int lus) {
        this.lus = lus;
    }

    public int getNs() {
        return ns;
    }

    public void setNs(int ns) {
        this.ns = ns;
    }

    public int getUs() {
        return us;
    }

    public void setUs(int us) {
        this.us = us;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getLs() {
        return ls;
    }

    public void setLs(int ls) {
        this.ls = ls;
    }
}
