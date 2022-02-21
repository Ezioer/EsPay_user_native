package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.AuthenCallBack;
import com.easou.androidsdk.login.LoginCallBack;
import com.easou.androidsdk.login.service.LogoutInfo;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.espay_user_lib.R;

import java.util.Timer;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class DeleteAccountDialog extends BaseDialog {

    public DeleteAccountDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,
                               LogoutInfo logoutInfo) {
        super(context, animation, gravity, mWidth, mHeight, false);
        mContext = context;
        mInfo = logoutInfo;
    }

    private View mView;
    private Context mContext;
    private TextView mTvContent;
    private CheckBox mCbAgree;
    private LogoutInfo mInfo;
    private Button mNext, mCancel, mOK;
    private LinearLayout mLlInfo;
    private CountDownTimer timer;
    //注销条件页面
    private int mCurrentPageType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_deleteaccount, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        ImageView back = mView.findViewById(R.id.iv_da_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null) {
                    timer.cancel();
                }
                dismiss();
            }
        });

        mTvContent = mView.findViewById(R.id.tv_da_content);
        mTvContent.setText(Html.fromHtml(mInfo.getCancellationCondition()));
        textHtmlClick(mContext, mTvContent);
        mCbAgree = mView.findViewById(R.id.cb_da_agree);
        mNext = mView.findViewById(R.id.btn_da_next);
        initTimer();
        mCancel = mView.findViewById(R.id.btn_da_cancel);
        final String phone = Starter.loginBean.getUser().getMobile();
        final EditText etName = mView.findViewById(R.id.et_da_name);
        final EditText etIdNum = mView.findViewById(R.id.et_da_idnum);
        final EditText etVerCode = mView.findViewById(R.id.et_da_vercode);
        final TextView etGetCode = mView.findViewById(R.id.tv_da_getcode);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //放弃注销
                dismiss();
            }
        });
        mOK = mView.findViewById(R.id.btn_da_ok);
        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确认提交注销
                StartESAccountCenter.submitLogout(
                        new LoginCallBack() {
                            @Override
                            public void loginSuccess() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, "已提交账号注销申请");
                                        dismiss();
                                        StartESAccountCenter.logout(mContext);
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
                        }, etName.getText().toString(), etIdNum.getText().toString(), phone, etVerCode.getText().toString()
                        , Starter.loginBean.getUser().getName(), mContext);
            }
        });
        mLlInfo = mView.findViewById(R.id.ll_da_info);
        TextView etAccount = mView.findViewById(R.id.et_da_account);
        etAccount.setText(Starter.loginBean.getUser().getName());
        final CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                etGetCode.setText(millisUntilFinished / 1000 + "");
                etGetCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                etGetCode.setText("获取");
                etGetCode.setEnabled(true);
            }
        };
        etGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取验证码
                StartESAccountCenter.getLogoutCode(new LoginCallBack() {
                    @Override
                    public void loginSuccess() {
                        timer.start();
                    }

                    @Override
                    public void loginFail(String msg) {
                        if (timer != null) {
                            timer.cancel();
                            timer.onFinish();
                        }
                    }
                }, phone, mContext);
            }
        });
        LinearLayout llBindPhone = mView.findViewById(R.id.ll_da_bindphone);
        LinearLayout llCode = mView.findViewById(R.id.ll_da_code);
        final TextView etPhoneNum = mView.findViewById(R.id.et_da_phonenum);
        if (CommonUtils.isNotNullOrEmpty(phone)) {
            etPhoneNum.setText(phone);
        } else {
            llBindPhone.setVisibility(View.GONE);
            llCode.setVisibility(View.GONE);
        }
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPageType == 1 || mCurrentPageType == 3) {
                    if (mCbAgree.isChecked()) {
                        switchPage(mCurrentPageType);
                    } else {
                        ESToast.getInstance().ToastShow(mContext, "请勾选条件须知");
                    }
                } else {
                    //验证短信验证码
                    final String name = etName.getText().toString();
                    final String idNum = etIdNum.getText().toString();
                    if (CommonUtils.isNotNullOrEmpty(name) && CommonUtils.isIDNumber(idNum)) {
                        if (CommonUtils.isNotNullOrEmpty(phone)) {
                            if (CommonUtils.isNotNullOrEmpty(etVerCode.getText().toString())) {
                                StartESAccountCenter.veriLogoutCode(phone, etVerCode.getText().toString(), new LoginCallBack() {
                                    @Override
                                    public void loginSuccess() {
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (verNameAndId(name, idNum)) {
                                                    switchPage(mCurrentPageType);
                                                } else {
                                                    ESToast.getInstance().ToastShow(mContext, "请填写正确的个人信息");
                                                }
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
                                }, mContext);
                            } else {
                                ESToast.getInstance().ToastShow(mContext, "请填写有效的验证码");
                            }
                        } else {
                            if (verNameAndId(name, idNum)) {
                                switchPage(mCurrentPageType);
                            } else {
                                ESToast.getInstance().ToastShow(mContext, "请填写正确的个人信息");
                            }
                        }
                    } else {
                        ESToast.getInstance().ToastShow(mContext, "请填写正确的个人信息");
                    }
                }
            }
        });
    }

    private boolean verNameAndId(String name, String idNum) {
        if (name.equals(Starter.loginBean.getUser().getIdentityName()) &&
                idNum.equals(Starter.loginBean.getUser().getIdentityNum())) {
            return true;
        }
        return false;
    }

    private void switchPage(int pageType) {
        if (pageType == 1) {
            //填写相关信息页面
            mCurrentPageType = 2;
            mCbAgree.setVisibility(View.GONE);
            mLlInfo.setVisibility(View.VISIBLE);
            mTvContent.setVisibility(View.GONE);
        } else if (pageType == 2) {
            //注销协议页面
            mCbAgree.setVisibility(View.VISIBLE);
            mCbAgree.setChecked(false);
            mCurrentPageType = 3;
            mLlInfo.setVisibility(View.GONE);
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(Html.fromHtml(mInfo.getCancellationAgreement()));
            textHtmlClick(mContext, mTvContent);
            mCbAgree.setText("我已详细阅读并同意《账号注销协议》");
            initTimer();
        } else if (pageType == 3) {
            //注销须知页面
            mTvContent.setText(Html.fromHtml(mInfo.getCancellationNotice()));
            textHtmlClick(mContext, mTvContent);
            mLlInfo.setVisibility(View.GONE);
            mTvContent.setVisibility(View.VISIBLE);
            mCbAgree.setVisibility(View.GONE);
            mNext.setVisibility(View.GONE);
            mCancel.setVisibility(View.VISIBLE);
            mOK.setVisibility(View.VISIBLE);
            mCurrentPageType = 4;
        }
    }

    public static void textHtmlClick(Context context, TextView tv) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();

        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL(), context);
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tv.setText(style);
        }

    }

    private static class MyURLSpan extends ClickableSpan {
        private String mUrl;
        private Context mContext;

        MyURLSpan(String url, Context context) {
            mContext = context;
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            //点击跳转到网页显示
            Intent intent = new Intent(mContext, CommonWebViewActivity.class);
            intent.putExtra("url", mUrl);
            mContext.startActivity(intent);
        }
    }

    private void initTimer() {
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
            timer = null;
        }
        timer = new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long l) {
                mNext.setText(l / 1000 + "");
                mNext.setEnabled(false);
            }

            @Override
            public void onFinish() {
                mNext.setText("下一步");
                mNext.setEnabled(true);
            }
        };
        timer.start();
    }
}
