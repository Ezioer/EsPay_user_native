package com.easou.androidsdk.dialog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.AuthenCallBack;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.MoneyDataCallBack;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.UserAPI;
import com.easou.androidsdk.login.service.CashHistoryInfo;
import com.easou.androidsdk.login.service.CashLevelInfo;
import com.easou.androidsdk.login.service.CheckBindByUserIdInfo;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.GiftBean;
import com.easou.androidsdk.login.service.GiftInfo;
import com.easou.androidsdk.login.service.MoneyBalance;
import com.easou.androidsdk.login.service.MoneyBaseInfo;
import com.easou.androidsdk.login.service.MoneyGroupAndRoleInfo;
import com.easou.androidsdk.login.service.MoneyGroupInfo;
import com.easou.androidsdk.login.service.MoneyGroupList;
import com.easou.androidsdk.login.service.MoneyList;
import com.easou.androidsdk.login.service.MoneyListDetail;
import com.easou.androidsdk.login.service.MoneyListInfo;
import com.easou.androidsdk.login.service.RoleMoneyRule;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class UserCenterDialog extends BaseDialog {

    public UserCenterDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight) {
        super(context, animation, gravity, mWidth, mHeight, false);
        mContext = context;
    }

    private View mView;
    private Context mContext;
    private boolean bindType = true;
    private int currentType = 0;
    private LinearLayout llMoney;
    private RelativeLayout rlChangePw;
    private ListView giftList;
    private TextView tvGiftCode, mTvMyMoney, mTotalMoneyValue;
    private RelativeLayout llGiftCode;
    private CountDownTimer getTimer;
    private TextView mBindPhoneType;
    private ImageView mIvBindPhone;
    private ImageView mIvAuthen;
    private ImageView mIvBindWx;
    private ImageView mMoney;
    private boolean isBindWX;
    private TextView mNoActivity, mTvNoRole;
    private ListView mLvMoneyType;
    private Map<Integer, List<MoneyListDetail>> lists;
    private RelativeLayout mRlBindWxNoti, mRlBindWx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_usercenter, null);
        setContentView(mView);
        final Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                uiOptions |= 0x00001000;
                window.getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
    }

    public void setUpdatePwHide() {
        if (rlChangePw != null) {
            if (Constant.isTaptapLogin == 1) {
                rlChangePw.setVisibility(View.GONE);
            } else {
                rlChangePw.setVisibility(View.VISIBLE);
            }
        }

        if (currentType == 2 && giftList != null && llGiftCode != null && tvGiftCode != null) {
            initGifts();
        }

       /* if (CommonUtils.isShowMoney(mContext) == 1) {
            mMoney.setVisibility(View.VISIBLE);
        } else {
            mMoney.setVisibility(View.GONE);
        } */
        bindWXStateChange();
        if (currentType == 3) {
            initMoneyPage(CommonUtils.getPlayerId(mContext), CommonUtils.getServerId(mContext));
        }
    }

    private void initView() {
        final View includeMenu = findViewById(R.id.include_usermenu);
        final ImageView ivMe = (ImageView) findViewById(R.id.iv_me);
        final ImageView ivGift = (ImageView) findViewById(R.id.iv_gift);
        mMoney = findViewById(R.id.iv_money);
        //首页
        final TextView tvWelcome = (TextView) includeMenu.findViewById(R.id.tv_username);
        final RelativeLayout llGift = (RelativeLayout) findViewById(R.id.ll_gift);
        llMoney = findViewById(R.id.ll_money);
        final RelativeLayout rlMoney = findViewById(R.id.rl_money_content);
        llGiftCode = (RelativeLayout) findViewById(R.id.ll_giftcode);
        tvGiftCode = (TextView) findViewById(R.id.tv_giftcode);
        giftList = (ListView) findViewById(R.id.listview);
        final LinearLayout llUser = (LinearLayout) findViewById(R.id.ll_user);
        tvWelcome.setText("您好：" + Starter.loginBean.getUser().getName());

        final ImageView ivService = (ImageView) findViewById(R.id.iv_service);
        ivMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 0) {
                    currentType = 0;
                    tvWelcome.setText("您好：" + Starter.loginBean.getUser().getName());
                    ivMe.setImageResource(R.drawable.icon_main_mehign);
                    ivService.setImageResource(R.drawable.icon_main_service);
                    ivGift.setImageResource(R.drawable.icon_main_gift);
                    mMoney.setImageResource(R.drawable.ic_moneyunselect);
                    llUser.setVisibility(View.VISIBLE);
                    llGift.setVisibility(View.GONE);
                    rlMoney.setVisibility(View.GONE);
                    resetTimer();
                    if (WebViewActivity.mActivity != null) {
                        WebViewActivity.mActivity.finish();
                        WebViewActivity.mActivity = null;
                    }
                }
            }
        });

        ivGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 2) {
                    currentType = 2;
                    ivMe.setImageResource(R.drawable.icon_main_me);
                    ivService.setImageResource(R.drawable.icon_main_service);
                    ivGift.setImageResource(R.drawable.icon_main_gifthign);
                    mMoney.setImageResource(R.drawable.ic_moneyunselect);
                    llUser.setVisibility(View.GONE);
                    llGift.setVisibility(View.VISIBLE);
                    rlMoney.setVisibility(View.GONE);
                    resetTimer();
                    if (WebViewActivity.mActivity != null) {
                        WebViewActivity.mActivity.finish();
                        WebViewActivity.mActivity = null;
                    }
                    initGifts();
                }
            }
        });

        ivService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 1;
                String url = CommonUtils.getServiceUrl(mContext);
                if (TextUtils.isEmpty(url)) {
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            final String serviceUrl = UserAPI.getServiceUrl(mContext);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.saveServiceUrl(mContext, serviceUrl);
                                    Intent intent = new Intent(mContext, WebViewActivity.class);
                                    intent.putExtra("url", serviceUrl);
                                    mContext.startActivity(intent);
                                }
                            });
                        }
                    });
                } else {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("url", url);
                    mContext.startActivity(intent);
                }
            }
        });

        //红包
        mMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentType != 3) {
                    currentType = 3;
                    String playerId = CommonUtils.getPlayerId(mContext);
                    String serverId = CommonUtils.getServerId(mContext);
                    mMoney.setImageResource(R.drawable.ic_moneyselect);
                    ivMe.setImageResource(R.drawable.icon_main_me);
                    ivService.setImageResource(R.drawable.icon_main_service);
                    ivGift.setImageResource(R.drawable.icon_main_gift);
                    llUser.setVisibility(View.GONE);
                    llGift.setVisibility(View.GONE);
                    rlMoney.setVisibility(View.VISIBLE);
                    initMoneyPage(playerId, serverId);
                    if (WebViewActivity.mActivity != null) {
                        WebViewActivity.mActivity.finish();
                        WebViewActivity.mActivity = null;
                    }
                }
            }
        });

        //是否绑定手机，实名认证以及绑定微信
        mIvBindPhone = findViewById(R.id.iv_isbindphone);
        mIvAuthen = findViewById(R.id.iv_isauthen);
        mIvBindWx = findViewById(R.id.iv_isbindwx);
        mTvMyMoney = findViewById(R.id.tv_mymoney);
        mNoActivity = findViewById(R.id.tv_noactivity);
        mLvMoneyType = mView.findViewById(R.id.listview_money);
        mTvNoRole = findViewById(R.id.tv_norole);

        getMyMoneyBalance();
        mRlBindWxNoti = findViewById(R.id.rl_bindwxnoti);
        mRlBindWxNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindWxDialog();
            }
        });
        mRlBindWx = findViewById(R.id.rl_bindwx);
        mRlBindWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindWxDialog();
            }
        });
        TextView getMoney = findViewById(R.id.tv_getmoney);
        getMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtils.isNotNullOrEmpty(Starter.loginBean.getUser().getOpenId())) {
                    ESToast.getInstance().ToastShow(mContext, "必须绑定微信才能提现哦!");
                    bindWxDialog();
                    return;
                }
                //提现
                getMoney();
            }
        });

        TextView tvMoneyDetail = findViewById(R.id.tv_moneydetail);
        //提现详细
        tvMoneyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final CashHistoryInfo cashHistory = EucService.getInstance(mContext).getCashHistory();
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (cashHistory != null && cashHistory.getLogList().size() > 0) {
                                        MoneyDetailLogDialog dialog = new MoneyDetailLogDialog(mContext, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, cashHistory.getLogList());
                                        dialog.show();
                                    } else {
                                        ESToast.getInstance().ToastShow(mContext, "暂无记录");
                                    }
                                }
                            });
                        } catch (Exception e) {
                            ESToast.getInstance().ToastThreadShow(mContext, "网络出错，请重试");
                        }
                    }
                });
            }
        });

        final View includeChangePw = findViewById(R.id.include_changepassword);
        final View includeBind = findViewById(R.id.include_bind);
        final View includeUserAuthen = findViewById(R.id.include_user_authen);

        rlChangePw = (RelativeLayout) includeMenu.findViewById(R.id.rl_changepassword);
        RelativeLayout rlBind = (RelativeLayout) includeMenu.findViewById(R.id.rl_bindphone);
        mBindPhoneType = (TextView) includeMenu.findViewById(R.id.tv_bindphone);
        final RelativeLayout rlAuthen = (RelativeLayout) includeMenu.findViewById(R.id.rl_authen);
        final RelativeLayout rlLogout = (RelativeLayout) includeMenu.findViewById(R.id.rl_logout);
        //修改密码
        final ImageView ivBack = (ImageView) includeChangePw.findViewById(R.id.iv_back);
        final EditText etNewPw = (EditText) includeChangePw.findViewById(R.id.et_newpw);
        final EditText etOldPw = (EditText) includeChangePw.findViewById(R.id.et_oldpw);
        final TextView changeSubmit = (TextView) includeChangePw.findViewById(R.id.tv_password_submit);
        //绑定手机
        final ImageView ivBindBack = (ImageView) includeBind.findViewById(R.id.iv_bind_back);
        final TextView mBindType = (TextView) includeBind.findViewById(R.id.tv_title_bind);

        phoneBindStateChange();
        authenStateChange();
        bindWXStateChange();

        final EditText etPhone = (EditText) includeBind.findViewById(R.id.et_bind_phone);
        final EditText etCode = (EditText) includeBind.findViewById(R.id.et_bind_code);
        final TextView tvGetCode = (TextView) includeBind.findViewById(R.id.tv_bind_getcode);
        final TextView tvBindSubmit = (TextView) includeBind.findViewById(R.id.tv_bind_submit);
        //实名认证
        final ImageView ivAuthenBack = (ImageView) includeUserAuthen.findViewById(R.id.iv_authen_back);
        final EditText etIdName = (EditText) includeUserAuthen.findViewById(R.id.et_user_inputname);
        final EditText etIdNum = (EditText) includeUserAuthen.findViewById(R.id.et_user_inputidnumber);
        final TextView authenSubmit = (TextView) includeUserAuthen.findViewById(R.id.tv_authen_submit);

        ImageView mCloseCode = (ImageView) findViewById(R.id.iv_closecode);
        mCloseCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llGiftCode.setVisibility(View.GONE);
            }
        });
        if (TextUtils.isEmpty(Starter.loginBean.getUser().getIdentityNum())) {
            rlAuthen.setVisibility(View.VISIBLE);
        } else {
            rlAuthen.setVisibility(View.GONE);
        }
        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                StartESUserPlugin.mUserCenterDialog = null;
                StartESAccountCenter.logout(mContext);
                Starter.mCallback.onLogout();
            }
        });
        rlAuthen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                includeMenu.setVisibility(View.GONE);
                includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_out));
                includeUserAuthen.setVisibility(View.VISIBLE);
                includeUserAuthen.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_in));
                ivAuthenBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(ivAuthenBack);
                        includeMenu.setVisibility(View.VISIBLE);
                        includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_in));
                        includeUserAuthen.setVisibility(View.GONE);
                        includeUserAuthen.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_out));
                    }
                });
                authenSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idName = etIdName.getText().toString();
                        final String idNum = etIdNum.getText().toString();
                        if (idName.isEmpty() || idNum.isEmpty()) {
                            ESToast.getInstance().ToastShow(mContext, "姓名和身份证号码不能为空");
                            return;
                        }
                        if (!CommonUtils.isIDNumber(idNum)) {
                            ESToast.getInstance().ToastShow(mContext, "请输入正确的身份证号吗");
                            return;
                        }
                        if (!CommonUtils.checkNameChinese(idName)) {
                            ESToast.getInstance().ToastShow(mContext, "请输入正确的名字");
                            return;
                        }
                        Tools.hideKeyboard(etIdName);
                        //开启实名认证
                        StartESAccountCenter.userCenterAuthen(new AuthenCallBack() {
                            @Override
                            public void loginSuccess(final String birthdate) {
                                int age = CommonUtils.getAge(idNum);
                                StartESAccountCenter.getPayLimitInfo(mContext, age, idNum, age > 18 ? "1" : "0");
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rlAuthen.setVisibility(View.GONE);
                                        Map<String, String> result = new HashMap<String, String>();
                                        result.put(ESConstant.SDK_IS_IDENTITY_USER, "false");
                                        result.put(ESConstant.SDK_USER_BIRTH_DATE, birthdate);
                                        Starter.mCallback.onUserCert(result);
                                        Starter.loginBean.getUser().setIdentityNum(idNum);
                                        ESToast.getInstance().ToastShow(mContext, "认证成功");
                                        ivAuthenBack.performClick();
                                    }
                                });
                            }

                            @Override
                            public void loginFail(final String msg) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, msg);
                                    }
                                });

                            }
                        }, idName, idNum, mContext);
                    }
                });
            }
        });

        rlBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGetCode.setEnabled(true);
                tvGetCode.setText("获取");
                etPhone.setText("");
                etCode.setText("");
                includeMenu.setVisibility(View.GONE);
                includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_out));
                includeBind.setVisibility(View.VISIBLE);
                includeBind.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_in));
                final CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvGetCode.setText(millisUntilFinished / 1000 + "");
                        tvGetCode.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        tvGetCode.setText("获取");
                        tvGetCode.setEnabled(true);
                    }
                };
                ivBindBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        includeMenu.setVisibility(View.VISIBLE);
                        includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_in));
                        includeBind.setVisibility(View.GONE);
                        includeBind.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_out));
                        Tools.hideKeyboard(ivBindBack);
                        if (timer != null) {
                            timer.cancel();
                        }

                    }
                });
                if (TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())) {
                    mBindType.setText("绑定手机");
                    bindType = true;
                } else {
                    bindType = false;
                    mBindType.setText("解绑手机");
                }
                tvGetCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = etPhone.getText().toString();
                        Tools.hideKeyboard(etPhone);
                        if (!phone.isEmpty()) {
                            //发送手机验证码
                            timer.start();
                            StartESAccountCenter.requestBindOrUnBind(new LoginCallBack() {
                                @Override
                                public void loginSuccess() {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ESToast.getInstance().ToastShow(mContext, "发送成功");
                                        }
                                    });
                                }

                                @Override
                                public void loginFail(final String msg) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (timer != null) {
                                                timer.cancel();
                                                timer.onFinish();
                                            }
                                            ESToast.getInstance().ToastShow(mContext, msg);
                                        }
                                    });
                                }
                            }, phone, bindType, mContext);
                        } else {
                            ESToast.getInstance().ToastShow(mContext, "请先输入手机号");
                        }
                    }
                });

                tvBindSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = etPhone.getText().toString();
                        String code = etCode.getText().toString();
                        if (phone.isEmpty() || code.isEmpty()) {
                            ESToast.getInstance().ToastShow(mContext, "手机号和验证码不能为空");
                            return;
                        }
                        Tools.hideKeyboard(etPhone);
                        //提交绑定或者解绑手机
                        StartESAccountCenter.applyBindOrUnBind(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, "操作成功");
                                        phoneBindStateChange();
                                        ivBindBack.performClick();
                                    }
                                });
                            }

                            @Override
                            public void loginFail(final String msg) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, msg);
                                    }
                                });
                            }
                        }, phone, code, bindType, mContext);
                    }
                });
            }
        });

        rlChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewPw.setText("");
                etOldPw.setText("");
                includeMenu.setVisibility(View.GONE);
                includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_out));
                includeChangePw.setVisibility(View.VISIBLE);
                includeChangePw.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_in));
                ivBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(ivBack);
                        includeMenu.setVisibility(View.VISIBLE);
                        includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_in));
                        includeChangePw.setVisibility(View.GONE);
                        includeChangePw.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_out));
                    }
                });

                changeSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPw = etNewPw.getText().toString();
                        String oldPw = etOldPw.getText().toString();
                        if (newPw.isEmpty() || oldPw.isEmpty()) {
                            ESToast.getInstance().ToastShow(mContext, "新密码和旧密码不能为空");
                            return;
                        }
                        Tools.hideKeyboard(etNewPw);
                        //修改密码提交
                        StartESAccountCenter.updatePassword(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, "'修改成功");
                                        includeMenu.setVisibility(View.VISIBLE);
                                        includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_in));
                                        includeChangePw.setVisibility(View.GONE);
                                        includeChangePw.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_out));
                                    }
                                });
                            }

                            @Override
                            public void loginFail(final String msg) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, msg);
                                    }
                                });
                            }
                        }, newPw, oldPw, mContext);
                    }
                });
            }
        });
    }

    private void resetTimer() {
        if (getTimer != null) {
            getTimer.cancel();
            getTimer.onFinish();
            getTimer = null;
        }
    }

    //获取红包界面数据
    private void initMoneyPage(final String playerId, final String serverId) {
        StartESAccountCenter.moneyBaseInfo(new MoneyDataCallBack<MoneyGroupAndRoleInfo>() {
            @Override
            public void success(final MoneyGroupAndRoleInfo moneyBaseInfo) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initMoney(moneyBaseInfo.getInfo(), playerId, serverId);
                        llMoney.setVisibility(View.VISIBLE);
                        mTvNoRole.setVisibility(View.GONE);
                        if (moneyBaseInfo.getGroupInfo() != null && moneyBaseInfo.getGroupInfo().size() > 0) {
                            initTab(moneyBaseInfo.getGroupInfo());
                        } else {
                            noActivity();
                        }
                    }
                });
            }

            @Override
            public void fail(String msg) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llMoney.setVisibility(View.GONE);
                        mTvNoRole.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, mContext, playerId, serverId);
    }

    private void noActivity() {
        mLvMoneyType.setVisibility(View.GONE);
        mNoActivity.setVisibility(View.VISIBLE);
    }

    private void initTab(final List<MoneyGroupInfo> groupInfo) {
        mLvMoneyType.setVisibility(View.VISIBLE);
        mNoActivity.setVisibility(View.GONE);
        if (lists == null) {
            lists = new HashMap<>();
        }
        lists.clear();
        TabLayout tabLayout = mView.findViewById(R.id.tablayout_moneytype);
        tabLayout.removeAllTabs();
        for (int i = 0; i < groupInfo.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(groupInfo.get(i).getGroupName()).setTag(groupInfo.get(i).getGroupId()));
        }
        setMoneyList(groupInfo.get(0).getGroupId());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setMoneyList((int) tab.getTag());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        final MoneyTypeAdapter moneyTypeAdapter = new MoneyTypeAdapter(mContext, groupInfo);
