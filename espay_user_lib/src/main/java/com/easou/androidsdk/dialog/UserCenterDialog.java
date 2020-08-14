package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.easou.espay_user_lib.R;

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
        final View includeMenu = findViewById(R.id.include_usermenu);
        final ImageView ivMe = (ImageView) findViewById(R.id.iv_me);
        final ImageView ivGift = (ImageView) findViewById(R.id.iv_gift);
        final RelativeLayout llContent = (RelativeLayout) findViewById(R.id.ll_content);
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
                    llGift.setVisibility(View.GONE);
                    if (WebViewActivity.mActivity != null) {
                        WebViewActivity.mActivity.finish();
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
                    llUser.setVisibility(View.GONE);
                    llGift.setVisibility(View.VISIBLE);
                    if (WebViewActivity.mActivity != null) {
                        WebViewActivity.mActivity.finish();
                    }
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            final GiftBean userGift = UserAPI.getUserGift(mContext);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
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
//                if (currentType != 1) {
                currentType = 1;
//                    ivMe.setImageResource(R.drawable.icon_main_me);
//                    ivService.setImageResource(R.drawable.icon_main_servicehign);
//                    ivGift.setImageResource(R.drawable.icon_main_gift);
//                    llUser.setVisibility(View.GONE);
//                    llGift.setVisibility(View.GONE);
//                    llUser.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));

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
//            }
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

    private MainLeftClick click = null;

    public interface MainLeftClick {
        void onClickMain();
    }
}
