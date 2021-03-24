package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.espay_user_lib.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class MoneyRuleDialog extends BaseDialog {

    public MoneyRuleDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, String content) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mContent = content;
    }

    private View mView;
    private Context mContext;
    private String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_rule, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView content = (TextView) mView.findViewById(R.id.tv_rule_content);
        ImageView mClose = mView.findViewById(R.id.iv_close_rule);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        try {
            content.setText(Html.fromHtml(URLDecoder.decode(mContent, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            content.setText(Html.fromHtml(mContent));
        }
    }
}