//        mVpMoneyType.setAdapter(moneyTypeAdapter);
//        mVpMoneyType.setOffscreenPageLimit(groupInfo.size());
//        tabLayout.setupWithViewPager(mVpMoneyType);
    }

    //初始化红包页面
    private void initMoney(final MoneyBaseInfo moneyBaseInfo, final String playerId, final String serverId) {
        TextView mTaskRule = mView.findViewById(R.id.tv_taskrule);
        TextView mName = mView.findViewById(R.id.tv_role_name);
        mName.setText(moneyBaseInfo.getPlayerName());
        TextView mRoleInfo = mView.findViewById(R.id.tv_role_zone);
        mRoleInfo.setText(moneyBaseInfo.getLvNcikName() + " " + moneyBaseInfo.getServerName());
        mTotalMoneyValue = mView.findViewById(R.id.tv_totalmoneyvalue);
        mTotalMoneyValue.setText(moneyBaseInfo.getTotalMoney() + "");
        final TextView mGetLeftTimeValue = mView.findViewById(R.id.tv_gettimevalue);
        //领取剩余时间倒计时
        resetTimer();
        getTimer = new CountDownTimer(moneyBaseInfo.getGainTimeRemaining(), 1000) {
            @Override
            public void onTick(long l) {
                mGetLeftTimeValue.setText(CommonUtils.formatTime(l / 1000));
            }

            @Override
            public void onFinish() {
                mGetLeftTimeValue.setText("00:00:00");
            }
        };
        getTimer.start();

        mTaskRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //红包领取规则
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        final RoleMoneyRule roleMoneyRule = EucService.getInstance(mContext).getRoleMoneyRule(playerId, serverId);
                        if (roleMoneyRule != null) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MoneyRuleDialog dialog = new MoneyRuleDialog(mContext, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, roleMoneyRule.getActivityRule());
                                    dialog.show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //初始化红包列表
    private void initMoneyList() {
        StartESAccountCenter.moneyListInfo(new MoneyDataCallBack<MoneyListInfo>() {
            @Override
            public void success(final MoneyListInfo moneyListInfo) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (moneyListInfo.getLuckyMoneyList() != null && moneyListInfo.getLuckyMoneyList().size() > 0) {
                            MoneyAdapter adapter = new MoneyAdapter(mContext, moneyListInfo.getLuckyMoneyList());
                            ListView listView = mView.findViewById(R.id.listview_money);
                            listView.setAdapter(adapter);
                            adapter.setTaskListener(new MoneyAdapter.TaskClickListener() {
                                @Override
                                public void onClick(int pos, int moneyNum) {

                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void fail(String msg) {

            }
        }, mContext, CommonUtils.getPlayerId(mContext), CommonUtils.getServerId(mContext));
    }

    //初始化礼包列表
    private void initGifts() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                final GiftBean userGift = UserAPI.getUserGift(mContext);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (userGift != null && userGift.getResultCode() == 1 && userGift.getRows() != null) {
                            if (userGift.getRows().size() > 0) {
                                initList(userGift.getRows());
                            }
                        } else {
                            if (userGift != null && !TextUtils.isEmpty(userGift.getMsg())) {
                                ESToast.getInstance().ToastShow(mContext, userGift.getMsg());
                            } else {
                                ESToast.getInstance().ToastShow(mContext, "暂无礼包信息!");
                            }
                        }
                    }
                });
            }
        });
    }

    private void initList(final List<GiftInfo> rows) {
        GiftAdapter adapter = new GiftAdapter(mContext, rows);
        giftList.setAdapter(adapter);
        adapter.setCodeListener(new GiftAdapter.GetCodeClickListener() {
            @Override
            public void onClick(int pos) {
                llGiftCode.setVisibility(View.VISIBLE);
                tvGiftCode.setText(rows.get(pos).getCode());
            }
        });
    }

    //手机绑定状态改变
    private void phoneBindStateChange() {
        if (TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())) {
            mBindPhoneType.setText("绑定手机");
            mIvBindPhone.setImageResource(R.drawable.ic_bindphone_normal);
        } else {
            mBindPhoneType.setText("解绑手机");
            mIvBindPhone.setImageResource(R.drawable.ic_bindphone_red);
        }
    }

    //实名认证状态改变
    private void authenStateChange() {
        if (Starter.loginBean.getUser().getIdentityNum().isEmpty()) {
            mIvAuthen.setImageResource(R.drawable.ic_authen_normal);
        } else {
            mIvAuthen.setImageResource(R.drawable.ic_authen_red);
        }
    }

    //微信绑定状态改变
    private void bindWXStateChange() {
        try {
            if (CommonUtils.isNotNullOrEmpty(Starter.loginBean.getUser().getOpenId())) {
                mRlBindWx.setVisibility(View.GONE);
                mRlBindWxNoti.setVisibility(View.GONE);
                mIvBindWx.setImageResource(R.drawable.ic_wx_red);
            } else {
                mRlBindWx.setVisibility(View.VISIBLE);
                mRlBindWxNoti.setVisibility(View.VISIBLE);
                mIvBindWx.setImageResource(R.drawable.ic_wx_normal);
            }
        } catch (Exception e) {
        }
    }

    //获取用户红包余额
    private void getMyMoneyBalance() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                final MoneyBalance moneyBalance = EucService.getInstance(mContext).getMoneyBalance();
                if (moneyBalance != null) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvMyMoney.setText(moneyBalance.getMoneyBalance() + "");
                        }
                    });
                }
            }
        });
    }

    //提现
    private void getMoney() {
        StartESAccountCenter.getCashInfo(new MoneyDataCallBack<CashLevelInfo>() {
            @Override
            public void success(final CashLevelInfo info) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GetCashDialog dialog = new GetCashDialog(mContext, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, info);
                        dialog.show();
                        dialog.setListener(new GetCashDialog.getCashSuccess() {
                            @Override
                            public void cashSuccess(int money) {
                                //money为提现申请成功的金额
                                String s = mTotalMoneyValue.getText().toString();
                                int tempMoney = Integer.valueOf(s);
                                mTotalMoneyValue.setText((tempMoney - money) + "");
                            }
                        });
                    }
                });
            }

            @Override
            public void fail(String msg) {

            }
        }, mContext);
    }

    //绑定微信弹窗
    private void bindWxDialog() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final EucApiResult<String> bindCode = AuthAPI.getBindCode(mContext, Constant.ESDK_USERID, RegisterAPI.getRequestInfo(mContext));
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bindCode.getResultCode().equals(CodeConstant.OK)) {
                                //弹出绑定微信弹窗
                                BindWxDialog dialog = new BindWxDialog(mContext, R.style.easou_dialog, Gravity.CENTER, 0.8f, 0, "", bindCode.getResult());
                                dialog.show();
                                dialog.setCloseListener(new BindWxDialog.DialogCloseListener() {
                                    @Override
                                    public void dialogClose() {
                                        bindWXStateChange();
                                    }
                                });
                            } else {
                                ESToast.getInstance().ToastShow(mContext, "网络出错了，请重试");
                            }
                        }
                    });
                } catch (Exception e) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ESToast.getInstance().ToastShow(mContext, "网络出错了，请重试");
                        }
                    });
                }
            }
        });
    }

    //设置每个分类红包下的数据
    private void setMoneyList(final int id) {
        if (lists.get(id) != null) {
            MoneyDetailAdapter adapter = new MoneyDetailAdapter(mContext, lists.get(id));
            mLvMoneyType.setAdapter(adapter);
            return;
        }
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                StartESAccountCenter.moneyListDetailInfo(new MoneyDataCallBack<MoneyList>() {
                    @Override
                    public void success(final MoneyList moneyListInfo) {
                        lists.put(id, moneyListInfo.getBonusList());
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (moneyListInfo.getBonusList().size() > 0) {
                                    MoneyDetailAdapter adapter = new MoneyDetailAdapter(mContext, moneyListInfo.getBonusList());
                                    mLvMoneyType.setAdapter(adapter);
                                }
                            }
                        });
                    }

                    @Override
                    public void fail(String msg) {

                    }
                }, mContext, CommonUtils.getPlayerId(mContext), CommonUtils.getServerId(mContext), id);
            }
        });
    }
}
