package com.easou.androidsdk.login.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.content.Context;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.httpclient.EucHttpClient;
import com.easou.androidsdk.login.para.AuthParametric;
import com.easou.androidsdk.login.util.GsonUtil;
import com.easou.androidsdk.util.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

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
            apiServer = CommonUtils.readPropertiesValue(_context, "api");
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
//        head.setFcm(true);
        return head;
    }

    /**
     * @param path  请求路径
     * @param jBody
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
        if (result.equals("502")) {
            JBean jBean = new JBean();
            JHead jHead1 = new JHead();
            jHead1.setRet("502");
            jBean.setHead(jHead1);
            return jBean;
        }
        // 返回结果
        JBean bean = GsonUtil.extraJsonBean(result);
        return bean;
    }


    public String getHelpUrl(String path) {
        Map map = new HashMap();
        map.put("appid", appId);
        String result = EucHttpClient.httpGet(path, map);
        if (result == null || "".equals(result)) {
            return "";
        }
        HelpServiceInfo info = GsonUtil.fromJson(result, HelpServiceInfo.class);
        if (info.getStatus() == 1) {
            try {
                return URLDecoder.decode(info.getMsg(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public GiftBean getGifts(String path) {
        Map map = new HashMap();
        map.put("appid", appId);
        map.put("userid", Constant.ESDK_USERID);
        String result = EucHttpClient.httpGet(path, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        GiftBean bean = new GiftBean();
        try {
            JSONObject object = new JSONObject(result);
            int code = object.optInt("resultCode");
            String msg = object.optString("msg");
            bean.setMsg(msg);
            bean.setResultCode(code);
            if (code == 1) {
                String list = object.optString("rows");
                List<GiftInfo> info = new Gson().fromJson(list, new TypeToken<List<GiftInfo>>() {
                }.getType());
                bean.setRows(info);
            }
        } catch (JSONException e) {

        }
        return bean;
    }

    public GiftBean giftsUse(String path, String activityid) {
        Map map = new HashMap();
        map.put("activityid", activityid);
        map.put("userid", Constant.ESDK_USERID);
        String result = EucHttpClient.httpGet(path, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        GiftBean bean = new GiftBean();
        try {
            JSONObject object = new JSONObject(result);
            int code = object.optInt("resultCode");
            String msg = object.optString("msg");
            bean.setMsg(msg);
            bean.setResultCode(code);
        } catch (JSONException e) {
            return null;
        }
        return bean;
    }


    //获取红包界面数据
    public MoneyBaseInfo getMoneyInfo(String playerId, String serverId) {
        Map map = getDefaultMap();
        map.put("playerId", playerId);
        map.put("serverId", serverId);
        BaseResponse info = getBaseResponse("http://sdkapi.eayou.com/luckyMoney/baseInfo", map);
        if (info == null) {
            return null;
        }
        MoneyBaseInfo moneyBaseInfo = GsonUtil.fromJson(info.getData().toString(), MoneyBaseInfo.class);
        return moneyBaseInfo;
    }

    //获取红包列表
    public MoneyListInfo getMoneyList(String playerId, String serverId) {
        Map map = getDefaultMap();
        map.put("playerId", playerId);
        map.put("serverId", serverId);
        BaseResponse info = getBaseResponse("http://sdkapi.eayou.com/luckyMoney/taskList", map);
        if (info == null) {
            return null;
        }
        MoneyListInfo listInfo = GsonUtil.fromJson(info.getData().toString(), MoneyListInfo.class);
        return listInfo;
    }

    //获取提现数据
    public CashLevelInfo getCashInfo(String playerId, String serverId) {
        Map map = getDefaultMap();
        map.put("playerId", playerId);
        map.put("serverId", serverId);
        BaseResponse info = getBaseResponse("http://sdkapi.eayou.com/luckyMoney/drawInfo", map);
        if (info == null) {
            return null;
        }
        CashLevelInfo listInfo = GsonUtil.fromJson(info.getData().toString(), CashLevelInfo.class);
        return listInfo;
    }

    //提现
    public DrawResultInfo getCash(String playerId, String money, String serverId) {
        Map map = getDefaultMap();
        map.put("playerId", playerId);
        map.put("drawMoney", money);
        map.put("serverId", serverId);
        BaseResponse info = getBaseResponse("http://sdkapi.eayou.com/luckyMoney/draw", map);
        if (info == null) {
            return null;
        }
        DrawResultInfo resultInfo = GsonUtil.fromJson(info.getData().toString(), DrawResultInfo.class);
        return resultInfo;
    }

    //提现记录
    public CashHistoryInfo getCashHistory(String playerId, String serverId) {
        Map map = getDefaultMap();
        map.put("playerId", playerId);
        map.put("serverId", serverId);
        map.put("page", "1");
        map.put("size", "10000");
        BaseResponse info = getBaseResponse("http://sdkapi.eayou.com/luckyMoney/drawLog", map);
        if (info == null) {
            return null;
        }
        CashHistoryInfo resultInfo = GsonUtil.fromJson(info.getData().toString(), CashHistoryInfo.class);
        return resultInfo;
    }

    //完成红包任务
    public GainMoneyInfo getMoneyTask(String playerId, int moneyId, String serverId) {
        Map map = getDefaultMap();
        map.put("playerId", playerId);
        map.put("serverId", serverId);
        map.put("luckyMoneyId", String.valueOf(moneyId));
        BaseResponse info = getBaseResponse("http://sdkapi.eayou.com/luckyMoney/gain", map);
        if (info == null) {
            return null;
        }
        GainMoneyInfo gainMoneyInfo = GsonUtil.fromJson(info.getData().toString(), GainMoneyInfo.class);
        return gainMoneyInfo;
    }

    /**
     * 获取相应数据
     *
     * @param url 请求url
     * @param map 请求体
     * @param <T> 响应数据实体类型
     * @return
     */
    public <T> BaseResponse<T> getNetData(String url, Map map) {
        BaseResponse info = getBaseResponse(url, map);
        if (info == null) {
            return null;
        }
        T data = GsonUtil.fromJson(info.getData().toString(), new TypeToken<T>() {
        }.getType());
        info.setData(data);
        return info;
    }

    //获取通用响应体
    private BaseResponse getBaseResponse(String url, Map map) {
        String result = EucHttpClient.httpPost(url, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        BaseResponse bean = new BaseResponse();
        try {
            JSONObject object = new JSONObject(result);
            int code = object.optInt("resultCode");
            String info = object.optString("resultInfo");
            if (object.opt("data") != null) {
                bean.setData(object.opt("data").toString());
            }
            bean.setResultInfo(info);
            bean.setResultCode(code);
            if (code != 1) {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
        return bean;
    }

    private Map getDefaultMap() {
        Map map = new HashMap();
        map.put("os", "Android");
        map.put("esId", Constant.ESDK_USERID);
        map.put("appId", appId);
        return map;
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

    public PayLimitInfo getPayLimit(String path, int age) {
        Map map = new HashMap();
        map.put("age", age);
        String result = EucHttpClient.httpGet(path, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        PayLimitInfo info = GsonUtil.fromJson(result, PayLimitInfo.class);
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