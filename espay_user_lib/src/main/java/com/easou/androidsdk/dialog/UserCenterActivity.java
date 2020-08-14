package com.easou.androidsdk.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.login.AuthenCallBack;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.UserAPI;
import com.easou.androidsdk.login.service.GiftBean;
import com.easou.androidsdk.login.service.GiftInfo;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.androidsdk.webviewutils.ConfigerManagner;
import com.easou.androidsdk.webviewutils.ImageUtil;
import com.easou.androidsdk.webviewutils.JSAndroid;
import com.easou.androidsdk.webviewutils.PermissionUtil;
import com.easou.androidsdk.webviewutils.ReWebChomeClient;
import com.easou.androidsdk.webviewutils.ReWebViewClient;
import com.easou.espay_user_lib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by xiaoqing.zhou
 * on 2020/8/12
 * fun
 */
public class UserCenterActivity extends Activity implements ReWebChomeClient.OpenFileChooserCallBack {
    /**
     * 网页加载提示
     */
    private ProgressDialog progressDialog = null;
    /**
     * 需要包装网页的控件
     */
    private WebView mWebView;
    /**
     * 使用的浏览器对象
     */
    private WebChromeClient mWebChromeClient;
    /**
     * js跳转控制
     */
    private WebViewClient mWebViewClient;

    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private Intent mSourceIntent;
    public ValueCallback<Uri[]> mUploadMsgForAndroid5;
    private ValueCallback<Uri> mUploadMsg;
    private static final int P_CODE_PERMISSIONS = 101;


