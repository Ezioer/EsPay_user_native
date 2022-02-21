package com.easou.androidsdk.login;

import android.app.Activity;
import android.content.Context;

import com.easou.androidsdk.login.para.AuthParametric;
import com.easou.androidsdk.login.para.OAuthParametric;
import com.easou.androidsdk.login.service.AccountStatusInfo;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.CheckBindInfo;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.EucToken;
import com.easou.androidsdk.login.service.EucUCookie;
import com.easou.androidsdk.login.service.IdentifyStatusInfo;
import com.easou.androidsdk.login.service.JBean;
import com.easou.androidsdk.login.service.JBody;
import com.easou.androidsdk.login.service.JUser;
import com.easou.androidsdk.login.service.LUser;
import com.easou.androidsdk.login.service.LimitStatusInfo;
import com.easou.androidsdk.login.service.LimitTimesInfo;
import com.easou.androidsdk.login.service.LoginBean;
import com.easou.androidsdk.login.service.LogoutInfo;
import com.easou.androidsdk.login.service.NationAuthenInfo;
import com.easou.androidsdk.login.service.RequestInfo;
import com.easou.androidsdk.login.util.CookieUtil;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.Tools;

import java.net.URLDecoder;

public class AuthAPI {

    protected static EucService eucService = null;

    public static Context mContext = null;
    private static AuthParametric<RequestInfo> oAuthPara = new OAuthParametric();

