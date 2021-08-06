package com.easou.androidsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.easou.androidsdk.callback.AppTimeWatcher;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.dialog.DeleteAccountStatusDialog;
import com.easou.androidsdk.dialog.NotiDialog;
import com.easou.androidsdk.login.MoneyDataCallBack;
import com.easou.androidsdk.login.service.AccountStatusInfo;
import com.easou.androidsdk.login.service.CashHistoryInfo;
import com.easou.androidsdk.login.service.CashLevelInfo;
import com.easou.androidsdk.login.service.CheckBindInfo;
import com.easou.androidsdk.login.service.DrawResultInfo;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.LimitTimesInfo;
import com.easou.androidsdk.login.service.LoginNameInfo;
import com.easou.androidsdk.login.service.LogoutInfo;
import com.easou.androidsdk.login.service.MoneyBaseInfo;
import com.easou.androidsdk.login.service.MoneyListInfo;
import com.easou.androidsdk.login.service.PayLimitInfo;
import com.easou.androidsdk.dialog.AccountInfoDialog;
import com.easou.androidsdk.dialog.AuthenNotiDialog;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.AuthenCallBack;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.PayAPI;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.UserAPI;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.JUser;
import com.easou.androidsdk.login.service.LUser;
import com.easou.androidsdk.login.service.LimitStatusInfo;
import com.easou.androidsdk.login.service.LoginBean;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartLogPlugin;
import com.easou.androidsdk.plugin.StartOtherPlugin;
import com.easou.androidsdk.romutils.RomHelper;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.ui.FloatView;
import com.easou.androidsdk.ui.UIHelper;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.GsonUtil;
import com.easou.androidsdk.util.NetworkUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;
import com.taptap.sdk.LoginManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by xiaoqing.zhou
 * on 2020/8/5
 * fun
 */
public class StartESAccountCenter {
    private StartESAccountCenter() {

    }

