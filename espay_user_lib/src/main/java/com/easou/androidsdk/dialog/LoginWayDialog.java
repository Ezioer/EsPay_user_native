package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.ApiType;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.data.FeeType;
import com.easou.androidsdk.data.LoginNameInfo;
import com.easou.androidsdk.http.ApiAsyncImp;
import com.easou.androidsdk.http.HttpAsyncTaskImp;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class LoginWayDialog extends BaseDialog {

    public LoginWayDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
    }

    private View mView;
    private Context mContext;
    //0为登录框，1为账号登录框
    private int currentType =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_loginway, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        final LinearLayout llRoot = (LinearLayout) mView.findViewById(R.id.ll_root);
        final LinearLayout llHelp = (LinearLayout) mView.findViewById(R.id.ll_help);

        TextView tvHelp = (TextView) mView.findViewById(R.id.tv_help);
        TextView tvService = (TextView) mView.findViewById(R.id.tv_user_service);
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartESUserPlugin.showWebViewDialaog(Constant.user_service);
            }
        });

        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartESUserPlugin.showWebViewDialaog(Constant.user_service);
            }
        });
        //账号登录或注册
        final View includeAccountLogin = mView.findViewById(R.id.include_accountlogin);
        final EditText editTextAccount = (EditText) includeAccountLogin.findViewById(R.id.et_login_account);
        final ImageView ivExpan = (ImageView) includeAccountLogin.findViewById(R.id.iv_expan);
        final EditText editTextPassword = (EditText) includeAccountLogin.findViewById(R.id.et_login_password);
        final TextView mAccountBack = (TextView) includeAccountLogin.findViewById(R.id.tv__login_back);
        final TextView mAccountLogin = (TextView) includeAccountLogin.findViewById(R.id.tv_login);
        final TextView accountType = (TextView) includeAccountLogin.findViewById(R.id.tv_account_type);


        final TextView lookPassword = (TextView) mView.findViewById(R.id.tv_look_password_loginway);
        TextView guestLogin = (TextView) mView.findViewById(R.id.tv_guest);
        final TextView accountLogin = (TextView) mView.findViewById(R.id.tv_account_login);
        final TextView accountRegister = (TextView) mView.findViewById(R.id.tv_account_register);

        //找回密码
        final View  includeLookPassword = mView.findViewById(R.id.include_lookpassword);
        final ImageView ivCloseLook = (ImageView) includeLookPassword.findViewById(R.id.iv_close_dialog);
        final EditText mEtPhone = (EditText) includeLookPassword.findViewById(R.id.et_phone);
        final EditText mEtCode = (EditText) includeLookPassword.findViewById(R.id.et_code);
        final EditText mEtNewPw = (EditText) includeLookPassword.findViewById(R.id.et_newpassword);
        final TextView mTvGetCode = (TextView) includeLookPassword.findViewById(R.id.tv_getcode);
        final TextView mChangeSubmit = (TextView) includeLookPassword.findViewById(R.id.tv_submit);

        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartESAccountCenter.handleAutoRegister(new LoginCallBack(){
                    @Override
                    public void loginSuccess() {
                        dismiss();
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
                },mContext);
            }
        });

        accountLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llRoot.setVisibility(View.GONE);
                llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                ivExpan.setVisibility(View.VISIBLE);
                editTextAccount.setText("");
                editTextPassword.setText("");
                accountType.setText(R.string.account_login);
                currentType = 1;
                includeAccountLogin.setVisibility(View.VISIBLE);
                includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                mAccountBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(mAccountBack);
                        llRoot.setVisibility(View.VISIBLE);
                        llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        currentType = 0;
                        includeAccountLogin.setVisibility(View.GONE);
                        includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    }
                });
                final ListPopupWindow lpw = new ListPopupWindow(mContext);
                final List<LoginNameInfo> loginNameInfo = CommonUtils.getLoginNameInfo(mContext);
                ivExpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lpw.setAdapter(new ArrayAdapter(mContext,
                                android.R.layout.simple_list_item_1, loginNameInfo));
                        lpw.setAnchorView(editTextAccount);
                        lpw.setModal(true);
                        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                LoginNameInfo item = loginNameInfo.get(position);
                                editTextAccount.setText(item.getName());
                                editTextPassword.setText(item.getPassword());
                                lpw.dismiss();
                            }
                        });
                        lpw.show();
                    }
                });
                mAccountLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editTextAccount.getText().toString();
                        String password = editTextPassword.getText().toString();
                        if (name.isEmpty() || password.isEmpty()){
                            return;
                        }
                        Tools.hideKeyboard(mAccountLogin);
                        StartESAccountCenter.handleAccountLogin(new LoginCallBack(){

                            @Override
                            public void loginSuccess() {
                                dismiss();
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
                        },name,password,mContext);
                    }
                });
            }
        });

        accountRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llRoot.setVisibility(View.GONE);
                llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                ivExpan.setVisibility(View.GONE);
                accountType.setText(R.string.account_register);
                editTextAccount.setText("");
                editTextPassword.setText("");
                includeAccountLogin.setVisibility(View.VISIBLE);
                includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                lookPassword.setVisibility(View.GONE);
                mAccountLogin.setText(R.string.register);
                mAccountBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(mAccountBack);
                        llRoot.setVisibility(View.VISIBLE);
                        llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        lookPassword.setVisibility(View.VISIBLE);
                        includeAccountLogin.setVisibility(View.GONE);
                        includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        mAccountLogin.setText(R.string.login);
                    }
                });
                mAccountLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editTextAccount.getText().toString();
                        String password = editTextPassword.getText().toString();
                        if (name.isEmpty() || password.isEmpty()){
                            return;
                        }
                        Tools.hideKeyboard(mAccountLogin);
                        StartESAccountCenter.handleAccountRegister(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                dismiss();
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
                        },name, password, mContext);
                    }
                });
            }
        });

        lookPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llHelp.setVisibility(View.GONE);
                if (currentType == 0){
                    llRoot.setVisibility(View.GONE);
                    llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    includeLookPassword.setVisibility(View.VISIBLE);
                } else {
                    includeAccountLogin.setVisibility(View.GONE);
                    includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    includeLookPassword.setVisibility(View.VISIBLE);
                }
                includeLookPassword.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));

                final CountDownTimer timer =  new CountDownTimer(60*1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mTvGetCode.setText(millisUntilFinished/1000+"");
                        mTvGetCode.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        mTvGetCode.setText("获取");
                        mTvGetCode.setEnabled(true);
                    }
                };

                mTvGetCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = mEtPhone.getText().toString();
                        Tools.hideKeyboard(mTvGetCode);
                        if (!phone.isEmpty()){
                            timer.start();
                            StartESAccountCenter.requestResetPassword(new LoginCallBack() {
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
                            },phone, mContext);
                        }
                    }
                });

                ivCloseLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(ivCloseLook);
                        llHelp.setVisibility(View.VISIBLE);
                        timer.cancel();
                        if (currentType == 0){
                            llRoot.setVisibility(View.VISIBLE);
                            llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                            includeLookPassword.setVisibility(View.GONE);
                        } else {
                            includeAccountLogin.setVisibility(View.VISIBLE);
                            includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                            includeLookPassword.setVisibility(View.GONE);
                        }
                        includeLookPassword.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    }
                });

                mChangeSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = mEtPhone.getText().toString();
                        String code = mEtCode.getText().toString();
                        String newPw = mEtNewPw.getText().toString();
                        if (phone.isEmpty() || code.isEmpty() || newPw.isEmpty()){
                            return;
                        }
                        timer.cancel();
                        Tools.hideKeyboard(mChangeSubmit);
                        StartESAccountCenter.lookPassword(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext,"修改成功");
                                        llHelp.setVisibility(View.VISIBLE);
                                        if (currentType == 0){
                                            llRoot.setVisibility(View.VISIBLE);
                                            includeLookPassword.setVisibility(View.GONE);
                                        } else {
                                            includeAccountLogin.setVisibility(View.VISIBLE);
                                            includeLookPassword.setVisibility(View.GONE);
                                        }
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
                        },phone,newPw,code,mContext);
                    }
                });


            }
        });
    }
}
