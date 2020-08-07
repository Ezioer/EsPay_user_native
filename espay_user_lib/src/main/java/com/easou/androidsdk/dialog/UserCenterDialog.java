package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bun.miitmdid.core.Utils;
import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.UserAPI;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartOtherPlugin;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class UserCenterDialog extends BaseDialog {

    public UserCenterDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
    }

    private View mView;
    private Context mContext;
    private boolean bindType = true;
    private int currentType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_usercenter, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        final View includeMenu = mView.findViewById(R.id.include_usermenu);
        final ImageView ivMe = (ImageView) mView.findViewById(R.id.iv_me);
        final LinearLayout llContent = (LinearLayout) mView.findViewById(R.id.ll_content);
        final WebView webView = (WebView) mView.findViewById(R.id.webview);
        final ImageView ivService = (ImageView) mView.findViewById(R.id.iv_service);
        ivMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 0){
                    currentType = 0;
                    ivMe.setImageResource(R.drawable.icon_main_mehign);
                    ivService.setImageResource(R.drawable.icon_main_service);
                    llContent.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
            }
        });

        ivService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 1){
                    currentType = 1;
                    ivMe.setImageResource(R.drawable.icon_main_me);
                    ivService.setImageResource(R.drawable.icon_main_servicehign);
                    llContent.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });


        final View includeChangePw = mView.findViewById(R.id.include_changepassword);
        final View includeBind = mView.findViewById(R.id.include_bind);
        final View includeUserAuthen = mView.findViewById(R.id.include_user_authen);
        //首页
        TextView tvWelcome = (TextView) includeMenu.findViewById(R.id.tv_username);
        tvWelcome.setText("您好："+Starter.loginBean.getUser().getName());
        RelativeLayout rlChangePw = (RelativeLayout) includeMenu.findViewById(R.id.rl_changepassword);
        RelativeLayout rlBind = (RelativeLayout) includeMenu.findViewById(R.id.rl_bindphone);
        RelativeLayout rlAuthen = (RelativeLayout) includeMenu.findViewById(R.id.rl_authen);
        final RelativeLayout rlLogout = (RelativeLayout) includeMenu.findViewById(R.id.rl_logout);
        //修改密码
        final ImageView ivBack = (ImageView) includeChangePw.findViewById(R.id.iv_back);
        final EditText etNewPw = (EditText) includeChangePw.findViewById(R.id.et_newpw);
        final EditText etOldPw = (EditText) includeChangePw.findViewById(R.id.et_oldpw);
        final TextView changeSubmit = (TextView) includeChangePw.findViewById(R.id.tv_password_submit);
        //绑定手机
        final ImageView ivBindBack = (ImageView) includeBind.findViewById(R.id.iv_bind_back);
        final TextView mBindType = (TextView) includeBind.findViewById(R.id.tv_title_bind);
        final EditText etPhone = (EditText) includeBind.findViewById(R.id.et_bind_phone);
        final EditText etCode = (EditText) includeBind.findViewById(R.id.et_bind_code);
        final TextView tvGetCode = (TextView) includeBind.findViewById(R.id.tv_bind_getcode);
        final TextView tvBindSubmit = (TextView) includeBind.findViewById(R.id.tv_bind_submit);
        //实名认证
        final ImageView ivAuthenBack = (ImageView) includeUserAuthen.findViewById(R.id.iv_authen_back);
        final EditText etIdName = (EditText) includeUserAuthen.findViewById(R.id.et_user_inputname);
        final EditText etIdNum = (EditText) includeUserAuthen.findViewById(R.id.et_user_inputidnumber);
        final TextView authenSubmit = (TextView) includeUserAuthen.findViewById(R.id.tv_authen_submit);

        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                StartESUserPlugin.hideFloatView();
                CommonUtils.saveLoginInfo("",mContext);
                StartESUserPlugin.showLoginDialog();
                StartOtherPlugin.logTTActionLogin("");
                StartOtherPlugin.logGismActionLogout();
                StartOtherPlugin.logGDTActionSetID("");
                StartOtherPlugin.logoutAqyAction();
                Constant.ESDK_USERID = "";
                Constant.ESDK_TOKEN = "";
                Constant.IS_LOGINED = false;
                Starter.mCallback.onLogout();
            }
        });
        rlAuthen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                includeMenu.setVisibility(View.GONE);
                includeUserAuthen.setVisibility(View.VISIBLE);
                ivAuthenBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(ivAuthenBack);
                        includeMenu.setVisibility(View.VISIBLE);
                        includeUserAuthen.setVisibility(View.GONE);
                    }
                });
                authenSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idName = etIdName.getText().toString();
                        String idNum = etIdNum.getText().toString();
                        if (idName.isEmpty() || idNum.isEmpty()){
                            return;
                        }
                        Tools.hideKeyboard(etIdName);
                        //开启实名认证
                        StartESAccountCenter.userCenterAuthen(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,"认证成功");
                                        ivAuthenBack.performClick();
                                    }
                                });
                            }

                            @Override
                            public void loginFail(final String msg) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,msg);
                                    }
                                });

                            }
                        },idName, idNum, mContext);
                    }
                });
            }
        });

        rlBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPhone.setText("");
                etCode.setText("");
                includeMenu.setVisibility(View.GONE);
                includeBind.setVisibility(View.VISIBLE);
                final CountDownTimer timer = new CountDownTimer(60*1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvGetCode.setText(millisUntilFinished/1000+"");
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
                        includeBind.setVisibility(View.GONE);
                        Tools.hideKeyboard(ivBindBack);
                        if (timer != null){
                            timer.cancel();
                        }
                    }
                });
                if (TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())){
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
                        if (!phone.isEmpty()){
                            //发送手机验证码
                            timer.start();
                            StartESAccountCenter.requestBindOrUnBind(new LoginCallBack() {
                                @Override
                                public void loginSuccess() {
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ESToast.getInstance().ToastShow(mContext,"发送成功");
                                        }
                                    });
                                }

                                @Override
                                public void loginFail(final String msg) {
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ESToast.getInstance().ToastShow(mContext,msg);
                                        }
                                    });
                                }
                            },phone,bindType, mContext);
                        }
                    }
                });

                tvBindSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = etPhone.getText().toString();
                        String code = etCode.getText().toString();
                        if (phone.isEmpty() || code.isEmpty()){
                            return;
                        }
                        if (timer != null){
                            timer.cancel();
                        }
                        Tools.hideKeyboard(etPhone);
                        //提交绑定或者解绑手机
                        StartESAccountCenter.applyBindOrUnBind(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,"操作成功");
                                        ivBindBack.performClick();
                                    }
                                });
                            }

                            @Override
                            public void loginFail(final String msg) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,msg);
                                    }
                                });
                            }
                        },phone,code,bindType,mContext);
                    }
                });
            }
        });

        rlChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                includeMenu.setVisibility(View.GONE);
                includeChangePw.setVisibility(View.VISIBLE);
                ivBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(ivBack);
                        includeMenu.setVisibility(View.VISIBLE);
                        includeChangePw.setVisibility(View.GONE);
                    }
                });

                changeSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPw = etNewPw.getText().toString();
                        String oldPw = etOldPw.getText().toString();
                        if (newPw.isEmpty() || oldPw.isEmpty()){
                            return;
                        }
                        Tools.hideKeyboard(etNewPw);
                        //修改密码提交
                        StartESAccountCenter.updatePassword(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,"'修改成功");
                                        rlLogout.performClick();
                                    }
                                });
                            }

                            @Override
                            public void loginFail(final String msg) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,msg);
                                    }
                                });
                            }
                        },newPw,oldPw,mContext);
                    }
                });
            }
        });
    }
}
