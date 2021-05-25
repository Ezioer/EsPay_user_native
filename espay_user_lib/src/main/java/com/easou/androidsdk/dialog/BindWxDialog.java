package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.CheckBindByUserIdInfo;
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
        String text = "绑定微信步骤：\n\n" +
                "            1、打开微信，搜索公众号XXX并关注；\n\n" +
                "            2、打开XXX菜单，输入绑定码绑定；\n\n" +
                "            3、绑定后返回游戏，同步绑定状态后即可开启红包之旅。\n";
        TextView bind = (TextView) mView.findViewById(R.id.tv_copyandbind);
        TextView refresh = mView.findViewById(R.id.tv_refreshstate);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsBindWx();
            }
        });
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //复制并绑定微信
            }
        });
        TextView code = (TextView) mView.findViewById(R.id.tv_bindcode_value);
        code.setText(mCode);
        TextView content = (TextView) mView.findViewById(R.id.tv_step_content);
        ImageView mClose = mView.findViewById(R.id.iv_close_bindwx);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        content.setText(Html.fromHtml(text));
    }

    //检查当前用户是否绑定微信
    private void checkIsBindWx() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final EucApiResult<CheckBindByUserIdInfo> info = AuthAPI.checkIsBindThird(mContext, Constant.ESDK_USERID, RegisterAPI.getRequestInfo(mContext));
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (info.getResultCode().equals(CodeConstant.OK)) {
                                if (info.getResult().getStatus().equals("1")) {
                                    Starter.loginBean.getUser().setOpenId(info.getResult().getOpenId());
                                    ESToast.getInstance().ToastShow(mContext, "已成功绑定，您可以提现了");
                                } else {
                                    ESToast.getInstance().ToastShow(mContext, "暂未绑定微信");
                                }
                                if (closeListener != null) {
                                    closeListener.dialogClose();
                                }
                            } else {
                                ESToast.getInstance().ToastShow(mContext, "网络出错了，请重试");
                            }
                        }
                    });

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

    private DialogCloseListener closeListener = null;

    public void setCloseListener(DialogCloseListener listener) {
        closeListener = listener;
    }

    public interface DialogCloseListener {
        void dialogClose();
    }
}
