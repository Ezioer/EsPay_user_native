package com.easou.androidsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.login.service.LoginNameInfo;
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
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.JUser;
import com.easou.androidsdk.login.service.LUser;
import com.easou.androidsdk.login.service.LimitStatusInfo;
import com.easou.androidsdk.login.service.LoginBean;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartLogPlugin;
import com.easou.androidsdk.plugin.StartOtherPlugin;
import com.easou.androidsdk.romutils.RomHelper;
import com.easou.androidsdk.ui.FloatView;
import com.easou.androidsdk.ui.UIHelper;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.GsonUtil;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

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

    public static void getUserInfoById(final String id, final Context mContext) {
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
    public static void handleAccountLogin(final LoginCallBack callBack, final String name, final String password, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<LoginBean> login = AuthAPI.login(name, password, true, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (login.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                        handleLoginBean(login, mContext, password);
                    } else {
                        if (login.getDescList().size() > 0) {
                            callBack.loginFail(login.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("登录失败");
                        }
                    }

                } catch (Exception e) {
                    ESdkLog.d("账号登录失败" + e.toString());
                    callBack.loginFail("登录失败");
                }
            }
        });
    }

    /**
     * 账号登录
     *
     * @param name
     * @param password
     * @param mContext
     */
    public static void silenceLogin(final String name, final String password, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<LoginBean> login = AuthAPI.login(name, password, true, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (login.getResultCode().equals(CodeConstant.OK)) {
                        reLogin(login, mContext, password);
                    }
                } catch (Exception e) {
                    ESdkLog.d("账号登录失败" + e.toString());
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
    public static void handleAccountRegister(final LoginCallBack callBack, final String name, final String password, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<AuthBean> login = RegisterAPI.registByName(name, password, true, "", RegisterAPI.getRequestInfo(mContext), mContext);
                    if (login.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                        handleRegister(login, mContext, false, password);
                    } else {
                        if (login.getDescList().size() > 0) {
                            callBack.loginFail(login.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("账号注册失败");
                        }
                    }
                } catch (Exception e) {
                    ESdkLog.d("账号注册失败" + e.toString());
                    callBack.loginFail("账号注册失败");
                }
            }
        });
    }

    /**
     * 用户中心更新密码
     *
     * @param callBack
     * @param newPw
     * @param oldPw
     * @param mContext
     */
    public static void updatePassword(final LoginCallBack callBack, final String newPw, final String oldPw, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> userInfo = UserAPI.updatePasswd(Constant.ESDK_TOKEN, oldPw, newPw, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        silenceLogin(Starter.loginBean.getUser().getName(), newPw, mContext);
                        callBack.loginSuccess();
                    } else {
                        if (userInfo.getDescList().size() > 0) {
                            callBack.loginFail(userInfo.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("更改失败");
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
    public static void requestResetPassword(final LoginCallBack callBack, final String phone, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> info = AuthAPI.requestResetPass(phone, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (info.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                    } else {
                        if (info.getDescList().size() > 0) {
                            callBack.loginFail(info.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("操作失败");
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
    public static void lookPassword(final AuthenCallBack callBack, final String phone, final String newPw, final String code, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<AuthBean> userInfo = AuthAPI.applyResetPass(phone, newPw, code, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess(userInfo.getResult().getUser().getName());
                    } else {
                        if (userInfo.getDescList().size() > 0) {
                            callBack.loginFail(userInfo.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("操作失败");
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
    public static void requestBindOrUnBind(final LoginCallBack callBack, final String phone, final boolean type, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> info = UserAPI.requestBindMobile(Constant.ESDK_TOKEN, phone, RegisterAPI.getRequestInfo(mContext), mContext, type);
                    if (info.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                    } else {
                        if (info.getDescList().size() > 0) {
                            callBack.loginFail(info.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("操作失败");
                        }
                    }
                } catch (EucAPIException e) {
                    e.printStackTrace();
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
    public static void applyBindOrUnBind(final LoginCallBack callBack, final String phone, final String code, final boolean type, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<JUser> info = UserAPI.applyBindMobile(Constant.ESDK_TOKEN, phone, code, RegisterAPI.getRequestInfo(mContext), mContext, type);
                    if (info.getResultCode().equals(CodeConstant.OK)) {
                        if (type) {
                            Starter.loginBean.getUser().setMobile(phone);
                        } else {
                            Starter.loginBean.getUser().setMobile("");
                        }
                        callBack.loginSuccess();
                    } else {
                        if (info.getDescList().size() > 0) {
                            callBack.loginFail(info.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("操作失败");
                        }
                    }
                } catch (EucAPIException e) {
                    e.printStackTrace();
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
    public static void userCenterAuthen(final AuthenCallBack callBack, final String name, final String idNum, final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<String> userInfo = AuthAPI.userIdentify(name, idNum, Constant.ESDK_USERID, Constant.ESDK_APP_ID, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess(CommonUtils.getYMDfromIdNum(idNum));
                    } else {
                        if (userInfo.getDescList().size() > 0) {
                            callBack.loginFail(userInfo.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("认证失败");
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
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EucApiResult<AuthBean> userInfo = RegisterAPI.autoRegist(true, RegisterAPI.getRequestInfo(mContext), mContext);
                    if (userInfo.getResultCode().equals(CodeConstant.OK)) {
                        callBack.loginSuccess();
                        handleRegister(userInfo, mContext, true, "");
                    } else {
                        if (userInfo.getDescList().size() > 0) {
                            callBack.loginFail(userInfo.getDescList().get(0).getD());
                        } else {
                            callBack.loginFail("登录失败");
                        }
                    }
                } catch (Exception e) {
                    ESdkLog.d("游客登录失败" + e.toString());
                    callBack.loginFail("登录失败");
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
    public static void handleRegister(EucApiResult<AuthBean> userInfo, final Context mContext, final boolean isShowInfoDialog, String pw) {
        saveLoginInfo(userInfo.getResult().getUser().getName(), userInfo.getResult().getUser().getPasswd(), mContext);
        final String userId = String.valueOf(userInfo.getResult().getUser().getId());
        final String userName = String.valueOf(userInfo.getResult().getUser().getName());
        String token = String.valueOf(userInfo.getResult().getToken().token);
        String userBirthdate = String.valueOf(userInfo.getResult().getUser().getBirthday());
        final String password = String.valueOf(userInfo.getResult().getUser().getPasswd());

        LUser user = generateLuser(userInfo.getResult().getUser());
        user.setPasswd(pw.isEmpty() ? password : pw);
        LoginBean login = new LoginBean(userInfo.getResult().getToken(), user, userInfo.getResult().getEsid(), true);
        Starter.loginBean = login;
        CommonUtils.saveLoginInfo(GsonUtil.toJson(login), mContext);
        //上报sdk数据
        StartOtherPlugin.logTTActionLogin(userId);
        StartOtherPlugin.logGismActionLogin(userId);
        StartOtherPlugin.logGDTActionSetID(userId);
        StartOtherPlugin.loginAqyAction();
        StartOtherPlugin.createRoleAqyAction(userName);

        Constant.IS_LOGINED = true;
        Constant.ESDK_USERID = userId;
        Constant.ESDK_TOKEN = token;
        final Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);
        result.put(ESConstant.SDK_USER_TOKEN, token);
        result.put(ESConstant.SDK_USER_BIRTH_DATE, "0");
        result.put(ESConstant.SDK_IS_IDENTITY_USER, "0");
        result.put(ESConstant.SDK_IS_ADULT, "0");
        result.put(ESConstant.SDK_IS_HOLIDAY, "0");
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StartLogPlugin.startSdkLoginLog(userId, userName);
                if (!isShowInfoDialog) {
                    final Map<String, String> register = new HashMap<String, String>();
                    result.put(ESConstant.SDK_USER_ID, userId);
                    result.put(ESConstant.SDK_USER_NAME, userName);
                    postShowMsg(mContext, "欢迎回来, " + userName + "!", Gravity.TOP);
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(mContext);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (limitStatue != null && limitStatue.getUs() != 0) {
                                        AuthenNotiDialog authenNotiDialog = new AuthenNotiDialog(mContext, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, limitStatue.getUs());
                                        authenNotiDialog.show();
                                        authenNotiDialog.setResult(new AuthenNotiDialog.authenResult() {
                                            @Override
                                            public void authenSuccess(String userBirthdate) {
                                                Map<String, String> result = new HashMap<String, String>();
                                                result.put(ESConstant.SDK_IS_IDENTITY_USER, "false");
                                                result.put(ESConstant.SDK_USER_BIRTH_DATE, userBirthdate);
                                                Starter.mCallback.onRegister(register);
                                                Starter.mCallback.onUserCert(result);
                                                new Handler().postDelayed(new Runnable() {
                                                    public void run() {
                                                        RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                                        Starter.getInstance().showFloatView();
                                                    }
                                                }, 3000);
                                            }
                                        });
                                        authenNotiDialog.setCloseListener(new AuthenNotiDialog.setCloseListener() {
                                            @Override
                                            public void dialogClose() {
                                                Starter.mCallback.onRegister(register);
                                                new Handler().postDelayed(new Runnable() {
                                                    public void run() {
                                                        RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                                        Starter.getInstance().showFloatView();
                                                    }
                                                }, 3000);
                                            }
                                        });
                                    } else {
                                        Starter.mCallback.onRegister(register);
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                                Starter.getInstance().showFloatView();
                                            }
                                        }, 3000);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    AccountInfoDialog infoDialog = new AccountInfoDialog(Starter.mActivity, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, userName, password);
                    infoDialog.show();
                    infoDialog.setCloseListener(new AccountInfoDialog.OnCloseDialogListener() {
                        @Override
                        public void dialogClose() {
                            Starter.mCallback.onLogin(result);
                            postShowMsg(mContext, "欢迎回来, " + userName + "!", Gravity.TOP);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                    Starter.getInstance().showFloatView();
                                }
                            }, 3000);
                        }
                    });
                }
            }
        });
    }

    public static void reLogin(final EucApiResult<LoginBean> userInfo, final Context mContext, String pw) {
        saveLoginInfo(userInfo.getResult().getUser().getName(), pw, mContext);
        final String password = String.valueOf(userInfo.getResult().getUser().getPasswd());
        userInfo.getResult().getUser().setPasswd(pw.isEmpty() ? password : pw);
        String token = String.valueOf(userInfo.getResult().getToken().token);
        Constant.ESDK_TOKEN = token;
        Starter.loginBean = userInfo.getResult();
        CommonUtils.saveLoginInfo(GsonUtil.toJson(userInfo.getResult()), mContext);
    }

    public static void handleLoginBean(final EucApiResult<LoginBean> userInfo, final Context mContext, String pw) {
        saveLoginInfo(userInfo.getResult().getUser().getName(), pw, mContext);
        final String userId = String.valueOf(userInfo.getResult().getUser().getId());
        final String userName = String.valueOf(userInfo.getResult().getUser().getName());
        String token = String.valueOf(userInfo.getResult().getToken().token);
        String userBirthdate = String.valueOf(userInfo.getResult().getUser().getBirthday());
        final String password = String.valueOf(userInfo.getResult().getUser().getPasswd());
        userInfo.getResult().getUser().setPasswd(pw.isEmpty() ? password : pw);
        Starter.loginBean = userInfo.getResult();
        CommonUtils.saveLoginInfo(GsonUtil.toJson(userInfo.getResult()), mContext);
        //上报sdk数据
        StartOtherPlugin.logTTActionLogin(userId);
        StartOtherPlugin.logGismActionLogin(userId);
        StartOtherPlugin.logGDTActionSetID(userId);
        StartOtherPlugin.loginAqyAction();
        StartOtherPlugin.createRoleAqyAction(userName);

        Constant.IS_LOGINED = true;
        Constant.ESDK_USERID = userId;
        Constant.ESDK_TOKEN = token;
        final Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);
        result.put(ESConstant.SDK_USER_TOKEN, token);
        result.put(ESConstant.SDK_USER_BIRTH_DATE, !TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum()) ?
                CommonUtils.getYMDfromIdNum(userInfo.getResult().getUser().getIdentityNum()) : "0");
        result.put(ESConstant.SDK_IS_IDENTITY_USER, TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum()) ? "0" : "1");
        String isAdult = "0";

        if (!TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum())) {
            int age = CommonUtils.getAge(userInfo.getResult().getUser().getIdentityNum());
            isAdult = age > 18 ? "1" : "0";
            getPayLimitInfo(mContext, age, userInfo.getResult().getUser().getIdentityNum(), isAdult);
        }

        result.put(ESConstant.SDK_IS_ADULT, isAdult);
        result.put(ESConstant.SDK_IS_HOLIDAY, "0");
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StartLogPlugin.startSdkLoginLog(userId, userName);
                postShowMsg(mContext, "欢迎回来, " + userName + "!", Gravity.TOP);
                if (TextUtils.isEmpty(userInfo.getResult().getUser().getIdentityNum()) && userInfo.getResult().getUser().getIsAutoRegist() != 1) {
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(mContext);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (limitStatue != null && limitStatue.getUs() != 0) {
                                        AuthenNotiDialog authenNotiDialog = new AuthenNotiDialog(mContext, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, limitStatue.getUs());
                                        authenNotiDialog.show();
                                        authenNotiDialog.setResult(new AuthenNotiDialog.authenResult() {
                                            @Override
                                            public void authenSuccess(String userBirthdate) {
                                                Map<String, String> result = new HashMap<String, String>();
                                                result.put(ESConstant.SDK_IS_IDENTITY_USER, "false");
                                                result.put(ESConstant.SDK_USER_BIRTH_DATE, userBirthdate);
                                                Starter.mCallback.onLogin(result);
                                                Starter.mCallback.onUserCert(result);
                                                new Handler().postDelayed(new Runnable() {
                                                    public void run() {
                                                        RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                                        Starter.getInstance().showFloatView();
                                                    }
                                                }, 3000);
                                            }
                                        });
                                        authenNotiDialog.setCloseListener(new AuthenNotiDialog.setCloseListener() {
                                            @Override
                                            public void dialogClose() {
                                                Starter.mCallback.onLogin(result);
                                                new Handler().postDelayed(new Runnable() {
                                                    public void run() {
                                                        RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                                        Starter.getInstance().showFloatView();
                                                    }
                                                }, 3000);
                                            }
                                        });
                                    } else {
                                        Starter.mCallback.onLogin(result);
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                RomHelper.checkFloatWindowPermission(Starter.mActivity);
                                                Starter.getInstance().showFloatView();
                                            }
                                        }, 3000);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Starter.mCallback.onLogin(result);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            RomHelper.checkFloatWindowPermission(Starter.mActivity);
                            Starter.getInstance().showFloatView();
                        }
                    }, 3000);
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

    private static void saveLoginInfo(String name, String pw, Context mContext) {
        List<LoginNameInfo> loginNameInfo = CommonUtils.getLoginNameInfo(mContext);
        LoginNameInfo info = new LoginNameInfo();
        info.setName(name);
        info.setPassword(pw);
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

    public static void getPayLimitInfo(Context mContext, int age, String idNum, String isAdult) {
        final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(mContext);
        Map<String, String> limitInfo = new HashMap<String, String>();
        PayLimitInfo payLimitInfo = PayAPI.getPayLimitInfo(mContext, age);
        limitInfo.put(Constant.SDK_PAY_STATUS, String.valueOf(limitStatue.getPs()));
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

    public static void logout(Context mContext) {
        Constant.IS_LOGINED = false;
        StartESUserPlugin.hideFloatView();
        StartESUserPlugin.isShowUser = false;
        FloatView.isSetHalf = true;
        CommonUtils.saveLoginInfo("", mContext);
        StartESUserPlugin.showLoginDialog();
        StartOtherPlugin.logTTActionLogin("");
        StartOtherPlugin.logGismActionLogout();
        StartOtherPlugin.logGDTActionSetID("");
        StartOtherPlugin.logoutAqyAction();
        Constant.ESDK_USERID = "";
        Constant.ESDK_TOKEN = "";
        ESdkLog.d("退出登录");
    }
}
