package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.util.CommonUtils;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class GetCashNotiDialog extends BaseDialog {

    public GetCashNotiDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, String content, String code, String title) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mContent = content;
        mCode = code;
        mTitle = title;
    }

    private View mView;
    private Context mContext;
    private String mContent;
    private String mCode;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_getcashnoti, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView mTitleView = mView.findViewById(R.id.tv_getcashresult_title);
        mTitleView.setText(mTitle);
        TextView contentTextView = mView.findViewById(R.id.tv_getcashresult_content);
        contentTextView.setText(CommonUtils.isNotNullOrEmpty(mContent) ? mContent : "提现成功！您的红包提现码：");
        TextView codeTextView = mView.findViewById(R.id.tv_getcashresult_code);
        if (mCode.isEmpty()) {
            codeTextView.setVisibility(View.GONE);
        } else {
            codeTextView.setText(mCode);
        }
        ImageView mClose = mView.findViewById(R.id.iv_getcashresult_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
