package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.data.ApiType;
import com.easou.androidsdk.http.ApiAsyncImp;
import com.easou.espay_user_lib.R;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class AccountInfoDialog extends BaseDialog {

    public AccountInfoDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,String account,String password) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
        this.account = account;
        this.password = password;
    }

    private View mView;
    private Context mContext;
    private String account;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_account_info, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        ImageView close = (ImageView) mView.findViewById(R.id.iv_clodedialog);
        TextView accountValue = (TextView) mView.findViewById(R.id.tv_account_value);
        TextView passwordValue = (TextView) mView.findViewById(R.id.tv_passwordvalue);
        accountValue.setText("账号："+account);
        passwordValue.setText("密码："+password);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.dialogClose();
                }
            }
        });
    }

    private OnCloseDialogListener listener = null;

    public void setCloseListener(OnCloseDialogListener listener) {
        this.listener = listener;
    }

    public interface OnCloseDialogListener {
        void dialogClose();
    }
}
