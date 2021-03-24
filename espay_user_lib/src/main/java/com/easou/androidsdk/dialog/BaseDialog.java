package com.easou.androidsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
class BaseDialog extends Dialog {
    private int animation;
    private int gravity;
    private int mHeight;
    private float mWidth;
    private Context mContext;
    private boolean mIsCanCancel;

    public BaseDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, boolean isCanCancel) {
        super(context, R.style.CommentStyle);
        this.animation = animation;
        this.gravity = gravity;
        this.mHeight = mHeight;
        this.mWidth = mWidth;
        this.mContext = context;
        this.mIsCanCancel = isCanCancel;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        window.setWindowAnimations(animation);
        window.setGravity(gravity);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams windowparams = window.getAttributes();
        windowparams.height = mHeight == 1 ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
        windowparams.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * mWidth);
        window.setAttributes(windowparams);
        setCanceledOnTouchOutside(mIsCanCancel);
        setCancelable(mIsCanCancel);
    }
}
