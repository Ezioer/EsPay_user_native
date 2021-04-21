package com.easou.androidsdk.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.dialog.AuthenNotiDialog;
import com.easou.androidsdk.dialog.LoginWayDialog;
import com.easou.androidsdk.dialog.UserCenterDialog;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.MoneyDataCallBack;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.LimitStatusInfo;
import com.easou.androidsdk.login.service.LoginBean;
import com.easou.androidsdk.login.service.MoneyBaseInfo;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.ui.ESUserWebActivity;
import com.easou.androidsdk.ui.FloatView;
import com.easou.androidsdk.util.AES;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.FileHelper;
import com.easou.androidsdk.util.HostRequestUtils;
import com.easou.androidsdk.util.NetworkUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;
import com.taptap.sdk.Profile;

import java.util.Map;

public class StartESUserPlugin {

    /**
     * 登陆接口
     */
    public static void loginSdk() {

        StartOtherPlugin.getOaid(Starter.mActivity);

        String channel = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.CHANNEL_MARK);
        if (channel.equals("DHT")) {
            Constant.PAY_CHANNEl = 1;
        } else if (channel.equals("YY")) {
            Constant.PAY_CHANNEl = 2;
        } else if (channel.equals("ZKX")) {
            Constant.PAY_CHANNEl = 3;
        } else if (channel.equals("WZYY")) {
            Constant.PAY_CHANNEl = 4;
        } else {
            Constant.PAY_CHANNEl = 3;
        }
        //添加晃动手机检测
		/*SensorManagerHelper sensorManagerHelper = new SensorManagerHelper(Starter.mActivity);
		sensorManagerHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {
			@Override
			public void onShake() {
				ESToast.getInstance().ToastShow(Starter.mActivity, "dont shake me");
			}
		});*/

