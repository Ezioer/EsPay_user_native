package com.easou.androidsdk.login;

import android.app.Activity;
import android.content.Context;

import com.easou.androidsdk.login.para.AuthParametric;
import com.easou.androidsdk.login.para.OAuthParametric;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.JBean;
import com.easou.androidsdk.login.service.JBody;
import com.easou.androidsdk.login.service.JUser;
import com.easou.androidsdk.login.service.RequestInfo;
import com.easou.androidsdk.login.util.CookieUtil;

public class UserAPI {

	private static AuthParametric<RequestInfo> authPara = new OAuthParametric();

	private static EucService eucService = null;

	/**
	 * 根据用户ID获取信息
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<JUser> getUserById(long id, RequestInfo info,
			Context _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.putContent("id", id);
		JBean jbean = eucService.getResult("/api2/getUserById.json", jbody,
				authPara, info);
		return buildResult(jbean);
	}

	/**
	 * 更新用户信息
	 * 
	 * @param token
	 * @param jUser
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<JUser> updateUser(String token, JUser jUser,
			RequestInfo info, Activity _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put(CookieUtil.COOKIE_TOKEN, token);
		if (jUser != null) {
			if (jUser.getNickName() != null && !"".equals(jUser.getNickName())) {
				jbody.put("nickName", jUser.getNickName());
			}
			if (jUser.getOccuId() != null) {
				jbody.put("occuId", jUser.getOccuId());
			}
			if (jUser.getBirthday() != null) {
				jbody.put("birthday", jUser.getBirthday());
			}
			if (jUser.getSex() != null) {
				jbody.put("sex", jUser.getSex());
			}
			if (jUser.getCity() != null && !"".equals(jUser.getCity())) {
				jbody.put("city", jUser.getCity());
			}
		}
		JBean jbean = eucService.getResult("/api2/updateUser.json", jbody,
				authPara, info);
		return buildResult(jbean);
	}

	/**
	 * 更改密码
	 * 
	 * @param token
	 * @param jUser
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> updatePasswd(String token,
			String oldPass, String newPass, RequestInfo info, Context _activity)
			throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put(CookieUtil.COOKIE_TOKEN, token);
		jbody.put("oldPass", oldPass);
		jbody.put("newPass", newPass);
		JBean jbean = eucService.getResult("/api2/updatePasswd.json", jbody,
				authPara, info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
	}

	/**
	 * 更改密码
	 * 
	 * @param token
	 * @param jUser
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> updatePasswd(String token,
			String newPass, RequestInfo info, Activity _activity)
			throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put(CookieUtil.COOKIE_TOKEN, token);
		jbody.put("newPass", newPass);
		JBean jbean = eucService.getResult("/api2/updatePasswd2.json", jbody,
				authPara, info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
	}

	/**
	 * 获取解绑手机验证码
	 * 
	 * @param token
	 * @param mobile
	 * @param info
	 * @param _activity
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> requesNuBindMobile(String token,
			RequestInfo info, Activity _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put(CookieUtil.COOKIE_TOKEN, token);
		JBean jbean = eucService.getResult("/api2/requestBindMobile.json",
				jbody, authPara, info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
	}

	/**
	 * 获得绑定手机验证码
	 * 
	 * @param token
	 * @param mobile
	 * @param info
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> requestBindMobile(String token,
			String mobile, RequestInfo info, Context _activity, boolean isBind)
			throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put(CookieUtil.COOKIE_TOKEN, token);
		jbody.put("mobile", mobile);
		JBean jbean;
		if (isBind) {
			// 获取绑定验证码
			jbean = eucService.getResult("/api2/requestBindMobile.json", jbody,
					authPara, info);
		} else {
			// 获取解绑验证码
			jbean = eucService.getResult("/api2/requestUnBindMobile.json", jbody,
					authPara, info);
		}
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
	}

	/**
	 * 提交手机绑定
	 * 
	 * @param token
	 * @param mobile
	 * @param veriCode
	 * @param info
	 * @return 用户信息
	 * @throws EucAPIException
	 */
	public static EucApiResult<JUser> applyBindMobile(String token,
			String mobile, String veriCode, RequestInfo info,
			Context _activity, boolean isBind) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put(CookieUtil.COOKIE_TOKEN, token);
		jbody.put("mobile", mobile);
		jbody.put("veriCode", veriCode);
		JBean jbean = null;
		if (isBind) {
			// 绑定操作
			jbean = eucService.getResult("/api2/applyBindMobile.json", jbody,
					authPara, info);
		} else {
			// 解绑操作
			jbean = eucService.getResult("/api2/applyUnBindMobile.json", jbody,
					authPara, info);
		}

		return buildResult(jbean);
	}

	private static EucApiResult<JUser> buildResult(JBean jbean)
			throws EucAPIException {
		EucApiResult<JUser> result = new EucApiResult<JUser>(jbean);
		if (jbean.getBody() != null) {
			JUser juser = jbean.getBody().getObject("user", JUser.class);
			result.setResult(juser);
		}
		return result;
	}
}
