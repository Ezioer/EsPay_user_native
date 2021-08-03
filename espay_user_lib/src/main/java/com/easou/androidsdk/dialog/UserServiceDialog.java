package com.easou.androidsdk.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easou.androidsdk.data.Constant;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class UserServiceDialog extends BaseDialog {

    public UserServiceDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,
                             boolean isShowAgree, String title, String content) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mTitle = title;
        mContent = content;
        this.isShowAgree = isShowAgree;
    }

    private View mView;
    private Context mContext;
    private String mTitle;
    private String mContent;
    private boolean isShowAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_user_service, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        final TextView agree = mView.findViewById(R.id.tv_agree);
        final TextView disagree = mView.findViewById(R.id.tv_disagree);
        final TextView know = mView.findViewById(R.id.tv_know);
        final TextView title = mView.findViewById(R.id.tv_service_title);
        final TextView content = mView.findViewById(R.id.tv_service_content);
        final TextView mPrivate = mView.findViewById(R.id.tv_dialog_private);
        final TextView mService = mView.findViewById(R.id.tv_dialog_service);
        if (!isShowAgree) {
            agree.setVisibility(View.GONE);
            disagree.setVisibility(View.GONE);
            know.setVisibility(View.VISIBLE);
        }
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (agreeListener != null) {
                    dismiss();
                    agreeListener.agree();
                }
            }
        });
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitle.contains("游客")) {
                    dismiss();
                } else {
                    agree.setVisibility(View.GONE);
                    disagree.setVisibility(View.GONE);
                    mPrivate.setVisibility(View.GONE);
                    mService.setVisibility(View.GONE);
                    know.setVisibility(View.VISIBLE);
                    content.setText("如未确认阅读并同意隐私政策协议，您将无法使用此产品");
                }
            }
        });
        mPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommonWebViewActivity.class);
                intent.putExtra("url", Constant.user_service);
                mContext.startActivity(intent);
            }
        });

        mService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommonWebViewActivity.class);
                intent.putExtra("url", Constant.user_service);
                mContext.startActivity(intent);
            }
        });
        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agree.setVisibility(View.VISIBLE);
                disagree.setVisibility(View.VISIBLE);
                know.setVisibility(View.GONE);
                mPrivate.setVisibility(View.VISIBLE);
                mService.setVisibility(View.VISIBLE);
                content.setText(mContent);
            }
        });
        title.setText(mTitle);
        content.setText(mContent);
    }

    private OnAgreeListener agreeListener = null;

    public void setOnAgreeListener(OnAgreeListener listener) {
        agreeListener = listener;
    }

    public interface OnAgreeListener {
        void agree();
    }
}
