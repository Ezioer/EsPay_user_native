package com.easou.androidsdk.dialog;

import android.content.Context;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.Starter;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class NotiDialog extends BaseDialog {

    public NotiDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,
                      boolean isForceQuit, String title, String content, String buttom) {
        super(context, animation, gravity, mWidth, mHeight, false);
        mContext = context;
        mTitle = title;
        mContent = content;
        mButtom = buttom;
        this.isForceQuit = isForceQuit;
    }

    private View mView;
    private Context mContext;
    private String mTitle;
    private String mContent;
    private String mButtom;
    private boolean isForceQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_noti, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView buttom = (TextView) mView.findViewById(R.id.tv_buttom);
        TextView title = (TextView) mView.findViewById(R.id.tv_title_type);
        TextView content = (TextView) mView.findViewById(R.id.tv_noti_content);
        ImageView mClose = mView.findViewById(R.id.iv_close);
        if (isForceQuit) {
            mClose.setVisibility(View.GONE);
            buttom.setVisibility(View.VISIBLE);
            buttom.setText("退出登录");
        }
        buttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //强制退出登录
                dismiss();
                StartESAccountCenter.logout(mContext);
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isForceQuit) {
                    StartESAccountCenter.logout(mContext);
                } else {
                    if (closeListener != null) {
                        closeListener.close();
                    }
                    dismiss();
                }
            }
        });
        content.setText(mContent);
    }

    private OnCloseListener closeListener = null;

    public void setOnCloseListener(OnCloseListener listener) {
        closeListener = listener;
    }

    public interface OnCloseListener {
        void close();
    }
}
