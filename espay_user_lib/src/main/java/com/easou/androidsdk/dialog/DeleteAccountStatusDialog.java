package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class DeleteAccountStatusDialog extends BaseDialog {

    public DeleteAccountStatusDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,
                                     String title, String content, boolean isTwoButton) {
        super(context, animation, gravity, mWidth, mHeight, false);
        mContext = context;
        mTitle = title;
        mContent = content;
        mIsTwoButton = isTwoButton;
    }

    private View mView;
    private Context mContext;
    private String mTitle;
    private String mContent;
    private boolean mIsTwoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_accountstatus, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView cancel = (TextView) mView.findViewById(R.id.tv_accountstatus_cancel);
        TextView submit = (TextView) mView.findViewById(R.id.tv_accountstatus_submit);
        TextView know = (TextView) mView.findViewById(R.id.tv_accountstatus_know);
        TextView title = (TextView) mView.findViewById(R.id.tv_accountstatus_type);
        TextView content = (TextView) mView.findViewById(R.id.tv_accountstatus_content);

        title.setText(mTitle);
        content.setText(mContent);
        if (!mIsTwoButton) {
            cancel.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            know.setVisibility(View.VISIBLE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //继续等待注销
                if (closeListener != null) {
                    closeListener.close(1);
                }
                dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确认取消注销
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EucApiResult<Integer> result = AuthAPI.cancelDeleteAccount(mContext,
                                    Constant.ESDK_TOKEN, Constant.ESDK_USERID, RegisterAPI.getRequestInfo(mContext));
                            if (result.getResultCode().equals(CodeConstant.OK)) {
                                if (closeListener != null) {
                                    closeListener.close(2);
                                }
                                dismiss();
                            } else {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ESToast.getInstance().ToastShow(mContext, "网络出错了，请重试");
                                    }
                                });
                            }
                        } catch (EucAPIException e) {
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
        });

        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //我知道了,注销驳回状态
                if (closeListener != null) {
                    closeListener.close(3);
                }
                dismiss();
            }
        });
    }

    private OnCloseListener closeListener = null;

    public void setOnCloseListener(OnCloseListener listener) {
        closeListener = listener;
    }

    public interface OnCloseListener {
        void close(int type);
    }
}
