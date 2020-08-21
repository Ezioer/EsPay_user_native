package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class AuthenNotiDialog extends BaseDialog {

    public AuthenNotiDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, int us) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
        this.us = us;
    }

    private View mView;
    private Context mContext;
    private int us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_authentication_noti, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        final View includeAuthen = mView.findViewById(R.id.include_authen);
        ImageView closeDialog = (ImageView) mView.findViewById(R.id.iv_close_authendialog);
        TextView authenNow = (TextView) mView.findViewById(R.id.tv_authen_now);
        final EditText editTextName = (EditText) includeAuthen.findViewById(R.id.et_inputname);
        final EditText editTextNum = (EditText) includeAuthen.findViewById(R.id.et_inputidnumber);
        TextView changeAccount = (TextView) includeAuthen.findViewById(R.id.tv__changeaccount);
        TextView submitAuthen = (TextView) includeAuthen.findViewById(R.id.tv_authen_submit_dialog);
        final LinearLayout llNoti = (LinearLayout) mView.findViewById(R.id.ll_noti);
        if (us == 1 || us == 4) {
            closeDialog.setVisibility(View.VISIBLE);
        } else {
            closeDialog.setVisibility(View.GONE);
        }
        if (us == 4) {
            changeAccount.setVisibility(View.GONE);
        } else {
            changeAccount.setVisibility(View.VISIBLE);
        }
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeListener != null) {
                    closeListener.dialogClose();
                }
                dismiss();
            }
        });
        authenNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNoti.setVisibility(View.GONE);
                includeAuthen.setVisibility(View.VISIBLE);
            }
        });

        submitAuthen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交实名认证
                final String name = editTextName.getText().toString();
                final String idNum = editTextNum.getText().toString();
                if (!CommonUtils.isIDNumber(idNum)) {
                    ESToast.getInstance().ToastShow(mContext, "请输入正确的身份证号吗");
                    return;
                }
                if (!CommonUtils.checkNameChinese(name)) {
                    ESToast.getInstance().ToastShow(mContext, "请输入正确的名字");
                    return;
                }
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(idNum)) {
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final EucApiResult<String> info = AuthAPI.userIdentify(name, idNum, Constant.ESDK_USERID, Constant.ESDK_APP_ID, RegisterAPI.getRequestInfo(mContext), mContext);
                                if (info.getResultCode().equals(CodeConstant.OK)) {
                                    int age = CommonUtils.getAge(idNum);
                                    StartESAccountCenter.getPayLimitInfo(mContext, age, idNum, age > 18 ? "1" : "0");
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ESToast.getInstance().ToastShow(mContext, "认证成功");
                                            dismiss();
                                            if (listener != null) {
                                                listener.authenSuccess(CommonUtils.getYMDfromIdNum(idNum));
                                            }
                                        }
                                    });
                                } else {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (info.getDescList().size() > 0) {
                                                ESToast.getInstance().ToastShow(mContext, info.getDescList().get(0).getD());
                                            } else {
                                                ESToast.getInstance().ToastShow(mContext, "认证失败");
                                            }
                                        }
                                    });
                                }
                            } catch (EucAPIException e) {
                            }
                        }
                    });
                } else {
                    ESToast.getInstance().ToastShow(mContext, "姓名和身份证号必须不为空");
                }
            }
        });

        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换账号
                dismiss();
                StartESUserPlugin.showLoginDialog();
            }
        });
    }

    private authenResult listener = null;

    public void setResult(authenResult result) {
        listener = result;
    }

    public interface authenResult {
        void authenSuccess(String ymd);
    }

    private setCloseListener closeListener = null;

    public void setCloseListener(setCloseListener listener) {
        closeListener = listener;
    }

    public interface setCloseListener {
        void dialogClose();
    }
}
