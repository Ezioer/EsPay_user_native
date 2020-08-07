package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class LookPasswordDialog extends BaseDialog {

    public LookPasswordDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
    }

    private View mView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_lookpassword, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView buttom = (TextView) mView.findViewById(R.id.tv_submit);
        ImageView closeDialog = (ImageView) mView.findViewById(R.id.iv_close_dialog);
        final EditText editTextPhone = (EditText) mView.findViewById(R.id.et_phone);
        final EditText editTextCode = (EditText) mView.findViewById(R.id.et_code);
        final EditText editTextNewPw = (EditText) mView.findViewById(R.id.et_newpassword);
        TextView textViewGetCode = (TextView) mView.findViewById(R.id.tv_getcode);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        textViewGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = editTextPhone.getText().toString();
                if (phone.isEmpty()) {
                    return;
                }
            }
        });

        buttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = editTextPhone.getText().toString();
                final String code = editTextCode.getText().toString();
                final String password = editTextNewPw.getText().toString();
                if (phone.isEmpty() || code.isEmpty() || password.isEmpty()){
                    return;
                }
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EucApiResult<AuthBean> info = AuthAPI.applyResetPass(phone, password, code, RegisterAPI.getRequestInfo(mContext), mContext);
                            dismiss();
                        } catch (EucAPIException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
