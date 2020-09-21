package com.easou.androidsdk.login;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

import com.easou.androidsdk.login.service.PayLimitInfo;
import com.easou.androidsdk.login.httpclient.EucHttpClient;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.Md5SignUtil;
import com.easou.androidsdk.login.service.PayBean;
import com.easou.androidsdk.login.util.GsonUtil;
import com.easou.androidsdk.util.CommonUtils;
public class PayAPI {

	/**
	 * 获得用户e币余额
	 * @param userId
	 * @return
	 */
	public static PayBean getUserCurrency(String userId, Activity _activity) {
		StringBuffer urlBuffer = new StringBuffer(
				CommonUtils.readPropertiesValue(_activity,"payment.url"));
		urlBuffer.append("/json/userEb.e");
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("userId", userId);
        paraMap.put("partnerId", CommonUtils.readPropertiesValue(_activity, "partnerId"));
        String sign = Md5SignUtil.sign(paraMap, CommonUtils.readPropertiesValue(_activity, "secertKey"));
        paraMap.put("sign", sign);
        String json = EucHttpClient.httpGet(urlBuffer.toString(), paraMap);
        if (null == json)
            return null;
        return GsonUtil.fromJson(json, PayBean.class);
    }

    protected static EucService eucService = null;

    /**
     * 获得订单限制信息
     *
     * @return
     */
    public static PayLimitInfo getPayLimitInfo(Context _activity, int age) {
        try {
            eucService = EucService.getInstance(_activity);
            PayLimitInfo payLimit = eucService.getPayLimit("https://egamec.eayou.com/pl/info?", age);
            return payLimit;
        } catch (Exception e) {
            return null;
        }
    }
}
