package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bun.miitmdid.core.Utils;
import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.AuthenCallBack;
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
import com.easou.androidsdk.webviewutils.JSAndroid;
import com.easou.androidsdk.webviewutils.ReWebChomeClient;
import com.easou.espay_user_lib.R;

import java.util.HashMap;
import java.util.Map;

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
    private String helpUrl = "";

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
        //首页
        final TextView tvWelcome = (TextView) includeMenu.findViewById(R.id.tv_username);
        final RelativeLayout llContent = (RelativeLayout) mView.findViewById(R.id.ll_content);
        final LinearLayout llUser = (LinearLayout) mView.findViewById(R.id.ll_user);
        tvWelcome.setText("您好：" + Starter.loginBean.getUser().getName());
        final WebView webView = (WebView) mView.findViewById(R.id.webview);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setSupportZoom(true);
//        webView.setInitialScale(30);
//        webView.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// WebView启用Javascript脚本执行
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        webView.getSettings().setBlockNetworkImage(false);//解决图片不显示
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.addJavascriptInterface(new JSAndroid(mContext), "Android");
        webView.setWebChromeClient(new ReWebChomeClient(new ReWebChomeClient.OpenFileChooserCallBack() {
            @Override
            public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
            }

            @Override
            public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                return true;
            }
        }));
        final ImageView ivService = (ImageView) mView.findViewById(R.id.iv_service);
        ivMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 0) {
                    currentType = 0;
                    tvWelcome.setText("您好：" + Starter.loginBean.getUser().getName());
                    ivMe.setImageResource(R.drawable.icon_main_mehign);
                    ivService.setImageResource(R.drawable.icon_main_service);
                    llUser.setVisibility(View.VISIBLE);
                    llUser.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                    webView.setVisibility(View.GONE);
                    webView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                }
            }
        });

        ivService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 1) {
                    currentType = 1;
                    ivMe.setImageResource(R.drawable.icon_main_me);
                    ivService.setImageResource(R.drawable.icon_main_servicehign);
                    llUser.setVisibility(View.GONE);
                    llUser.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    webView.setVisibility(View.VISIBLE);
                    webView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                    if (TextUtils.isEmpty(helpUrl)) {
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                final String serviceUrl = UserAPI.getServiceUrl(mContext);
                                helpUrl = serviceUrl;
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.setWebViewClient(new WebViewClient() {
                                            @Override
                                            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                                return super.shouldOverrideUrlLoading(view, serviceUrl);
                                            }

                                            @Override
                                            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                                handler.proceed();
                                            }
                                        });
                                        webView.loadUrl(serviceUrl);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });


        final View includeChangePw = mView.findViewById(R.id.include_changepassword);
        final View includeBind = mView.findViewById(R.id.include_bind);
        final View includeUserAuthen = mView.findViewById(R.id.include_user_authen);

        RelativeLayout rlChangePw = (RelativeLayout) includeMenu.findViewById(R.id.rl_changepassword);
        RelativeLayout rlBind = (RelativeLayout) includeMenu.findViewById(R.id.rl_bindphone);
        final TextView mBindPhoneType = (TextView) includeMenu.findViewById(R.id.tv_bindphone);
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
        if (TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())) {
            mBindPhoneType.setText("绑定手机");
        } else {
            mBindPhoneType.setText("解绑手机");
        }
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
                        if (idName.isEmpty() || idNum.isEmpty()){
                            ESToast.getInstance().ToastShow(mContext, "姓名和身份证号码不能为空");
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
                                        Map<String, String> result = new HashMap<String, String>();
                                        result.put(ESConstant.SDK_IS_IDENTITY_USER, "false");
                                        result.put(ESConstant.SDK_USER_BIRTH_DATE, birthdate);
                                        Starter.mCallback.onUserCert(result);
                                        ESToast.getInstance().ToastShow(mContext, "认证成功");
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
                includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_out));
                includeBind.setVisibility(View.VISIBLE);
                includeBind.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_in));
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
                        includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_in));
                        includeBind.setVisibility(View.GONE);
                        includeBind.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_out));
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
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
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
                        if (phone.isEmpty() || code.isEmpty()){
                            ESToast.getInstance().ToastShow(mContext, "手机号和验证码不能为空");
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
                                        ESToast.getInstance().ToastShow(mContext, "操作成功");
                                        if (TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())) {
                                            mBindPhoneType.setText("绑定手机");
                                        } else {
                                            mBindPhoneType.setText("解绑手机");
                                        }
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
                        if (newPw.isEmpty() || oldPw.isEmpty()){
                            ESToast.getInstance().ToastShow(mContext, "新密码和旧密码不能为空");
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
                                        ESToast.getInstance().ToastShow(mContext, "'修改成功");
                                        includeMenu.setVisibility(View.VISIBLE);
                                        includeMenu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_left_in));
                                        includeChangePw.setVisibility(View.GONE);
                                        includeChangePw.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.guide_right_out));
//                                        rlLogout.performClick();
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
