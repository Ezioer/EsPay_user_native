package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.AuthenCallBack;
import com.easou.androidsdk.login.service.LoginNameInfo;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.UserAPI;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;


import java.util.ArrayList;
import java.util.List;

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
    private TextView guestLogin;
    private Context mContext;
    //0为登录框，1为账号登录框
    private int currentType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_loginway, null);
        setContentView(mView);
        initView();
    }

    public void setGuestShow(int status) {
        if (guestLogin != null) {
            if (status == 0) {
                guestLogin.setVisibility(View.VISIBLE);
            } else {
                guestLogin.setVisibility(View.GONE);
            }
        }
    }

    private void initView() {
        final LinearLayout llRoot = (LinearLayout) mView.findViewById(R.id.ll_root);
        final LinearLayout llHelp = (LinearLayout) mView.findViewById(R.id.ll_help);

        TextView tvMainHelp = (TextView) mView.findViewById(R.id.tv_main_help);
        TextView tvMainService = (TextView) mView.findViewById(R.id.tv_main_user_service);

        tvMainHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        final String serviceUrl = UserAPI.getServiceUrl(mContext);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra("url", serviceUrl);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                });

            }
        });

        tvMainService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("url", Constant.user_service);
                mContext.startActivity(intent);
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
        guestLogin = (TextView) mView.findViewById(R.id.tv_guest);
        final TextView accountLogin = (TextView) mView.findViewById(R.id.tv_account_login);
        final TextView accountRegister = (TextView) mView.findViewById(R.id.tv_account_register);

        //找回密码
        final View includeLookPassword = mView.findViewById(R.id.include_lookpassword);
        final ImageView ivCloseLook = (ImageView) includeLookPassword.findViewById(R.id.iv_close_dialog);
        final EditText mEtPhone = (EditText) includeLookPassword.findViewById(R.id.et_phone);
        final EditText mEtCode = (EditText) includeLookPassword.findViewById(R.id.et_code);
        final EditText mEtNewPw = (EditText) includeLookPassword.findViewById(R.id.et_newpassword);
        final TextView mTvGetCode = (TextView) includeLookPassword.findViewById(R.id.tv_getcode);
        final TextView mChangeSubmit = (TextView) includeLookPassword.findViewById(R.id.tv_submit);


        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartESAccountCenter.handleAutoRegister(new LoginCallBack() {
                    @Override
                    public void loginSuccess() {
                        dismiss();
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
                }, mContext);
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
                ivExpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final List<LoginNameInfo> loginNameInfo = CommonUtils.getLoginNameInfo(mContext);
                        final List<String> nameList = new ArrayList<String>();
                        for (LoginNameInfo name : loginNameInfo) {
                            nameList.add(name.getName());
                        }
                        if (loginNameInfo.isEmpty()) {
                            return;
                        }
                        lpw.setAdapter(new ArrayAdapter(mContext,
                                R.layout.item_loginname, nameList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = LayoutInflater.from(mContext).inflate(R.layout.item_loginname, null);
                                TextView textView = (TextView) view.findViewById(R.id.tv_loginname);
                                textView.setText(nameList.get(position));
                                return view;
                            }
                        });
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
                        if (name.isEmpty() || password.isEmpty()) {
                            ESToast.getInstance().ToastShow(mContext, "账号和密码不能为空");
                            return;
                        }
                        Tools.hideKeyboard(mAccountLogin);
                        StartESAccountCenter.handleAccountLogin(new LoginCallBack() {

                            @Override
                            public void loginSuccess() {
                                dismiss();
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
                        }, name, password, mContext);
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
                        if (name.isEmpty() || password.isEmpty()) {
                            ESToast.getInstance().ToastShow(mContext, "账号和密码不能为空");
                            return;
                        }
                        Tools.hideKeyboard(mAccountLogin);
                        StartESAccountCenter.handleAccountRegister(new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editTextAccount.setText("");
                                        editTextPassword.setText("");
                                    }
                                });
                                dismiss();

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
                        }, name, password, mContext);
                    }
                });
            }
        });

        lookPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llHelp.setVisibility(View.GONE);
                mTvGetCode.setEnabled(true);
                mTvGetCode.setText("获取");
                mEtPhone.setText("");
                mEtCode.setText("");
                mEtNewPw.setText("");
                if (currentType == 0) {
                    llRoot.setVisibility(View.GONE);
                    llRoot.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    includeLookPassword.setVisibility(View.VISIBLE);
                } else {
                    includeAccountLogin.setVisibility(View.GONE);
                    includeAccountLogin.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    includeLookPassword.setVisibility(View.VISIBLE);
                }
                includeLookPassword.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));

                final CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mTvGetCode.setText(millisUntilFinished / 1000 + "");
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
                        if (!phone.isEmpty()) {
                            timer.start();
                            StartESAccountCenter.requestResetPassword(new LoginCallBack() {
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
                            }, phone, mContext);
                        } else {
                            ESToast.getInstance().ToastShow(mContext, "请先输入手机号");
                        }
                    }
                });

                ivCloseLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.hideKeyboard(ivCloseLook);
                        llHelp.setVisibility(View.VISIBLE);
                        timer.cancel();
                        if (currentType == 0) {
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
                        final String newPw = mEtNewPw.getText().toString();
                        if (phone.isEmpty() || code.isEmpty() || newPw.isEmpty()) {
                            ESToast.getInstance().ToastShow(mContext, "您有信息未填完哦");
                            return;
                        }
                        Tools.hideKeyboard(mChangeSubmit);
                        StartESAccountCenter.lookPassword(new AuthenCallBack() {
                            @Override
                            public void loginSuccess(final String name) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, "修改成功");
                                        llHelp.setVisibility(View.VISIBLE);
                                        timer.cancel();
                                        accountLogin.performClick();
                                        editTextAccount.setText(name);
                                        editTextPassword.setText(newPw);
                                        llRoot.setVisibility(View.GONE);
                                        includeAccountLogin.setVisibility(View.VISIBLE);
                                        includeLookPassword.setVisibility(View.GONE);
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
                        }, phone, newPw, code, mContext);
                    }
                });
            }
        });
    }
}
