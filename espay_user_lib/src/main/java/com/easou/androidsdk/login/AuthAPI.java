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
import com.easou.androidsdk.login.service.LUser;
import com.easou.androidsdk.login.service.LimitStatusInfo;
import com.easou.androidsdk.login.service.LoginBean;
import com.easou.androidsdk.login.service.RequestInfo;
import com.easou.androidsdk.login.util.CookieUtil;

public class AuthAPI {

    protected static EucService eucService = null;

    public static Context mContext = null;
    private static AuthParametric<RequestInfo> oAuthPara = new OAuthParametric();

    /**
     * 用户名密码登录
     *
     * @param userName
     * @param password
     * @param remember
     * @param info
     * @return
     * @throws EucAPIException
     */
    public static EucApiResult<LoginBean> login(String username,
                                               String password, boolean remember, RequestInfo info, Context _activity)
            throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.putContent("username", username);
        jbody.putContent("password", password);
        jbody.putContent("remember", remember);
        JBean jbean = eucService.getResult("/api2/login.json", jbody,
                oAuthPara, info);
        return buildLoginResult(jbean);
    }

    /**
     * TOKEN验证接口
     *
     * @param token
     * @param info
     * @return
     * @throws EucAPIException
     */
    public static EucApiResult<AuthBean> validate(String token, RequestInfo info, Activity _activity)
            throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        JBean jbean = eucService.getResult("/api2/validate.json", jbody,
                oAuthPara, info);
        return buildAuthResult(jbean);
    }


    /**
     * TOKEN验证接口
     *
     * @param token
     * @param info
     * @return
     * @throws EucAPIException
     */
    public static EucApiResult<AuthBean> validate(String token, String U, RequestInfo info, Activity _activity)
            throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.putContent(CookieUtil.COOKIE_U, U);
        JBean jbean = eucService.getResult("/api2/validate.json", jbody,
                oAuthPara, info);
        return buildAuthResult(jbean);
    }


    /**
     * 验证SERVICE TICKET
     *
     * @param request
     * @param response
     * @return
     * @throws EucAPIException
     */
    public static EucApiResult<AuthBean> validateServiceTicket(String ticket,
                                                               RequestInfo info, Activity _activity) throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.put("ticket", ticket);
        JBean jbean = eucService.getResult("/api2/validateServiceTicket.json",
                jbody, oAuthPara, info);
        return buildAuthResult(jbean);
    }

    /**
     * 忘记密码请求重设密码
     *
     * @param mobile 要重设密码的手机号
     * @param info
     * @return 没有返回结果，通过result.getResultCode()判断请求是否成功
     * @throws EucAPIException
     */
    public static EucApiResult<String> requestResetPass(String mobile,
                                                        RequestInfo info, Context _activity) throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.put("mobile", mobile);
        JBean jbean = eucService.getResult("/api2/requestResetPass.json",
                jbody, oAuthPara, info);
        EucApiResult<String> result = new EucApiResult<String>(jbean);
        return result;
    }

    /**
     * 忘记密码重设密码，通过手机号判断重设哪个用户
     *
     * @param mobile 手机号
     * @param info
     * @return
     * @throws EucAPIException
     */
    public static EucApiResult<AuthBean> applyResetPass(String mobile,
                                                        String newpwd, String veriCode, RequestInfo info, Context _activity)
            throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.put("mobile", mobile);
        jbody.put("newpwd", newpwd);
        jbody.put("veriCode", veriCode);
        JBean jbean = eucService.getResult("/api2/applyResetPass.json", jbody,
                oAuthPara, info);
        return buildAuthResult(jbean);
    }

	/**
	 * 实名认证
	 * @param name
	 * @param idNum
	 * @param userId
	 * @param appId
	 * @param info
	 * @param _activity
	 * @return
	 * @throws EucAPIException
	 */
    public static EucApiResult<String> userIdentify(String name, String idNum, String userId, String appId, RequestInfo info, Context _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put("userId", userId);
		jbody.put("identityNum", idNum);
		jbody.put("identityName", name);
		jbody.put("appId", appId);
		JBean jbean = eucService.getResult("/api2/saveUserIdentity.json",
				jbody, oAuthPara, info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		return result;
    }

    public static LimitStatusInfo getLimitStatue(Context activity){
        eucService = EucService.getInstance(activity);
        LimitStatusInfo limitSwitch = eucService.getLimitSwitch("https://egamec.eayou.com/c3s/as?");
        return limitSwitch;
    }

	/**
	 * 实名认证状态
	 *
	 * @param info
	 * @return
	 * @throws EucAPIException
	 */
	public static EucApiResult<String> identifyStatus(String deviceId,RequestInfo info,Context _activity) throws EucAPIException {
		eucService = EucService.getInstance(_activity);
		JBody jbody = new JBody();
		jbody.put("deviceId", deviceId);
		JBean jbean = eucService.getResult("/api2/getUserIdentityStatus.json", jbody,oAuthPara,info);
		EucApiResult<String> result = new EucApiResult<String>(jbean);
		if (result.getResultCode() != null && result.getResultCode().equals(CodeConstant.OK) && jbean.getBody()!=null) {
			int isHoliday = (int) jbean.getBody().get("isHoliday");
			int autoRegist = (int) jbean.getBody().get("autoRegistLimitByDeviceId");
			int identifyStatue = (int) jbean.getBody().get("userIdentityStatus");
		}
		return result;
	}

    private static EucApiResult<AuthBean> buildAuthResult(JBean jbean)
            throws EucAPIException {
        EucApiResult<AuthBean> result = new EucApiResult<AuthBean>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            JUser juser = jbean.getBody().getObject("user", JUser.class);
            EucToken token = jbean.getBody().getObject("token", EucToken.class);
            EucUCookie u = jbean.getBody().getObject("U", EucUCookie.class);
            AuthBean eucAuthResult = new AuthBean(token, juser, u,
                    String.valueOf(juser.getId()), false);
            result.setResult(eucAuthResult);
        }
        return result;
    }

    private static EucApiResult<LoginBean> buildLoginResult(JBean jbean)
            throws EucAPIException {
        EucApiResult<LoginBean> result = new EucApiResult<LoginBean>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            LUser juser = jbean.getBody().getObject("user", LUser.class);
            EucToken token = jbean.getBody().getObject("token", EucToken.class);
            EucUCookie u = jbean.getBody().getObject("U", EucUCookie.class);
            LoginBean eucAuthResult = new LoginBean(token, juser, u,
                    String.valueOf(juser.getId()), false);
            result.setResult(eucAuthResult);
        }
        return result;
    }
}