    /**
     * 用户名密码登录
     *
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
        jbody.putContent("deviceId", Tools.getDeviceImei(_activity));
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
    public static EucApiResult<LoginBean> validate(String token, String U, RequestInfo info, Activity _activity)
            throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.putContent(CookieUtil.COOKIE_U, U);
        JBean jbean = eucService.getResult("/api2/validate.json", jbody,
                oAuthPara, info);
        return buildLoginResult(jbean);
    }

    /**
     * TOKEN验证接口
     *
     * @param info
     * @return
     * @throws EucAPIException
     */
    public static EucApiResult<LoginBean> validateU(String U, RequestInfo info, Activity _activity)
            throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_U, U);
        jbody.putContent("remember", true);
        JBean jbean = eucService.getResult("/api2/validate.json", jbody,
                oAuthPara, info);
        return buildLoginResult(jbean);
    }


    /**
     * 验证SERVICE TICKET
     *
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
     *
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

    public static EucApiResult<String> userIdentifyNation(String name, String idNum, String userId, String deviceId, RequestInfo info, Context _activity) throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.put("userId", userId);
        jbody.put("identityNum", idNum);
        jbody.put("identityName", name);
        jbody.put("deviceId", deviceId);
        JBean jbean = eucService.getResult("/api2/saveUserIdentity2.json",
                jbody, oAuthPara, info);
        EucApiResult<String> result = new EucApiResult<String>(jbean);
        return result;
    }

    //查询国家实名认证状态
    public static EucApiResult<NationAuthenInfo> queryNationIdentify(String userId, String deviceId, RequestInfo info, Context _activity) throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.put("userId", userId);
        jbody.put("deviceId", deviceId);
        JBean jbean = eucService.getResult("/api2/queryUserIdentity2.json",
                jbody, oAuthPara, info);
        return buildNationAuthen(jbean);
    }

    /**
     * 检查是否绑定第三方账号
     *
     * @param thirdType 1：taptap
     * @param openid    第三方账号
     */
    public static EucApiResult<CheckBindInfo> checkIsBindThird(Context activity, int thirdType, String openid, RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.put("thirdPartySsoId", thirdType);
        jbody.put("openId", openid);
        JBean jbean = eucService.getResult("/api2/findThirdpartySsoBind.json",
                jbody, oAuthPara, info);
        return buildCheckBindResult(jbean);
    }

    /**
     * 验证注销验证码
     */
    public static EucApiResult<Integer> verifyDestoryCode(Context activity, String mobile, String token,
                                                          String veriCode, RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.put("mobile", mobile);
        jbody.put("veriCode", veriCode);
        JBean jbean = eucService.getResult("/api2/verifyDestoryCode.json",
                jbody, oAuthPara, info);
        return buildBodyInt(jbean);
    }

    /**
     * 获取注销验证码
     */
    public static EucApiResult<String> getDestoryCode(Context activity, String mobile, String token,
                                                      RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.put("mobile", mobile);
        JBean jbean = eucService.getResult("/api2/requestDestoryCode.json",
                jbody, oAuthPara, info);
        return buildBodyString(jbean);
    }

    /**
     * 获取注销相关协议
     */
    public static EucApiResult<LogoutInfo> getLogoutProtocol(Context activity, String token,
                                                             RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        JBean jbean = eucService.getResult("/api2/getDestoryAgreement.json",
                jbody, oAuthPara, info);
        return buildBodyLogout(jbean);
    }

    /**
     * 提交注销账号申请
     */
    public static EucApiResult<Integer> submitLogout(Context activity, String token, String userId, String name,
                                                     String phone, String code, String idName, String idNum, RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        String tempPhone = "";
        if (!CommonUtils.isNotNullOrEmpty(phone)) {
            phone = tempPhone;
        }
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.putContent("userId", userId);
        jbody.putContent("name", name);
        jbody.putContent("identityName", idName);
        jbody.putContent("identityNum", idNum);
        jbody.putContent("mobile", phone);
        jbody.putContent("veriCode", code);
        JBean jbean = eucService.getResult("/api2/saveUserDestoryCheck.json",
                jbody, oAuthPara, info);
        return buildBodyInt(jbean);
    }

    /**
     * 获取用户注销状态
     */
    public static EucApiResult<AccountStatusInfo> getAccountStatus(Context activity, String token, String name,
                                                                   RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.putContent("username", name);
        JBean jbean = eucService.getResult("/api2/getUserDestoryCheckStatus.json",
                jbody, oAuthPara, info);
        return buildBodyAccountStatus(jbean);
    }

    /**
     * 取消注销申请
     */
    public static EucApiResult<Integer> cancelDeleteAccount(Context activity, String token, String userid,
                                                            RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.putContent("userId", userid);
        JBean jbean = eucService.getResult("/api2/cancelUserDestoryCheck.json",
                jbody, oAuthPara, info);
        return buildBodyInt(jbean);
    }

    /**
     * 驳回弹窗状态置为0
     */
    public static EucApiResult<Integer> setNotRemind(Context activity, String token, String userid,
                                                     RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.putContent(CookieUtil.COOKIE_TOKEN, token);
        jbody.putContent("userId", userid);
        JBean jbean = eucService.getResult("/api2/remindedUser.json",
                jbody, oAuthPara, info);
        return buildBodyInt(jbean);
    }


    /**
     * 绑定第三方账号
     *
     * @param activity
     * @param userid    宜搜id
     * @param u         宜搜token
     * @param thirdType 第三方账号类型 1：taptap
     * @param openid    三方账号id
     * @param info
     * @throws EucAPIException
     */
    public static EucApiResult<String> bindThirdAccount(Context activity, String userid, String u, int thirdType, String openid, RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.put("userId", userid);
        jbody.put("U", u);
        jbody.put("thirdPartySsoId", thirdType);
        jbody.put("openId", openid);
        JBean jbean = eucService.getResult("/api2/saveThirdpartySsoUser.json",
                jbody, oAuthPara, info);
        EucApiResult<String> result = new EucApiResult<String>(jbean);
        return result;
    }

    public static LimitStatusInfo getLimitStatue(Context activity) {
        try {
            eucService = EucService.getInstance(activity);
            LimitStatusInfo limitSwitch = eucService.getLimitSwitch("https://egamec.eayou.com/c3s/as?");
            return limitSwitch;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 实名认证状态
     *
     * @param info
     * @return
     * @throws EucAPIException
     */
    public static IdentifyStatusInfo identifyStatus(String deviceId, RequestInfo info, Context _activity) throws EucAPIException {
        eucService = EucService.getInstance(_activity);
        JBody jbody = new JBody();
        jbody.put("deviceId", deviceId);
        JBean jbean = eucService.getResult("/api2/getUserIdentityStatus.json", jbody, oAuthPara, info);
        EucApiResult<String> result = new EucApiResult<String>(jbean);
        if (result.getResultCode() != null && result.getResultCode().equals(CodeConstant.OK) && jbean.getBody() != null) {
            String isHoliday = jbean.getBody().getObject("isHoliday", String.class);
            String autoRegist = jbean.getBody().getObject("autoRegistLimitByDeviceId", String.class);
            String identifyStatue = jbean.getBody().getObject("userIdentityStatus", String.class);
            IdentifyStatusInfo identifyStatusInfo = new IdentifyStatusInfo(Boolean.valueOf(isHoliday),
                    Integer.valueOf(autoRegist), Integer.valueOf(identifyStatue));
            return identifyStatusInfo;
        }
        return null;
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

    private static EucApiResult<CheckBindInfo> buildCheckBindResult(JBean jbean)
            throws EucAPIException {
        EucApiResult<CheckBindInfo> result = new EucApiResult<CheckBindInfo>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            String status = jbean.getBody().getObject("status", String.class);
            String userId = jbean.getBody().getObject("userId", String.class);
            String u = jbean.getBody().getObject("U", String.class);
            CheckBindInfo bindInfo = new CheckBindInfo(userId, status, u);
            result.setResult(bindInfo);
        }
        return result;
    }

    //返回通用body响应（body总result为1）
    private static EucApiResult<Integer> buildBodyInt(JBean jbean)
            throws EucAPIException {
        EucApiResult<Integer> result = new EucApiResult<Integer>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            String u = jbean.getBody().getObject("result", String.class);
            result.setResult(Integer.valueOf(u));
        }
        return result;
    }

    //返回通用body响应（body总result为1）
    private static EucApiResult<String> buildBodyString(JBean jbean)
            throws EucAPIException {
        EucApiResult<String> result = new EucApiResult<String>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            String u = jbean.getBody().getObject("veriCode", String.class);
            result.setResult(u);
        }
        return result;
    }

    //返回通用body响应（body总result为1）
    private static EucApiResult<LogoutInfo> buildBodyLogout(JBean jbean)
            throws EucAPIException {
        EucApiResult<LogoutInfo> result = new EucApiResult<LogoutInfo>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            String cancellationAgreement = jbean.getBody().getObject("cancellationAgreement", String.class);
            String cancellationCondition = jbean.getBody().getObject("cancellationCondition", String.class);
            String cancellationNotice = jbean.getBody().getObject("cancellationNotice", String.class);
            LogoutInfo info = new LogoutInfo(URLDecoder.decode(cancellationCondition), URLDecoder.decode(cancellationAgreement),
                    URLDecoder.decode(cancellationNotice));
            result.setResult(info);
        }
        return result;
    }

    private static EucApiResult<AccountStatusInfo> buildBodyAccountStatus(JBean jbean)
            throws EucAPIException {
        EucApiResult<AccountStatusInfo> result = new EucApiResult<AccountStatusInfo>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            String remark = jbean.getBody().getObject("remarks", String.class);
            String canApply = jbean.getBody().getObject("canApply", String.class);
            String isRemind = jbean.getBody().getObject("isRemind", String.class);
            String createTime = jbean.getBody().getObject("createTime", String.class);
            String status = jbean.getBody().getObject("status", String.class);
            String accountStatus = jbean.getBody().getObject("accountStatus", String.class);
            AccountStatusInfo info = new AccountStatusInfo(Integer.valueOf(canApply), remark, Integer.valueOf(isRemind)
                    , Long.valueOf(createTime), Integer.valueOf(status), Integer.valueOf(accountStatus));
            result.setResult(info);
        }
        return result;
    }

    private static EucApiResult<NationAuthenInfo> buildNationAuthen(JBean jbean)
            throws EucAPIException {
        EucApiResult<NationAuthenInfo> result = new EucApiResult<NationAuthenInfo>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            String identityNum = jbean.getBody().getObject("identityNum", String.class);
            String identityName = jbean.getBody().getObject("identityName", String.class);
            String status = jbean.getBody().getObject("identityStatus", String.class);
            NationAuthenInfo eucAuthResult = new NationAuthenInfo(identityNum, identityName,
                    Integer.valueOf(status));
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


    //获取玩家游玩时间，方便处理未成年和成年的限制
    public static EucApiResult<LimitTimesInfo> getTotalTimes(Context activity, String userid, String token, RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.put("userId", userid);
        jbody.put("EASOUTGC", token);
        JBean jbean = eucService.getResult("/api2/getUserIdentityReportTimes.json",
                jbody, oAuthPara, info);
        return getTimes(jbean);
    }

    //获取玩家游玩时间，方便处理未成年和成年的限制
    public static EucApiResult<LimitTimesInfo> addTotalTimes(Context activity, String userid, String token, RequestInfo info) throws EucAPIException {
        eucService = EucService.getInstance(activity);
        JBody jbody = new JBody();
        jbody.put("userId", userid);
        jbody.put("EASOUTGC", token);
        JBean jbean = eucService.getResult("/api2/incrUserIdentityReportTimes.json",
                jbody, oAuthPara, info);
        return getTimes(jbean);
    }

    private static EucApiResult<LimitTimesInfo> getTimes(JBean jbean)
            throws EucAPIException {
        EucApiResult<LimitTimesInfo> result = new EucApiResult<LimitTimesInfo>(jbean);
        if (CodeConstant.OK.equals(result.getResultCode())) {
            LimitTimesInfo info = new LimitTimesInfo();
            String todayTimes = jbean.getBody().getObject("todayTimes", String.class);
            String totalTimes = jbean.getBody().getObject("totalTimes", String.class);
            info.setTodayTimes(Integer.valueOf(todayTimes));
            info.setTotalTimes(Integer.valueOf(totalTimes));
            result.setResult(info);
        }
        return result;
    }
}