        startLogin();
        final LoginBean info = CommonUtils.getLoginInfo(Starter.mActivity);
        final Profile currentProfile = Profile.getCurrentProfile();
        if (info == null && currentProfile == null) {
            //宜搜账号和tap账号都未登陆显示登陆框
            showLoginDialog();
        } else {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(Starter.mActivity);
                        if (limitStatue != null) {
                            CommonUtils.saveNationIdentity(Starter.mActivity, limitStatue.getRns());
                            CommonUtils.saveLogState(Starter.mActivity, limitStatue.getLus());
                        }
                    } catch (Exception e) {
                    }
                    Starter.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //已登陆用户直接账号密码登录
                            if (currentProfile != null) {
                                //tap登录
                                StartESAccountCenter.handleTapLogin(currentProfile.getOpenid(), new LoginCallBack() {
                                    @Override
                                    public void loginSuccess() {
                                        //登陆成功回调
                                        Constant.isTaptapLogin = 1;
                                        ESdkLog.d("login success");
                                    }

                                    @Override
                                    public void loginFail(final String msg) {
                                        Starter.mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showLoginDialog();
                                                ESToast.getInstance().ToastShow(Starter.mActivity, msg);
                                            }
                                        });
                                    }
                                }, Starter.mActivity);
                            } else {
                                //宜搜登录
                                final String u = CommonUtils.getUInfo(Starter.mActivity);
                                if (TextUtils.isEmpty(u)) {
                                    StartESAccountCenter.handleAccountLogin(new LoginCallBack() {
                                        @Override
                                        public void loginSuccess() {
                                            //登陆成功回调
                                            ESdkLog.d("login success");
                                        }

                                        @Override
                                        public void loginFail(final String msg) {
                                            Starter.mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showLoginDialog();
                                                    ESToast.getInstance().ToastShow(Starter.mActivity, msg);
                                                }
                                            });
                                        }
                                    }, info.getUser().getName(), info.getUser().getPasswd(), Starter.mActivity);
                                } else {
                                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                                        @Override
                                        public void run() {
                                            StartESAccountCenter.loginByTokenUser(true, new LoginCallBack() {
                                                @Override
                                                public void loginSuccess() {
                                                    //登陆成功回调
                                                    ESdkLog.d("login success");
                                                }

                                                @Override
                                                public void loginFail(final String msg) {
                                                    Starter.mActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            showLoginDialog();
                                                            ESToast.getInstance().ToastShow(Starter.mActivity, msg);
                                                        }
                                                    });
                                                }
                                            }, u, Starter.mActivity);
                                        }
                                    });

                                }
                            }
                        }
                    });
                }
            });

        }
    }

    /**
     * 登陆前操作
     */
    public static void startLogin() {

        // 获取deviceID
        String imei = Tools.getDeviceImei(Starter.mActivity);
        if (!TextUtils.isEmpty(imei.trim())) {
            Constant.IMEI = imei;
        }
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Constant.NET_IP = Tools.getNetIp();
                startRequestHost(Starter.mActivity);
                Starter.getInstance().startAppLog();
            }
        });
    }

    /**
     * 登录框
     */
    public static LoginWayDialog mLoginDialog;

    public static void showLoginDialog() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                int status = 0;
                try {
                    //获取登陆限制信息
                    final LimitStatusInfo limitStatue = AuthAPI.getLimitStatue(Starter.mActivity);
                    if (limitStatue != null) {
                        CommonUtils.saveNationIdentity(Starter.mActivity, limitStatue.getRns());
                        CommonUtils.saveLogState(Starter.mActivity, limitStatue.getLus());
                    }
                    status = AuthAPI.identifyStatus(Tools.getDeviceImei(Starter.mActivity), RegisterAPI.getRequestInfo(Starter.mActivity), Starter.mActivity);
                    if (limitStatue == null || limitStatue.getUs() == 0) {
                        status = 0;
                    }
                } catch (Exception e) {
                    status = 0;
                }
                //开启实名认证后，如果status为1，则隐藏游客登陆
                final int isAutoRegister = status;
                Starter.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoginDialog == null) {
                            mLoginDialog = new LoginWayDialog(Starter.mActivity, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0);
                        }
                        mLoginDialog.show();
                        mLoginDialog.setGuestShow(isAutoRegister);
                    }
                });
            }
        });

    }

    public static void hideUserCenter() {
        if (mUserCenterDialog != null) {
            mUserCenterDialog.dismiss();
            mUserCenterDialog = null;
        }
    }

    public static UserCenterDialog mUserCenterDialog = null;

    public static boolean isShowUser = false;

    public static void showUserCenterDialog() {
		/*if (isShowUser) {
			UserCenterActivity.mContext.moveTaskToBack(true);
			ESdkLog.d("隐藏用户中心");
			isShowUser = false;
		} else {
			ESdkLog.d("显示用户中心");
			Starter.mActivity.startActivity(new Intent(Starter.mActivity, UserCenterActivity.class));
			isShowUser = true;
		}*/

        if (mUserCenterDialog == null) {
            mUserCenterDialog = new UserCenterDialog(Starter.mActivity, R.style.easou_usercenterdialog, Gravity.LEFT, 1f, 1);
        }
        if (mUserCenterDialog.isShowing()) {
            //正在展示则隐藏用户中心并将图标设置显示一半
            FloatView.isSetHalf = false;
            mUserCenterDialog.dismiss();
        } else {
            //显示正常图标并显示用户中心
            FloatView.isSetHalf = true;
            mUserCenterDialog.show();
            mUserCenterDialog.setUpdatePwHide();
            //设置用户中心的dialog为全屏显示
            Window window = mUserCenterDialog.getWindow();
            //必须在show之后设置dialog的宽高
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
    }

    /**
     * 弹出实名认证对话框
     */
    public static AuthenNotiDialog authenDialog = null;

    public static void showUserAuthenDialog() {
        if (authenDialog == null) {
            authenDialog = new AuthenNotiDialog(Starter.mActivity, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, 4);
        }
        authenDialog.show();
    }

    /**
     * 进入H5 SDK页面
     */
    public static void enterH5View() {

        Intent intent = new Intent();
        intent.putExtra("params", getNewParam());
        intent.setClass(Starter.mActivity, ESUserWebActivity.class);
        Starter.mActivity.startActivity(intent);
    }

    /**
     * 获取SDK用户信息
     */
    public static void getH5UserInfo() {
        StartESAccountCenter.getUserInfoById(Constant.ESDK_USERID);
//		ESUserWebActivity.clientToJS(Constant.YSTOJS_GET_USERINFO, null);
    }

    /**
     * 每隔5分钟去请求服务器更新用户游玩时长
     */
    public static void postTime() {
        ESUserWebActivity.clientToJS(Constant.YSTOJS_UPLOAD_TIME, null);
    }

    /**
     * 判断用户是否实名认证
     */
    public static void getUserCertStatus() {
        showUserAuthenDialog();
	/*	ESUserWebActivity.clientToJS(Constant.YSTOJS_IS_CERTUSER, null);
		enterH5View();*/
    }

    /**
     * 打开实名认证页面
     */
    public static void showUserCert() {

        ESUserWebActivity.clientToJS(Constant.YSTOJS_USERCERT, null);
        enterH5View();
    }

    /**
     * 显示SDK页面
     */
    public static void showSdkView() {
        showUserCenterDialog();
		/*ESUserWebActivity.clientToJS(Constant.YSTOJS_CLICK_FLOATVIEW, null);
		enterH5View();*/
    }


    /**
     * 游戏登录日志
     */
    public static void startGameLoginLog(Map<String, String> playerInfo) {

        StartLogPlugin.startGameLoginLog(playerInfo);
//        initMoneyActivity(playerInfo.get(ESConstant.PLAYER_ID), playerInfo.get(ESConstant.PLAYER_SERVER_ID));
//		ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGIN_LOG, playerInfo);
    }

    private static void initMoneyActivity(String playerId, String serverId) {
        if (CommonUtils.isNotNullOrEmpty(playerId) && CommonUtils.isNotNullOrEmpty(serverId)) {
            CommonUtils.savePlayerId(Starter.mActivity, playerId);
            CommonUtils.saveServerId(Starter.mActivity, serverId);
            /*StartESAccountCenter.moneyBaseInfo(new MoneyDataCallBack<MoneyBaseInfo>() {
                @Override
                public void success(MoneyBaseInfo moneyBaseInfo) {
                    Starter.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.saveShowMoney(Starter.mActivity, 1);
                            FloatView.setMoneyIcon();
                        }
                    });
                }

                @Override
                public void fail(String msg) {
                    CommonUtils.saveShowMoney(Starter.mActivity, 0);
                }
            }, Starter.mActivity, playerId, serverId);*/
        }
    }

    /**
     * 获取网页SDK所需参数
     */
    public static String getNewParam() {

        String encryptKey = CommonUtils.readPropertiesValue(Starter.mActivity, "key");
        try {
            encryptKey = AES.encrypt(encryptKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String appid = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID);
        String token = CommonUtils.getH5Token(Starter.mActivity);

        if (TextUtils.isEmpty(token)) {
            try {
//				AuthBean bean = CommonUtils.getAuthBean(Starter.mActivity,appid);
                String bean = CommonUtils.getTokenFromSD(appid);
                if (bean != null) {
//					token = bean.getToken().getToken();
                    token = bean;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        String param = "&appId=" + appid
                + "&qn=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN)
                + "&partnerId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.PARTENER_ID)
                + "&secretKey=" + encryptKey
                + "&deviceId=" + Constant.IMEI
                + "&clientIp=" + Constant.NET_IP
                + "&oaId=" + Constant.OAID
                + "&phoneOs=" + Constant.SDK_PHONEOS
                + "&phoneBrand=" + Tools.getDeviceBrand()
                + "&phoneModel=" + Tools.getSystemModel()
                + "&phoneVersion=" + Tools.getSystemVersion()
                + "&userToken=" + token
                + "&isSimulator=" + Constant.IS_SIMULATOR
                + "&netMode=" + NetworkUtils.getNetworkState(Starter.mActivity)
                + "&telecom=" + NetworkUtils.getOperator(Starter.mActivity);

        ESdkLog.d("上传的oaid：" + Constant.OAID);
        System.out.println("param：" + param);

        return param;
    }

    /**
     * 显示悬浮窗
     */
    public static void showFloatView() {
        ESdkLog.d("进入悬浮窗开关");
        if (Constant.IS_LOGINED) {
            ESdkLog.d("显示悬浮窗");
            FloatView.show(Starter.mActivity);
        } /*else {
			if (Constant.IS_ENTERED_SDK) {
				// 未登陆显示用户中心
				StartESUserPlugin.enterH5View();
			}
		}*/
    }

    /**
     * 隐藏悬浮窗
     */
    public static void hideFloatView() {
//		UserCenterActivity.mContext.moveTaskToBack(true);
        try {
            isShowUser = false;
            if (mUserCenterDialog != null && mUserCenterDialog.isShowing()) {
                FloatView.isSetHalf = false;
                mUserCenterDialog.dismiss();
            }
            ESdkLog.d("隐藏悬浮窗");
            FloatView.close();
        } catch (Exception e) {
            ESdkLog.d("隐藏悬浮窗出错" + e.getMessage());
        }
    }

    /**
     * 请求host信息
     */
    public static void startRequestHost(final Activity activity) {

        try {
            // 读取存储的host信息
            String jsonData = FileHelper.readFile(Constant.getHostInfoFile(activity));
            if (jsonData == null) {
                jsonData = FileHelper.readFile(Constant.getSDHostInfoFile());
            }

            if (jsonData == null || jsonData.equals("")) {
                HostRequestUtils.requestHostInfo(activity, false);
            } else {
                HostRequestUtils.requestHostInfo(activity, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从Properties文件中读取配置信息
     *
     * @param key：参数名称
     */
    public static String getPropValue(Context _context, String key) {
        return CommonUtils.readPropertiesValue(_context, key);
    }
}
