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

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class BindWxDialog extends BaseDialog {

    public BindWxDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, String content, String code) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mContent = content;
        mCode = code;
    }

    private View mView;
    private Context mContext;
    private String mContent;
    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bindwx, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView bind = (TextView) mView.findViewById(R.id.tv_copyandbind);
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //复制并绑定微信
            }
        });
        TextView code = (TextView) mView.findViewById(R.id.tv_bindcode_value);
        code.setText(mCode);
        TextView content = (TextView) mView.findViewById(R.id.tv_noti_content);
        ImageView mClose = mView.findViewById(R.id.iv_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        content.setText(Html.fromHtml(mContent));
    }
}