    /**
     * 获取用户信息
     *
     * @param id
     */
    public static void getUserInfoById(final String id) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_LOGIN_STATUS, "true");
        result.put(ESConstant.SDK_USER_ID, id);
        result.put(ESConstant.SDK_USER_NAME, Starter.loginBean.getUser().getName());
        result.put(ESConstant.SDK_USER_TOKEN, Constant.ESDK_TOKEN);
        Starter.mCallback.onUserInfo(result);
    }

    /**
     * 账号登录
     *
     * @param name
     * @param password
     * @param mContext
     */
    public static void handleAccountLogin(final LoginCallBack callBack, final String name,
                                          final String password, final Context mContext) {
        if (NetworkUtils.isNetConnected(mContext)) {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        EucApiResult<LoginBean> login = AuthAPI.login(name, password,
                                true, RegisterAPI.getRequestInfo(mContext), mContext);
                        if (login.getResultCode().equals(CodeConstant.OK)) {
                            callBack.loginSuccess();
                            handleLoginBean(true, login, mContext, password);
                        } else {
                            if (login.getResultCode().equals("502")) {
                                callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                            } else {
                                if (login.getDescList().size() > 0) {
                                    callBack.loginFail(login.getDescList().get(0).getD());
                                } else {
                                    callBack.loginFail("登录失败");
                                }
                            }
                        }

                    } catch (Exception e) {
                        ESdkLog.d("账号登录失败" + e.toString());
                        callBack.loginFail("登录中,请稍后");
                    }
                }
            });
        } else {
            callBack.loginFail("网络连接错误，请检查您的网络");
        }
    }

    /**
     * 处理tap登录
     *
     * @param openId
     * @param callBack
     * @param mContext
     */
    public static void handleTapLogin(final String openId, final LoginCallBack callBack, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                //检查该tap账号是否绑定了宜搜账号
                try {
                    EucApiResult<CheckBindInfo> bindInfo = AuthAPI.checkIsBindThird(mContext, 1,
                            openId, RegisterAPI.getRequestInfo(mContext));
                    if (bindInfo.getResultCode().equals(CodeConstant.OK)) {
                        if (bindInfo.getResult().getStatus().equals("1")) {
                            //已绑定，直接使用宜搜账号登录
                            loginByTokenUser(false, callBack, bindInfo.getResult().getU(), mContext);
                        } else {
                            //未绑定宜搜账号，自动注册宜搜账号并绑定
                            registerEsAndBind(callBack, openId, mContext);
                        }
                    } else {
                        if (bindInfo.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (bindInfo.getDescList().size() > 0) {
                                callBack.loginFail(bindInfo.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("登录失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    callBack.loginFail("登录失败");
                    ESdkLog.d("查询绑定失败" + e.toString());
                }
            }
        });
    }

    /**
     * 自动注册宜搜账号并与该tap id绑定
     *
     * @param openId
     */
    private static void registerEsAndBind(final LoginCallBack callBack, final String openId, Context mContext) {
        try {
            EucApiResult<AuthBean> userInfo = RegisterAPI.autoRegist(true,
                    RegisterAPI.getTapRequestInfo(mContext), mContext);
            if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                //自动注册成功后需要绑定当前账号
                EucApiResult<String> result = AuthAPI.bindThirdAccount(mContext, userInfo.getResult().getEsid(),
                        userInfo.getResult().getU().getU(),
                        1, openId, RegisterAPI.getRequestInfo(mContext));
                if (result.getResultCode().equals(CodeConstant.OK)) {
                    //绑定成功
                    callBack.loginSuccess();
                    handleRegister(false, userInfo, mContext, false, "");
                } else {
                    if (result.getResultCode().equals("502")) {
                        callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                    } else {
                        if (result.getDescList().size() > 0 && result.getDescList().get(0).getC().equals("4")) {
                            //已经绑定过了则执行登录等操作
                            callBack.loginSuccess();
                            handleRegister(false, userInfo, mContext, false, "");
                        } else {
                            callBack.loginFail("登录失败,请重新登录");
                        }
                    }
                }
            } else {
                if (userInfo.getResultCode().equals("502")) {
                    callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                } else {
                    if (userInfo.getDescList().size() > 0) {
                        callBack.loginFail(userInfo.getDescList().get(0).getD());
                    } else {
                        callBack.loginFail("登录失败");
                    }
                }
            }
        } catch (Exception e) {
            ESdkLog.d("游客登录失败" + e.toString());
            callBack.loginFail("登录中,请稍后");
        }
    }

    /**
     * token登录
     *
     * @param
     * @param u
     * @param mContext
     */
    public static void loginByTokenUser(boolean isNeedSave, final LoginCallBack callBack,
                                        final String u, final Context mContext) {
        try {
            EucApiResult<LoginBean> validate = AuthAPI.validateU(u, RegisterAPI.getRequestInfo(mContext),
                    (Activity) mContext);
            if (validate.getResultCode().equals(CodeConstant.OK)) {
                callBack.loginSuccess();
                handleTapLoginBean(isNeedSave, validate, mContext);
            } else {
                if (validate.getResultCode().equals("502")) {
                    callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                } else {
                    if (validate.getDescList().size() > 0) {
                        callBack.loginFail(validate.getDescList().get(0).getD());
                    } else {
                        callBack.loginFail("登录失败");
                    }
                }
            }

        } catch (Exception e) {
            ESdkLog.d("账号登录失败" + e.toString());
            callBack.loginFail("登录中,请稍后");
        }
    }

    /**
     * 处理tap登录后该账号绑定的宜搜账号登录
     *
     * @param validate
     * @param mContext
     */
    private static void handleTapLoginBean(boolean isNeedSave, EucApiResult<LoginBean> validate, Context mContext) {
        handleLoginBean(isNeedSave, validate, mContext, "");
    }

    /**
     * 修改密码后静默登陆
     *
     * @param callBack
     * @param name
     * @param password
     * @param mContext
     */
    public static void silenceLogin(final LoginCallBack callBack, final String name,
                                    final String password, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<LoginBean> login = AuthAPI.login(name, password,
                            true, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (login.getResultCode().equals(CodeConstant.OK)) {
                        reLogin(login, mContext, password);
                    } else {
                        if (login.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            callBack.loginFail("请重新登录");
                        }
                        StartESUserPlugin.hideUserCenter();
                        logout(mContext);
                    }
                } catch (Exception e) {
                    ESdkLog.d("登录失败" + e.toString());
                    StartESUserPlugin.hideUserCenter();
                    logout(mContext);
                }
            }
        });
    }

    /**
     * 账号注册
     *
     * @param name
     * @param password
     * @param mContext
     */
    public static void handleAccountRegister(final LoginCallBack callBack, final String name,
                                             final String password, final Context mContext) {
        if (NetworkUtils.isNetConnected(mContext)) {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        EucApiResult<AuthBean> login = RegisterAPI.registByName(name, password,
                                true, "", RegisterAPI.getRequestInfo(mContext), mContext);
                        if (login.getResultCode().equals(CodeConstant.OK)) {
                            callBack.loginSuccess();
                            handleRegister(true, login, mContext, false, password);
                        } else {
                            if (login.getResultCode().equals("502")) {
                                callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                            } else {
                                if (login.getDescList().size() > 0) {
                                    callBack.loginFail(login.getDescList().get(0).getD());
                                } else {
                                    callBack.loginFail("账号注册失败");
                                }
                            }
                        }
                    } catch (Exception e) {
                        ESdkLog.d("账号注册失败" + e.toString());
                        callBack.loginFail("账号注册失败");
                    }
                }
            });
        } else {
            callBack.loginFail("网络连接错误，请检查您的网络");
        }
    }

    /**
     * 用户中心更新密码
     *
     * @param callBack
     * @param newPw
     * @param oldPw
     * @param mContext
     */
    public static void updatePassword(final LoginCallBack callBack, final String newPw,
                                      final String oldPw, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> userInfo = UserAPI.updatePasswd(Constant.ESDK_TOKEN,
                            oldPw, newPw, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        silenceLogin(callBack, Starter.loginBean.getUser().getName(), newPw, mContext);
                        callBack.loginSuccess();
                    } else {
                        if (userInfo.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (userInfo.getDescList().size() > 0) {
                                callBack.loginFail(userInfo.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("更改失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    ESdkLog.d("修改失败" + e.toString());
                    callBack.loginFail("更改失败");
                }
            }
        });
    }

    /**
     * 请求重设密码
     *
     * @param callBack
     * @param phone
     * @param mContext
     */
    public static void requestResetPassword(final LoginCallBack callBack, final String phone,
                                            final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> info = AuthAPI.requestResetPass(phone,
                            RegisterAPI.getRequestInfo(mContext), mContext);
                    if (info.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                    } else {
                        if (info.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (info.getDescList().size() > 0) {
                                callBack.loginFail(info.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("操作失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    ESdkLog.d("操作失败" + e.toString());
                    callBack.loginFail("操作失败");
                }
            }
        });
    }

    /**
     * 重设密码
     *
     * @param callBack
     * @param phone
     * @param newPw
     * @param code
     * @param mContext
     */
    public static void lookPassword(final AuthenCallBack callBack, final String phone,
                                    final String newPw, final String code, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<AuthBean> userInfo = AuthAPI.applyResetPass(phone, newPw,
                            code, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess(userInfo.getResult().getUser().getName());
                    } else {
                        if (userInfo.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (userInfo.getDescList().size() > 0) {
                                callBack.loginFail(userInfo.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("操作失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    ESdkLog.d("操作失败" + e.toString());
                    callBack.loginFail("操作失败");
                }
            }
        });
    }

    /**
     * 请求绑定手机
     *
     * @param callBack
     * @param phone
     * @param type
     * @param mContext
     */
    public static void requestBindOrUnBind(final LoginCallBack callBack, final String phone,
                                           final boolean type, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> info = UserAPI.requestBindMobile(Constant.ESDK_TOKEN, phone,
                            RegisterAPI.getRequestInfo(mContext), mContext, type);
                    if (info.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                    } else {
                        if (info.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (info.getDescList().size() > 0) {
                                callBack.loginFail(info.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("操作失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    callBack.loginFail("操作失败");
                }
            }
        });
    }

    /**
     * 绑定或解绑手机
     *
     * @param callBack
     * @param phone
     * @param code
     * @param type
     * @param mContext
     */
    public static void applyBindOrUnBind(final LoginCallBack callBack, final String phone,
                                         final String code, final boolean type, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<JUser> info = UserAPI.applyBindMobile(Constant.ESDK_TOKEN, phone,
                            code, RegisterAPI.getRequestInfo(mContext), mContext, type);
                    if (info.getResultCode().equals(CodeConstant.OK)) {
                        if (type) {
                            Starter.loginBean.getUser().setMobile(phone);
                        } else {
                            Starter.loginBean.getUser().setMobile("");
                        }
                        callBack.loginSuccess();
                    } else {
                        if (info.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (info.getDescList().size() > 0) {
                                callBack.loginFail(info.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("操作失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    callBack.loginFail("操作失败");
                }
            }
        });
    }


    /**
     * 用户实名认证
     *
     * @param callBack
     * @param name
     * @param idNum
     * @param mContext
     */
    public static void userCenterAuthen(final AuthenCallBack callBack, final String name,
                                        final String idNum, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> userInfo;
                    if (CommonUtils.getNationIdentity(mContext) == 0) {
                        userInfo = AuthAPI.userIdentify(name, idNum, Constant.ESDK_USERID, Constant.ESDK_APP_ID,
                                RegisterAPI.getRequestInfo(mContext), mContext);
                    } else {
                        userInfo = AuthAPI.userIdentifyNation(name, idNum, Constant.ESDK_USERID, Constant.ESDK_APP_ID,
                                RegisterAPI.getRequestInfo(mContext), mContext);
                    }
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess(CommonUtils.getYMDfromIdNum(idNum));
                    } else {
                        if (userInfo.getResultCode().equals("502")) {
                            callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                        } else {
                            if (userInfo.getDescList().size() > 0) {
                                callBack.loginFail(userInfo.getDescList().get(0).getD());
                            } else {
                                callBack.loginFail("认证失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    ESdkLog.d("认证失败" + e.toString());
                    callBack.loginFail("认证失败");
                }
            }
        });
    }

    /**
     * 游客自动登录
     *
     * @param mContext
     */
    public static void handleAutoRegister(final LoginCallBack callBack, final Context mContext) {
        if (NetworkUtils.isNetConnected(mContext)) {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        EucApiResult<AuthBean> userInfo = RegisterAPI.autoRegist(true,
                                RegisterAPI.getRequestInfo(mContext), mContext);
                        if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                            callBack.loginSuccess();
                            handleRegister(true, userInfo, mContext, true, "");
                        } else {
                            if (userInfo.getResultCode().equals("502")) {
                                callBack.loginFail("账号系统升级维护中，请您稍候再尝试登录。");
                            } else {
                                if (userInfo.getDescList().size() > 0) {
                                    callBack.loginFail(userInfo.getDescList().get(0).getD());
                                } else {
                                    callBack.loginFail("登录失败");
                                }
                            }
                        }
                    } catch (Exception e) {
                        ESdkLog.d("游客登录失败" + e.toString());
                        callBack.loginFail("登录中,请稍后");
                    }
                }
            });
        } else {
            callBack.loginFail("网络连接错误，请检查您的网络");
        }
    }

    //获取红包界面基本数据
    public static void moneyBaseInfo(final MoneyDataCallBack<MoneyBaseInfo> callBack, final Context mContext
            , final String playerId, final String serverId) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    MoneyBaseInfo moneyInfo = EucService.getInstance(mContext).getMoneyInfo(playerId, serverId);
                    if (moneyInfo == null) {
                        callBack.fail("网络出错，请重试");
                        return;
                    }
                    callBack.success(moneyInfo);
                } catch (Exception e) {
                    callBack.fail("网络出错，请重试");
                }
            }
        });
    }

    //获取红包列表数据
    public static void moneyListInfo(final MoneyDataCallBack<MoneyListInfo> callBack, final Context mContext
            , final String playerId, final String serverId) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    MoneyListInfo listInfo = EucService.getInstance(mContext).getMoneyList(playerId, serverId);
                    if (listInfo == null) {
                        callBack.fail("红包获取失败");
                        return;
                    }
                    callBack.success(listInfo);
                } catch (Exception e) {
                    callBack.fail("红包获取失败");
                }
            }
        });
    }

    //获取提现信息
    public static void getCashInfo(final MoneyDataCallBack<CashLevelInfo> callBack, final Context mContext
            , final String playerId, final String serverId) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    CashLevelInfo listInfo = EucService.getInstance(mContext).getCashInfo(playerId, serverId);
                    if (listInfo == null) {
                        callBack.fail("网络出错，请重试");
                        return;
                    }
                    callBack.success(listInfo);
                } catch (Exception e) {
                    callBack.fail("网络出错，请重试");
                }
            }
        });
    }

    //提现
    public static void getCash(final MoneyDataCallBack<DrawResultInfo> callBack, final Context mContext
            , final String playerId, final String money, final String serverId) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    DrawResultInfo listInfo = EucService.getInstance(mContext).getCash(playerId, money, serverId);
                    if (listInfo == null) {
                        callBack.fail("提现失败");
                        return;
                    }
                    callBack.success(listInfo);
                } catch (Exception e) {
                    callBack.fail("提现失败");
                }
            }
        });
    }

    //提现记录
    public static void getCashHistory(final MoneyDataCallBack<CashHistoryInfo> callBack, final Context mContext
            , final String playerId, final String serverId) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    CashHistoryInfo listInfo = EucService.getInstance(mContext).getCashHistory(playerId, serverId);
                    if (listInfo == null) {
                        callBack.fail("暂无记录");
                        return;
                    }
                    callBack.success(listInfo);
                } catch (Exception e) {
                    callBack.fail("暂无记录");
                }
            }
        });
    }

    public static void loginOrOutLog(final int bt, final Context mContext) {
        //需要根据接口状态来判断是否需要上传上下线日志
        if (CommonUtils.getLogState(mContext) == 1) {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    UserAPI.loginOrOutLog(mContext, Tools.getDeviceImei(mContext), Constant.ESDK_USERID,
                            bt, RegisterAPI.getRequestInfo(mContext));
                }
            });
        }
    }

    //验证注销账号验证码
    public static void veriLogoutCode(final String mobile, final String code, final LoginCallBack callBack
            , final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<Integer> result = AuthAPI.verifyDestoryCode(mContext, mobile,
                            Constant.ESDK_TOKEN, code, RegisterAPI.getRequestInfo(Starter.mActivity));
                    if (result.getResultCode().equals(CodeConstant.OK) && result.getResult() == 1) {
                        callBack.loginSuccess();
                    } else {
                        callBack.loginFail("网络出错了");
                    }
                } catch (EucAPIException e) {
                    callBack.loginFail("网络出错了");
                }
            }
        });
    }

    //获取注销账号验证码
    public static void getLogoutCode(final String mobile, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> result = AuthAPI.getDestoryCode(mContext, mobile,
                            Constant.ESDK_TOKEN, RegisterAPI.getRequestInfo(Starter.mActivity));
                    if (result.getResultCode().equals(CodeConstant.OK)) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ESToast.getInstance().ToastShow(mContext, "验证码发送成功，请留意短信提醒");
                            }
                        });
                    } else {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ESToast.getInstance().ToastShow(mContext, "网络出错，请重试");
                            }
                        });
                    }
                } catch (EucAPIException e) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ESToast.getInstance().ToastShow(mContext, "网络出错，请重试");
                        }
                    });
                }
            }
        });
    }

    //获取注销协议
    public static void getLogoutInfo(final MoneyDataCallBack<LogoutInfo> callBack
            , final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<LogoutInfo> result = AuthAPI.getLogoutProtocol(mContext,
                            Constant.ESDK_TOKEN, RegisterAPI.getRequestInfo(Starter.mActivity));
                    if (result.getResultCode().equals(CodeConstant.OK)) {
                        callBack.success(result.getResult());
                    } else {
                        callBack.fail("网络出错了");
                    }
                } catch (EucAPIException e) {
                    callBack.fail("网络出错了");
                }
            }
        });
    }

    //提交注销
    public static void submitLogout(final LoginCallBack callBack, final String idName, final String idNum, final String phone
            , final String code, final String name, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<Integer> result = AuthAPI.submitLogout(mContext, Constant.ESDK_TOKEN,
                            Constant.ESDK_USERID, name, phone, code, idName, idNum, RegisterAPI.getRequestInfo(Starter.mActivity));
                    if (result.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                    } else {
                        if (result.getDescList().size() > 0) {
                            callBack.loginFail(result.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("登录失败");
                        }
                    }
                } catch (EucAPIException e) {
                    callBack.loginFail("网络出错了");
                }
            }
        });
    }

    //获取注销账号状态
    public static void getAccountStatus(final MoneyDataCallBack<AccountStatusInfo> callBack,
                                        final String name, final Context mContext) {
        try {
            EucApiResult<AccountStatusInfo> result = AuthAPI.getAccountStatus(mContext, Constant.ESDK_TOKEN,
                    name, RegisterAPI.getRequestInfo(Starter.mActivity));
            if (result.getResultCode().equals(CodeConstant.OK)) {
                callBack.success(result.getResult());
            } else {
                callBack.fail("网络出错了");
            }
        } catch (EucAPIException e) {
            callBack.fail("网络出错了");
        }

    }

    //获取当前用户的游玩时长
    public static void uploadTime(final boolean isAdd, final Map<String, String> result, final boolean isLogin
            , final Map<String, String> authenResult) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final EucApiResult<LimitTimesInfo> times;
                    if (isAdd) {
                        times = AuthAPI.addTotalTimes(Starter.mActivity,
                                Constant.ESDK_USERID, Constant.ESDK_TOKEN, RegisterAPI.getRequestInfo(Starter.mActivity));
                    } else {
                        times = AuthAPI.getTotalTimes(Starter.mActivity,
                                Constant.ESDK_USERID, Constant.ESDK_TOKEN, RegisterAPI.getRequestInfo(Starter.mActivity));
                    }
                    final LUser user = Starter.loginBean.getUser();
                    Starter.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (times.getResultCode().equals(CodeConstant.OK)) {
                                if (!CommonUtils.isNotNullOrEmpty(user.getIdentityNum())) {
                                    //游客累计时长为1小时
                                    if (times.getResult().getTotalTimes() * 5 >= 60) {
                                        //弹出强制退出框
                                        NotiDialog authenDialog = new NotiDialog(Starter.mActivity, R.style.easou_dialog, Gravity.CENTER,
                                                0.8f, 0, false, "", Constant.guestLimitNoti, "");
                                        authenDialog.show();
                                    } else {
                                        handleAuthenLogin(result, isLogin, authenResult);
                                    }
                                } else {
                                    if (CommonUtils.getAge(user.getIdentityNum()) < 18) {
                                        //弹出未成年规则，每日22-次日8点禁止登录，每日累计1.5小时，节假日每日累计3小时
                                        if (CommonUtils.is22to8() || (times.getResult().getTodayTimes() * 5 >= 180
                                                && Constant.isHoliday) || (times.getResult().getTodayTimes() * 5 >= 90 && !Constant.isHoliday)) {
                                            //弹出强制退出框
                                            NotiDialog authenDialog = new NotiDialog(Starter.mActivity, R.style.easou_dialog, Gravity.CENTER,
                                                    0.8f, 0, false, "", Constant.teeLimitNoti, "");
                                            authenDialog.show();
                                        } else {
                                            handleAuthenLogin(result, isLogin, authenResult);
                                        }
                                    } else {
                                        handleAuthenLogin(result, isLogin, authenResult);
                                    }
                                    //成年人不做任何处理
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    //查询次数异常不影响登录
                    Starter.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleAuthenLogin(result, isLogin, authenResult);
                        }
                    });
                }
            }
        });
    }

    private static void queryNationIdentify(final Context mContext, final String userId, final int mAge, final LimitStatusInfo limitStatue
            , final Map<String, String> result, final EucApiResult<LoginBean> userInfo) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final EucApiResult<String> stringEucApiResult = AuthAPI.queryNationIdentify(userId, Tools.getDeviceImei(mContext)
                            , RegisterAPI.getRequestInfo(mContext), mContext);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (stringEucApiResult.getResultCode().equals(CodeConstant.OK)) {
                                if (mAge < 18 && limitStatue != null && limitStatue.getNs() == 1 && limitStatue.getUs() != 0) {
                                    showNotiDialog(mAge, result, true, null);
                                } else {
                                    handleAuthenLogin(result, true, null);
                                }
//                                handleResult(result);
                            } else {
                                if (userInfo.getDescList().size() > 0 && userInfo.getDescList().get(0).getC().equals("8")) {
                                    ESToast.getInstance().ToastShow(mContext, userInfo.getDescList().get(0).getD());
                                } else {
                                    ESToast.getInstance().ToastShow(mContext, "认证失败");
                                }
                                popAuthenDialog(mContext, limitStatue, result, true);
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        });
    }

    private static void queryNationIdentify(final Context mContext, final String userId) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final EucApiResult<String> stringEucApiResult = AuthAPI.queryNationIdentify(userId, Tools.getDeviceImei(mContext)
                            , RegisterAPI.getRequestInfo(mContext), mContext);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (stringEucApiResult.getResultCode().equals(CodeConstant.OK)) {
                                //国家实名认证成功，开启账号注销功能
                                Constant.mNationAuthen = 1;
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 注册成功后要判断是否实名认证
     *
     * @param userInfo
     * @param mContext
     * @param isShowInfoDialog
     * @param pw
     */
    public static void handleRegister(boolean isSaveInfo, EucApiResult<AuthBean> userInfo,
                                      final Context mContext, final boolean isShowInfoDialog, String pw) {

        final String userId = String.valueOf(userInfo.getResult().getEsid());
        final String userName = String.valueOf(userInfo.getResult().getUser().getName());
        String token = String.valueOf(userInfo.getResult().getToken().token);
        final String password = String.valueOf(userInfo.getResult().getUser().getPasswd());
        LUser user = generateLuser(userInfo.getResult().getUser());
        user.setPasswd(pw.isEmpty() ? password : pw);
        LoginBean login = new LoginBean(userInfo.getResult().getToken(), user, userInfo.getResult().getEsid(), true);
        Starter.loginBean = login;
        if (isSaveInfo) {
            //tap登录不需要保存这些信息，因为每次都会根据tap保存的信息进行服务器查询是否绑定操作来登录
            saveLoginInfo(userInfo.getResult().getUser().getName(), userInfo.getResult().getUser().getPasswd(), mContext);
            //注册成功后的用户实体类和登陆返回的用户实体类不同，需要转换成一样的
            CommonUtils.saveLoginInfo(GsonUtil.toJson(login), mContext);
            CommonUtils.saveUInfo(userInfo.getResult().getU().getU(), mContext);
        }
        //上报sdk数据
        StartOtherPlugin.logTTActionRegister();
        StartOtherPlugin.logGismActionRegister();
        StartOtherPlugin.logGDTActionSetID(userId);
        StartOtherPlugin.registerAqyAction();
        StartOtherPlugin.createRoleAqyAction(userName);
        StartOtherPlugin.logKSActionRegister();
        StartOtherPlugin.logBDRegister();
        Constant.IS_LOGINED = true;
        Constant.ESDK_USERID = userId;
        Constant.ESDK_TOKEN = token;
        //登陆成功回调给cp
        final Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);
        result.put(ESConstant.SDK_USER_TOKEN, token);
        result.put(ESConstant.SDK_USER_BIRTH_DATE, "0");
        result.put(ESConstant.SDK_IS_IDENTITY_USER, "0");
        result.put(ESConstant.SDK_IS_ADULT, "0");
        result.put(ESConstant.SDK_IS_HOLIDAY, "0");
        final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(mContext);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startTimer();
                StartLogPlugin.startSdkLoginLog(userId, userName);
                if (!isShowInfoDialog) {
                    //注册成功，不需要显示账号密码信息框
                    final Map<String, String> register = new HashMap<String, String>();
                    register.put(ESConstant.SDK_USER_ID, userId);
                    register.put(ESConstant.SDK_USER_NAME, userName);
                    postShowMsg(mContext, "欢迎回来, " + userName + "!", Gravity.TOP);
                    if (limitStatue != null && limitStatue.getUs() != 0) {
                        //需要实名认证
                        popAuthenDialog(mContext, limitStatue, register, false);
                    } else {
                        Starter.mCallback.onRegister(register);
                       /* //注册用户登录提示
                        if (limitStatue.getNs() == 1) {
                            showNotiDialog(0);
                        }*/
                        popFloatView();
                    }
                } else {
                    //自动注册用户需要弹出账号信息框
                    AccountInfoDialog infoDialog = new AccountInfoDialog(Starter.mActivity,
                            R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, userName, password);
                    infoDialog.show();
                    infoDialog.setCloseListener(new AccountInfoDialog.OnCloseDialogListener() {
                        @Override
                        public void dialogClose() {
                            //游客登录提示
                            if (limitStatue != null && limitStatue.getNs() == 1 && limitStatue.getUs() != 0) {
                                showNotiDialog(0, result, true, null);
                            } else {
                                handleAuthenLogin(result, true, null);
                            }
//                            handleResult(result);
//                            postShowMsg(mContext, "欢迎回来, " + userName + "!", Gravity.TOP);
                        }
                    });
                }
            }
        });
    }

    //修改密码后静默登陆
    public static void reLogin(final EucApiResult<LoginBean> userInfo, final Context mContext, String pw) {
        final String password = String.valueOf(userInfo.getResult().getUser().getPasswd());
        userInfo.getResult().getUser().setPasswd(pw.isEmpty() ? password : pw);
        saveLoginInfo(userInfo.getResult().getUser().getName(), pw, mContext);
        String token = String.valueOf(userInfo.getResult().getToken().token);
        Constant.ESDK_TOKEN = token;
        Starter.loginBean = userInfo.getResult();
        CommonUtils.saveLoginInfo(GsonUtil.toJson(userInfo.getResult()), mContext);
        CommonUtils.saveUInfo(userInfo.getResult().getU().getU(), mContext);
    }

    public static void handleLoginBean(final boolean isSaveInfo, final EucApiResult<LoginBean> userInfo, final Context mContext, final String pw) {
        final String userId = String.valueOf(userInfo.getResult().getEsid());
        final String userName = String.valueOf(userInfo.getResult().getUser().getName());
        String token = String.valueOf(userInfo.getResult().getToken().token);
        Constant.ESDK_TOKEN = token;
        getAccountStatus(new MoneyDataCallBack<AccountStatusInfo>() {
            @Override
            public void success(final AccountStatusInfo accountStatusInfo) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Constant.mIsCanReply = accountStatusInfo.getCanApply();
                        if (accountStatusInfo.getAccountStatus() == 5) {
                            ESToast.getInstance().ToastShow(mContext, "该账号已注销!");
                            logout(mContext);
                        } else if (accountStatusInfo.getAccountStatus() == 2 || accountStatusInfo.getAccountStatus() == 3) {
                            //弹窗提醒用户该账号处于注销申请期状态，是否继续等待注销活着确认取消注销
                            DeleteAccountStatusDialog dialog = new DeleteAccountStatusDialog(mContext, R.style.easou_dialog, Gravity.CENTER,
                                    0.8f, 0, "注销申请取消", Constant.AccountCancelNoti, true);
                            dialog.show();
                            dialog.setOnCloseListener(new DeleteAccountStatusDialog.OnCloseListener() {
                                @Override
                                public void close(int type) {
                                    if (type == 1) {
                                        //继续等待注销,回到登录状态
                                        logout(mContext);
                                    } else if (type == 2) {
                                        //确认取消注销，正常登录
                                        handleDeleteAccount(isSaveInfo, userInfo, mContext, pw);
                                    }
                                }
                            });
                        } else if (accountStatusInfo.getAccountStatus() == 4) {
                            if (accountStatusInfo.getIsRemind() == 0) {
                                //注销驳回状态，弹窗提醒
                                DeleteAccountStatusDialog dialog = new DeleteAccountStatusDialog(mContext, R.style.easou_dialog, Gravity.CENTER,
                                        0.8f, 0, "账号注销申请状态提醒", accountStatusInfo.getRemarks(), false);
                                dialog.show();
                                dialog.setOnCloseListener(new DeleteAccountStatusDialog.OnCloseListener() {
                                    @Override
                                    public void close(int type) {
                                        if (type == 3) {
                                            //账号注销被驳回,正常登录
                                            ThreadPoolManager.getInstance().addTask(new Runnable() {
                                                @Override
                                                public void run() {
                                                    handleDeleteAccount(isSaveInfo, userInfo, mContext, pw);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                //正常登录
                                ThreadPoolManager.getInstance().addTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleDeleteAccount(isSaveInfo, userInfo, mContext, pw);
                                    }
                                });
                            }
                        } else {
                            ThreadPoolManager.getInstance().addTask(new Runnable() {
                                @Override
                                public void run() {
                                    handleDeleteAccount(isSaveInfo, userInfo, mContext, pw);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void fail(String msg) {
                //正常登录
                logout(mContext);
            }
        }, userName, mContext);
    }

    public static void handleDeleteAccount(boolean isSaveInfo, final EucApiResult<LoginBean> userInfo, final Context mContext, String pw) {
        final String userId = String.valueOf(userInfo.getResult().getEsid());
        final String userName = String.valueOf(userInfo.getResult().getUser().getName());
        String token = String.valueOf(userInfo.getResult().getToken().token);
        Starter.loginBean = userInfo.getResult();
        if (isSaveInfo) {
            //tap登录不需要保存这些信息，因为每次都会根据tap保存的信息进行服务器查询是否绑定操作来登录
            if (!pw.isEmpty()) {
                saveLoginInfo(userInfo.getResult().getUser().getName(), pw, mContext);
                final String password = String.valueOf(userInfo.getResult().getUser().getPasswd());
                userInfo.getResult().getUser().setPasswd(pw.isEmpty() ? password : pw);
            }
            CommonUtils.saveLoginInfo(GsonUtil.toJson(userInfo.getResult()), mContext);
            CommonUtils.saveUInfo(userInfo.getResult().getU().getU(), mContext);
        }
        //上报sdk数据
        StartOtherPlugin.logTTActionLogin(userId);
        StartOtherPlugin.logGismActionLogin(userId);
        StartOtherPlugin.logGDTActionSetID(userId);
        StartOtherPlugin.loginAqyAction();
        StartOtherPlugin.createRoleAqyAction(userName);
        StartOtherPlugin.logBDLogin();

        Constant.IS_LOGINED = true;
        Constant.ESDK_USERID = userId;
        Constant.ESDK_TOKEN = token;
        //登陆成功回调给cp
        final Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);
        result.put(ESConstant.SDK_USER_TOKEN, token);
        final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(mContext);
        String isAdult = "0";
        int age = 0;
        queryNationIdentify(mContext, userId);
        final int identityStatus = userInfo.getResult().getUser().getIdentityStatus();
        if (limitStatue.getRns() != 0) {
            //走国家实名认证
            if (identityStatus == 1) {
                //已经实名认证过
                result.put(ESConstant.SDK_USER_BIRTH_DATE,
                        CommonUtils.getYMDfromIdNum(userInfo.getResult().getUser().getIdentityNum()));
                result.put(ESConstant.SDK_IS_IDENTITY_USER, "1");
                age = CommonUtils.getAge(userInfo.getResult().getUser().getIdentityNum());
                isAdult = age >= 18 ? "1" : "0";
                getPayLimitInfo(mContext, age, userInfo.getResult().getUser().getIdentityNum(), isAdult);
            }
        } else {
            //走宜搜实名认证
            //身份证号为null则生日为0，未实名认证
            result.put(ESConstant.SDK_USER_BIRTH_DATE, !TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum()) ?
                    CommonUtils.getYMDfromIdNum(userInfo.getResult().getUser().getIdentityNum()) : "0");
            result.put(ESConstant.SDK_IS_IDENTITY_USER, TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum()) ? "0" : "1");

            if (!TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum())) {
                //实名认证过的用户才会去做支付限制
                age = CommonUtils.getAge(userInfo.getResult().getUser().getIdentityNum());
                isAdult = age >= 18 ? "1" : "0";
                getPayLimitInfo(mContext, age, userInfo.getResult().getUser().getIdentityNum(), isAdult);
            }
        }

        final int mAge = age;
        result.put(ESConstant.SDK_IS_ADULT, isAdult);
        result.put(ESConstant.SDK_IS_HOLIDAY, "0");
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startTimer();
                StartLogPlugin.startSdkLoginLog(userId, userName);
                postShowMsg(mContext, "欢迎回来, " + userName + "!", Gravity.TOP);
                if (limitStatue.getRns() == 0) {
                    if (TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum()) && userInfo.getResult().getUser().getIsAutoRegist() != 1) {
                        //没有认证过的并且不是自动注册的用户需要去检查是否需要弹出实名验证框
                        if (limitStatue != null && limitStatue.getUs() != 0) {
                            //需要登陆验证时弹出验证框
                            popAuthenDialog(mContext, limitStatue, result, true);
                        } else {
                            handleResult(result);
                        }
                    } else {
                        //未认证过的自动注册用户或者认证过但是未成年的提示
                        if (mAge < 18 && limitStatue != null && limitStatue.getNs() == 1 && limitStatue.getUs() != 0) {
                            showNotiDialog(mAge, result, true, null);
                        } else {
                            handleAuthenLogin(result, true, null);
                        }
//                        handleResult(result);
                    }
                } else {
                    if ((identityStatus == 0 || identityStatus == 2 || identityStatus == 3) && userInfo.getResult().getUser().getIsAutoRegist() != 1) {
                        if (limitStatue != null && limitStatue.getUs() != 0) {
                            if (identityStatus == 2 || identityStatus == 3) {
                                //需要登陆验证时弹出验证框
                                popAuthenDialog(mContext, limitStatue, result, true);
                            } else {
                                queryNationIdentify(mContext, userId, mAge, limitStatue, result, userInfo);
                            }
                        } else {
                            handleResult(result);
                        }
                    } else {
                        //未认证过的自动注册用户或者认证过但是未成年的提示
                        if (mAge < 18 && limitStatue != null && limitStatue.getNs() == 1 && limitStatue.getUs() != 0) {
                            showNotiDialog(mAge, result, true, null);
                        } else {
                            handleAuthenLogin(result, true, null);
                        }
//                        handleResult(result);
                    }
                }
            }
        });
    }

    /**
     * 显示自定义位置的Toast
     *
     * @param context
     * @param msg
     * @param gravity
     */
    public static void postShowMsg(final Context context, final String msg, final int gravity) {
        Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        t.setView(UIHelper.createToastView(context, msg));
        t.setGravity(gravity, 0, 50);
        t.show();
    }

    //注册用户返回的实体类转成统一的实体类
    private static LUser generateLuser(JUser user) {
        LUser lUser = new LUser();
        lUser.setBirthday(user.getBirthday());
        lUser.setCity(user.getCity());
        lUser.setId(user.getId());
        lUser.setIdentityName("");
        lUser.setIdentityNum("");
        lUser.setMobile(user.getMobile());
        lUser.setName(user.getName());
        lUser.setNickName(user.getNickName());
        lUser.setOccuId(user.getOccuId());
        lUser.setSex(user.getSex());
        lUser.setStatus(user.getStatus());
        return lUser;
    }

    /**
     * 保存所有登陆用户名和密码信息
     *
     * @param name
     * @param pw
     * @param mContext
     */
    private static void saveLoginInfo(String name, String pw, Context mContext) {
        List<LoginNameInfo> loginNameInfo = CommonUtils.getLoginNameInfo(mContext);
        LoginNameInfo info = new LoginNameInfo();
        info.setName(name);
        info.setPassword(pw);
        //如果存在相同的用户名，则更新密码，否则添加新的用户信息
        if (loginNameInfo.contains(info)) {
            for (LoginNameInfo saveInfo : loginNameInfo) {
                if (saveInfo.getName().equals(name)) {
                    saveInfo.setPassword(pw);
                    break;
                }
            }
        } else {
            loginNameInfo.add(info);
        }
        CommonUtils.saveLoginNameInfo(GsonUtil.list2json(loginNameInfo), mContext);
    }

    /**
     * 获取支付限制信息
     *
     * @param mContext
     * @param age
     * @param idNum
     * @param isAdult
     */
    public static void getPayLimitInfo(Context mContext, int age, String idNum, String isAdult) {
        final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(mContext);
        Map<String, String> limitInfo = new HashMap<String, String>();
        PayLimitInfo payLimitInfo = PayAPI.getPayLimitInfo(mContext, age);
        if (payLimitInfo == null) {
            return;
        }
        if (limitStatue != null) {
            limitInfo.put(Constant.SDK_PAY_STATUS, String.valueOf(limitStatue.getPs()));
        }
        limitInfo.put(Constant.SDK_MIN_AGE, String.valueOf(payLimitInfo.getMinAge()));
        limitInfo.put(Constant.SDK_MAX_AGE, String.valueOf(payLimitInfo.getMaxAge()));
        limitInfo.put(Constant.SDK_S_PAY, String.valueOf(payLimitInfo.getsPay()));
        limitInfo.put(Constant.SDK_C_PAY, String.valueOf(payLimitInfo.getcPay()));
        int ut = 1;
        if (TextUtils.isEmpty(idNum)) {
            ut = 2;
        } else {
            ut = isAdult.equals("1") ? 0 : 1;
        }
        limitInfo.put(Constant.SDK_USER_TYPE, String.valueOf(ut));
        Constant.PAY_LIMIT_INFO_MAP = limitInfo;
    }

    /**
     * 登出操作
     *
     * @param mContext
     */
    public static void logout(Context mContext) {
        if (Constant.isTaptapLogin == 1) {
            Constant.isTaptapLogin = 0;
            LoginManager.getInstance().logout();
        }
        unRegisterTimer();
        Constant.IS_LOGINED = false;
        StartESUserPlugin.hideFloatView();
        StartESUserPlugin.isShowUser = false;
        FloatView.isSetHalf = false;
        Starter.loginBean = null;
        CommonUtils.saveLoginInfo("", mContext);
        CommonUtils.saveUInfo("", mContext);
        CommonUtils.saveShowMoney(mContext, 0);
        CommonUtils.saveServiceUrl(mContext, "");
        CommonUtils.savePlayerId(mContext, "");
        CommonUtils.saveLogState(Starter.mActivity, 0);
        CommonUtils.saveNationIdentity(Starter.mActivity, 0);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloatView.setNormalIcon();
            }
        });
        StartESUserPlugin.showLoginDialog();
        StartOtherPlugin.logOutTT();
        StartOtherPlugin.logTTActionLogin("");
        StartOtherPlugin.logGismActionLogout();
        StartOtherPlugin.logGDTActionSetID("");
        StartOtherPlugin.logoutAqyAction();
        Constant.ESDK_USERID = "";
        Constant.ESDK_TOKEN = "";
        ESdkLog.d("退出登录");
    }

    //回调登录信息并弹出悬浮窗
    private static void handleResult(Map<String, String> result) {
        Starter.mCallback.onLogin(result);
        popFloatView();
    }

    /**
     * 检查悬浮窗权限，显示悬浮窗
     */
    private static void popFloatView() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RomHelper.checkFloatWindowPermission(Starter.mActivity);
                Starter.getInstance().showFloatView();
            }
        }, 500);
    }

    //弹出实名认证框
    private static void popAuthenDialog(Context mContext, final LimitStatusInfo limitStatue,
                                        final Map<String, String> result, final boolean isLogin) {
        AuthenNotiDialog authenNotiDialog = new AuthenNotiDialog(mContext,
                R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, limitStatue.getUs());
        authenNotiDialog.show();
        authenNotiDialog.setResult(new AuthenNotiDialog.authenResult() {
            @Override
            public void authenSuccess(String userBirthdate, String idNum) {
                //手动验证成功回调
                Map<String, String> authenResult = new HashMap<String, String>();
                authenResult.put(ESConstant.SDK_IS_IDENTITY_USER, "false");
                authenResult.put(ESConstant.SDK_USER_BIRTH_DATE, userBirthdate);
                //更新当前用户的身份证信息
                Starter.loginBean.getUser().setIdentityNum(idNum);
                int uAge = CommonUtils.getAge(idNum);
                /*if (isLogin) {
                    Starter.mCallback.onLogin(result);
                } else {
                    Starter.mCallback.onRegister(result);
                }
                //回调认证结果
                Starter.mCallback.onUserCert(authenResult);*/
                //认证成功且是未成年的用户提示
                if (limitStatue.getNs() == 1 && uAge < 18) {
                    showNotiDialog(uAge, result, isLogin, authenResult);
                } else {
                    handleAuthenLogin(result, isLogin, authenResult);
                }
//                popFloatView();
            }
        });
        authenNotiDialog.setCloseListener(new AuthenNotiDialog.setCloseListener() {
            @Override
            public void dialogClose() {
                //关闭认证回调
                //未认证过的且不是自动注册的用户的提示
                if (limitStatue.getNs() == 1) {
                    showNotiDialog(0, result, isLogin, null);
                } else {
                    handleAuthenLogin(result, isLogin, null);
                }
             /*   if (isLogin) {
                    Starter.mCallback.onLogin(result);
                } else {
                    Starter.mCallback.onRegister(result);
                }*/
//                popFloatView();
            }
        });
    }

    /**
     * 公告提醒框
     */
    public static void showNotiDialog(int age, final Map<String, String> result, final boolean isLogin
            , final Map<String, String> authenResult) {
        String text = "";
        if (age == 0 || TextUtils.isEmpty(String.valueOf(age))) {
            text = Constant.guestLimitNoti;
        } else {
            if (age < 8 && age > 0) {
                text = Constant.babyLimitNoti;
            } else if (age >= 8 && age < 16) {
                text = Constant.boyLimitNoti;
            } else if (age >= 16 && age < 18) {
                text = Constant.teeLimitNoti;
            }
        }
        NotiDialog authenDialog = new NotiDialog(Starter.mActivity, R.style.easou_dialog, Gravity.CENTER,
                0.8f, 0, false, "", text, "");
        authenDialog.show();
        authenDialog.setOnCloseListener(new NotiDialog.OnCloseListener() {
            @Override
            public void close() {
                uploadTime(false, result, isLogin, authenResult);
            }
        });
    }

    public static void handleAuthenLogin(Map<String, String> result, boolean isLogin, Map<String, String> authenResult) {
        if (result == null) {
            return;
        }
        if (isLogin) {
            Starter.mCallback.onLogin(result);
        } else {
            Starter.mCallback.onRegister(result);
        }
        if (authenResult != null) {
            Starter.mCallback.onUserCert(authenResult);
        }
        popFloatView();
    }

    public static void startTimer() {
        AppTimeWatcher.getInstance().startTimer();
    }

    public static void unRegisterTimer() {
        AppTimeWatcher.getInstance().unRegisterWatcher();
    }
}
