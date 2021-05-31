package com.easou.androidsdk.login;

import android.app.Activity;
import android.content.Context;

import com.easou.androidsdk.login.para.AuthParametric;
import com.easou.androidsdk.login.para.OAuthParametric;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.EucToken;
import com.easou.androidsdk.login.service.EucUCookie;
import com.easou.androidsdk.login.service.JBean;
import com.easou.androidsdk.login.service.JBody;
import com.easou.androidsdk.login.service.JUser;
import com.easou.androidsdk.login.service.RequestInfo;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.Tools;

public class RegisterAPI {

	protected static EucService eucService = null;

	private static AuthParametric<RequestInfo> oAuthPara = new OAuthParametric();

	/**
	 * 自动注册
	 * 
	 * @param remember
	 *            是否需要回传记住密码cookie内容
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<AuthBean> autoRegist(boolean remember,
			RequestInfo info,Context _context) throws EucAPIException {
        eucService = EucService.getInstance(_context);
        JBody jbody = new JBody();
        if (remember) { // 为真传remember参数
            jbody.putContent("remember", remember);
        }
        jbody.putContent("deviceId", Tools.getDeviceImei(_context));
        JBean jbean = eucService.getResult("/api2/autoRegist.json", jbody, oAuthPara,
                info);
        if (jbean == null) {
            return null;
        }
        return buildAuthResult(jbean);
    }

	/**
	 * 用户名注册
	 * 
	 * @param username
	 *            注册用户名(不能重复)
	 * @param password
	 *            注册密码
	 * @param remember
	 *            是否需要回传记住密码cookie内容
	 * @param info
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<AuthBean> registByName(String username,
			String password, boolean remember, String bookNum, RequestInfo info,Context _activity)
			throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.putContent("username", username);
        jbody.putContent("password", password);
        if (remember) { // 为真传remember参数
            jbody.putContent("remember", remember);
        }
        jbody.putContent("deviceId", Tools.getDeviceImei(_activity));
        jbody.putContent("bookNum", bookNum);
        JBean jbean = eucService.getResult("/api2/registByName.json", jbody, oAuthPara,
                info);
        return buildAuthResult(jbean);
    }

	/**
	 * 预约用户名，预约成功会返回一个预约名和预约号，用户名注册时可带上该预约号
	 * 
	 * @param info
	 * @return	返回的字符串内容为{预约名},{预约号}
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> bookingName(RequestInfo info,Activity _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		JBean jbean = eucService.getResult("/api2/bookingName.json", jbody,oAuthPara,info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		if (result.getResultCode() != null && result.getResultCode().equals(CodeConstant.OK) && jbean.getBody()!=null) {
			result.setResult(jbean.getBody().getString("bookName") + "," + jbean.getBody().getString("bookNum"));
		}
		return result;
	}
	
	/**
	 * 检测用户名
	 * @param username
	 * @param info
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> checkName(String username,
			RequestInfo info,Activity _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.putContent("username", username);
		JBean jbean = eucService.getResult("/api2/checkName.json", jbody, oAuthPara,
				info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
	}

	/**
	 * 手机验证码注册
	 * 
	 * @param mobile
	 *            注册手机号
	 * @param password
	 *            注册密码
	 * @param veriCode
	 *            注册验证码
	 * @param remember
	 *            是否需要回传记住密码cookie内容
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<AuthBean> regByMobileCode(String mobile,
			String password, String veriCode, boolean remember, RequestInfo info,Activity _activity)
			throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.putContent("mobile", mobile);
		jbody.putContent("password", password);
		jbody.putContent("veriCode", veriCode);
		if (remember) { // 为真传remember参数
			jbody.putContent("remember", remember);
		}
		JBean jbean = eucService.getResult("/api2/regByMobileCode.json", jbody,
				oAuthPara, info);
		return buildAuthResult(jbean);
	}

	/**
	 * 获得手机验证码，服务端会给该手机号发送一个验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> requestMobileCode(String mobile,
			RequestInfo info,Context _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.putContent("mobile", mobile);
		JBean jbean = eucService.getResult("/api2/requestMobileCode.json", jbody,
				oAuthPara, info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
	}

	private static EucApiResult<AuthBean> buildAuthResult(JBean jbean)
			throws EucAPIException {
		EucApiResult<AuthBean> result = new EucApiResult<AuthBean>(
				jbean);
		if (result.getResultCode() != null && result.getResultCode().equals(CodeConstant.OK)) {
			JUser juser = jbean.getBody().getObject("user", JUser.class);
			EucToken token=jbean.getBody().getObject("token", EucToken.class);
			EucUCookie u = jbean.getBody().getObject("U", EucUCookie.class);
			AuthBean eucAuthResult = new AuthBean(token, juser, u,
					String.valueOf(juser.getId()), false);
			result.setResult(eucAuthResult);
		}
		return result;
	}

	/**
	 * 获取用于登录的RequestInfo信息
	 *
	 * @return
	 */
	public static RequestInfo getRequestInfo(Context context) {
        RequestInfo requestInfo = null;
        if (null == requestInfo) {
            requestInfo = new RequestInfo();
            requestInfo.setAgent("SDK_Android_Client");
            if (CommonUtils.getNationIdentity(context) == 0) {
                requestInfo.setFcm(false);
            } else {
                requestInfo.setFcm(true);
            }
            requestInfo.setQn(CommonUtils.readPropertiesValue(context, "qn"));
            requestInfo.setAppId(CommonUtils.readPropertiesValue(context, "appId"));
            requestInfo.setSource(CommonUtils.readPropertiesValue(context, "source"));
        }
        return requestInfo;
    }

    /**
     * 获取用于登录的RequestInfo信息
     *
     * @return
     */
    public static RequestInfo getTapRequestInfo(Context context) {
        RequestInfo requestInfo = null;
        if (null == requestInfo) {
            requestInfo = new RequestInfo();
            requestInfo.setAgent("SDK_Android_Client");
            requestInfo.setQn(CommonUtils.readPropertiesValue(context, "qn"));
            requestInfo.setAppId(CommonUtils.readPropertiesValue(context, "appId"));
            requestInfo.setSource("TSSO");
        }
        return requestInfo;
    }
}
