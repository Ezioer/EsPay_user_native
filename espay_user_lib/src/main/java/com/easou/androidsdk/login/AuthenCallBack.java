package com.easou.androidsdk.login;

/**
 * created by xiaoqing.zhou
 * on 2020/8/6
 * fun
 */
public interface AuthenCallBack {
    void loginSuccess(String birthdate);

    void loginFail(String msg);
}