    public static UserCenterActivity mContext;
    private boolean bindType = true;
    private int currentType = 0;
    private String helpUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(getApplication().getResources().getIdentifier("layout_usercenter", "layout",
                getApplication().getPackageName()));
        mContext = this;
        initView();
    }

    private void initView() {
        mWebView = (WebView) findViewById(getApplication().getResources().getIdentifier("webview", "id",
                getApplication().getPackageName()));
        final ImageView ivMe = (ImageView) findViewById(R.id.iv_me);
        initWebView(ivMe);
        initOtherView(ivMe);
    }

    private void initOtherView(final ImageView ivMe) {
        final View includeMenu = findViewById(R.id.include_usermenu);

        final ImageView ivGift = (ImageView) findViewById(R.id.iv_gift);
        //首页
        final TextView tvWelcome = (TextView) includeMenu.findViewById(R.id.tv_username);
        final RelativeLayout llGift = (RelativeLayout) findViewById(R.id.ll_gift);
        final RelativeLayout llGiftCode = (RelativeLayout) findViewById(R.id.ll_giftcode);
        final TextView tvGiftCode = (TextView) findViewById(R.id.tv_giftcode);
        final ListView giftList = (ListView) findViewById(R.id.listview);
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
                    llUser.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    llGift.setVisibility(View.GONE);
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
                    llUser.setVisibility(View.GONE);
                    mWebView.setVisibility(View.GONE);
                    llGift.setVisibility(View.VISIBLE);
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            final GiftBean userGift = UserAPI.getUserGift(mContext);
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (userGift != null && userGift.getResultCode() == 1) {
                                        initList(userGift.getRows());
                                    } else {
                                        ESToast.getInstance().ToastShow(mContext, userGift.getMsg());
                                    }
                                }
                            });
                        }
                    });
                }
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
        });

        ivService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType != 1) {
                    currentType = 1;
                    ivMe.setImageResource(R.drawable.icon_main_me);
                    ivService.setImageResource(R.drawable.icon_main_servicehign);
                    ivGift.setImageResource(R.drawable.icon_main_gift);
                    llUser.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                    llGift.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(helpUrl)) {
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                final String serviceUrl = UserAPI.getServiceUrl(mContext);
                                helpUrl = serviceUrl;
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mWebView.loadUrl(serviceUrl);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });


        final View includeChangePw = findViewById(R.id.include_changepassword);
        final View includeBind = findViewById(R.id.include_bind);
        final View includeUserAuthen = findViewById(R.id.include_user_authen);

        RelativeLayout rlChangePw = (RelativeLayout) includeMenu.findViewById(R.id.rl_changepassword);
        RelativeLayout rlBind = (RelativeLayout) includeMenu.findViewById(R.id.rl_bindphone);
        final TextView mBindPhoneType = (TextView) includeMenu.findViewById(R.id.tv_bindphone);
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
                finish();
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
                                        if (timer != null) {
                                            timer.cancel();
                                        }
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

    private void initWebView(final ImageView ivMe) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);// webview必须设置支持Javascript
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// WebView启用Javascript脚本执行
        mWebView.setVerticalScrollBarEnabled(true);// 取消VerticalScrollBar显示
        mWebView.getSettings().setDomStorageEnabled(true);// 设置html5离线缓存可用

        mWebView.addJavascriptInterface(new Object() {
            private ConfigerManagner configerManagner;

            @JavascriptInterface
            public void openAndroid(String msg) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivMe.performClick();
                    }
                });
            }

            @JavascriptInterface
            public void writeData(String msg) {
                configerManagner = ConfigerManagner.getInstance(mContext);
                configerManagner.setString("js", msg);
            }

            @JavascriptInterface
            public String giveInformation(String msg) {
                configerManagner = ConfigerManagner.getInstance(mContext);
                String msg1 = configerManagner.getString("js");
                return msg1;
            }
        }, "Android");

        fixDirPath();
        mWebView.setWebViewClient(new ReWebViewClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebChromeClient = new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                AlertDialog.Builder b2 = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("温馨提示").setMessage(message)
                        .setPositiveButton("确 定", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });

                b2.setCancelable(false);
                b2.create();
                b2.show();

                return true;
            }
        };

        mWebView.requestFocus();
        mWebView.requestFocusFromTouch();

        mWebViewClient = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideDialog();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showDialog();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                ViewParent webParentView = (ViewParent) mWebView.getParent();
                ((ViewGroup) webParentView).removeAllViews();
                showAlert();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }


        };

        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new ReWebChomeClient(this));
    }

    protected void hideBottomUIMenu() {
        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        _window.setAttributes(params);
    }

    public void clearData() {

        mWebView.clearFormData();
        mWebView.clearHistory();
    }

    private void showAlert() {

        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
        exitDialog.setTitle("温馨提示")
                .setMessage("网络连接错误，请检查网络后重启游戏！")
                .setPositiveButton("确 定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        mContext.finish();
                        System.exit(0);
                    }
                });
        exitDialog.setCancelable(false);
        // 显示
        exitDialog.show();
    }

    private void showDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
            }
            progressDialog.setMessage("数据加载中，请稍候...");
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
            }

            if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
                mUploadMsgForAndroid5.onReceiveValue(null);
            }
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
            case REQUEST_CODE_PICK_IMAGE: {
                try {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsg == null) {
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {

                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsg.onReceiveValue(uri);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {

                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public void showOptions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new DialogOnCancelListener());
        alertDialog.setTitle("请选择操作");
        String[] options = {"相册", "拍照"};
        alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (PermissionUtil.isOverMarshmallow()) {
                                if (!PermissionUtil.isPermissionValid(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    Toast.makeText(mContext,
                                            "请去\"设置\"中开启本应用的图片媒体访问权限",
                                            Toast.LENGTH_SHORT).show();

                                    restoreUploadMsg();
                                    requestPermissionsAndroidM();
                                    return;
                                }

                            }

                            try {
                                mSourceIntent = ImageUtil.choosePicture();
                                startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mContext,
                                        "请去\"设置\"中开启本应用的图片媒体访问权限",
                                        Toast.LENGTH_SHORT).show();
                                restoreUploadMsg();
                            }

                        } else {
                            if (PermissionUtil.isOverMarshmallow()) {
                                if (!PermissionUtil.isPermissionValid(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    Toast.makeText(mContext,
                                            "请去\"设置\"中开启本应用的图片媒体访问权限",
                                            Toast.LENGTH_SHORT).show();

                                    restoreUploadMsg();
                                    requestPermissionsAndroidM();
                                    return;
                                }

                                if (!PermissionUtil.isPermissionValid(mContext, Manifest.permission.CAMERA)) {
                                    Toast.makeText(mContext,
                                            "请去\"设置\"中开启本应用的相机权限",
                                            Toast.LENGTH_SHORT).show();

                                    restoreUploadMsg();
                                    requestPermissionsAndroidM();
                                    return;
                                }
                            }

                            try {
                                mSourceIntent = ImageUtil.takeBigPicture();
                                startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mContext,
                                        "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
                                        Toast.LENGTH_SHORT).show();

                                restoreUploadMsg();
                            }
                        }
                    }
                }
        ).show();
    }

    private void fixDirPath() {
        String path = ImageUtil.getDirPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMsg = uploadMsg;

        showOptions();
    }


    @Override
    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadMsgForAndroid5 = filePathCallback;

        showOptions();
        return true;
    }


    private class DialogOnCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            restoreUploadMsg();
        }
    }

    private void restoreUploadMsg() {
        if (mUploadMsg != null) {
            mUploadMsg.onReceiveValue(null);
            mUploadMsg = null;

        } else if (mUploadMsgForAndroid5 != null) {
            mUploadMsgForAndroid5.onReceiveValue(null);
            mUploadMsgForAndroid5 = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case P_CODE_PERMISSIONS:
                requestResult(permissions, grantResults);
                restoreUploadMsg();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestPermissionsAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermissionList = new ArrayList<>();
            needPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.CAMERA);

            PermissionUtil.requestPermissions(mContext, P_CODE_PERMISSIONS, needPermissionList);

        } else {
            return;
        }
    }

    public void requestResult(String[] permissions, int[] grantResults) {
        ArrayList<String> needPermissions = new ArrayList<String>();

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtil.isOverMarshmallow()) {

                    needPermissions.add(permissions[i]);
                }
            }
        }

        if (needPermissions.size() > 0) {
            StringBuilder permissionsMsg = new StringBuilder();

            for (int i = 0; i < needPermissions.size(); i++) {
                String strPermissons = needPermissions.get(i);

                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(strPermissons)) {
                    permissionsMsg.append("," + getString(R.string.permission_storage));

                } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(strPermissons)) {
                    permissionsMsg.append("," + getString(R.string.permission_storage));

                } else if (Manifest.permission.CAMERA.equals(strPermissons)) {
                    permissionsMsg.append("," + getString(R.string.permission_camera));

                }
            }

            String strMessage = "请允许使用\"" + permissionsMsg.substring(1).toString() + "\"权限, 以正常使用APP的所有功能.";

            Toast.makeText(mContext, strMessage, Toast.LENGTH_SHORT).show();

        } else {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        StartESUserPlugin.showFloatView();
        StartESUserPlugin.isShowUser = true;
    }
}
