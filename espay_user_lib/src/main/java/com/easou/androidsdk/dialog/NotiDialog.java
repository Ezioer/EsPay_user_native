package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class NotiDialog extends BaseDialog {

    public NotiDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,String title,String content,String buttom) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
        mTitle = title;
        mContent = content;
        mButtom = buttom;
    }

    private View mView;
    private Context mContext;
    private String mTitle;
    private String mContent;
    private String mButtom;

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
        if (mButtom.isEmpty()){
            buttom.setVisibility(View.GONE);
        } else {
            buttom.setVisibility(View.VISIBLE);
        }
        buttom.setText(mButtom);
        title.setText(mTitle);
        content.setText(mContent);
    }
}