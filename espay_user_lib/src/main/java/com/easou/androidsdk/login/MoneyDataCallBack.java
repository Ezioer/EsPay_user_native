package com.easou.androidsdk.login;

public interface MoneyDataCallBack<T> {
    void success(T t);

    void fail(String msg);
}
