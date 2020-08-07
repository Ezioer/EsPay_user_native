package com.easou.androidsdk.login.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.content.Context;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.http.ApiInterface;
import com.easou.androidsdk.login.httpclient.EucHttpClient;
import com.easou.androidsdk.login.para.AuthParametric;
import com.easou.androidsdk.login.util.GsonUtil;
import com.easou.androidsdk.util.CommonUtils;

public class EucService {

    private String appId;

    private String partnerId;

    private String key;
    /*SDK版本*/
    private String version;

    private String apiServer;

    private String source;

    private String qn;

    private String paymentUrl;

    private static EucService instance = null;

    private EucService() {

    }

    public static EucService getInstance(Context _context) {
        if (null == instance) {
            instance = new EucService();
            Properties pro = getProperties(_context);
            instance.init(pro, _context);
        }
        return instance;
    }

    private static Properties getProperties(Context _context) {
        Properties pro = null;
        try {
            InputStream is = _context.getAssets().open("client.properties");
            pro = new Properties();
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return pro;
    }

    private void init(Properties pro, Context _context) {
        try {
            appId = CommonUtils.readPropertiesValue(_context, "appId");
            Constant.ESDK_APP_ID = appId;
            partnerId = CommonUtils.readPropertiesValue(_context, "partnerId");
            key = CommonUtils.readPropertiesValue(_context, "key");
            version = CommonUtils.readPropertiesValue(_context, "version");
            source = CommonUtils.readPropertiesValue(_context, "source");
            apiServer = ApiInterface.apiTest;
            qn = CommonUtils.readPropertiesValue(_context, "qn");
            paymentUrl = CommonUtils.readPropertiesValue(_context, "paymentUrl");
        } catch (Exception e) {
            //log.error(e, e);
            e.printStackTrace();
        }
    }

    /**
     * 构建请求头部
     *
     * @return
     */
    public JHead buildDefaultRequestHeader() {
        JHead head = new JHead();
        head.setAppId(appId);
        head.setVersion(version);
        head.setPartnerId(partnerId);
        head.setSource(source);
        head.setQn(qn);
        return head;
    }

    /**
     * @param path      请求路径
     * @param jHead     请求头部
     * @param jBody
     * @param eucParser
     * @return
     */
    public JBean getResult(String path, JBody jBody, AuthParametric authPara, RequestInfo info) {
        // 生成公共请求头
        JHead jHead = authPara.getVeriHeader(jBody, this, info);
        // 生成请求参数
        JBean jbean = new JBean();
        jbean.setHead(jHead);
        jbean.setBody(jBody);
        //请求参数
        String requestJSON = GsonUtil.toJson(jbean);
        // 请求接口并返回结果
        String result = executeUrl(buildURL(apiServer + path, info), requestJSON);
//		System.out.println("请求结果是：" + result);
        if (null == result || "".equals(result)) {
            return null;
        }
        // 返回结果
        JBean bean = GsonUtil.extraJsonBean(result);
        return bean;
    }

    public LimitStatusInfo getLimitSwitch(String path) {
        Map map = new HashMap();
        map.put("qn", qn);
        String result = EucHttpClient.httpGet(path, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        LimitStatusInfo info = GsonUtil.fromJson(result, LimitStatusInfo.class);
        return info;
    }

    /**
     * 执行HTTP URL请求,如果不使用本sdk提供的httpclient
     * 可直接用自己的http客户端替换
     *
     * @param url  请求地址，如果是https，不验证任何证书
     * @param json 请求的json内容，使用标准的post方式传送的内容
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String executeUrl(String url, String json) {
        Map paraMap = new HashMap();
        paraMap.put("json", json);
        String result = "";
        try {
            result = EucHttpClient.httpPost(url, paraMap);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 请求参数信息
     *
     * @param uri  uri信息
     * @param info 请求信息
     * @return
     */
    public String buildURL(String url, RequestInfo info) {
        if (info == null)
            return url;
        StringBuffer buffer = new StringBuffer();
        buffer.append(url).append("?").append(info.paraToString());
        return buffer.toString();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiServer() {
        return apiServer;
    }

    public void setApiServer(String apiServer) {
        this.apiServer = apiServer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQn() {
        return qn;
    }

    public void setQn(String qn) {
        this.qn = qn;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

}