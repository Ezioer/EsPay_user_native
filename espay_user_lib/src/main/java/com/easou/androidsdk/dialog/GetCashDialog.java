package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.login.MoneyDataCallBack;
import com.easou.androidsdk.login.service.CashLevelInfo;
import com.easou.androidsdk.login.service.DrawResultInfo;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.widget.flowlayout.FlowLayout;
import com.easou.androidsdk.widget.flowlayout.TagAdapter;
import com.easou.androidsdk.widget.flowlayout.TagFlowLayout;
import com.easou.espay_user_lib.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class GetCashDialog extends BaseDialog {

    public GetCashDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, CashLevelInfo info) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mInfo = info;
    }

    private View mView;
    private Context mContext;
    private CashLevelInfo mInfo;
    private String mSelectMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_getcashdialog, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView content = (TextView) mView.findViewById(R.id.tv_getcashrule);
        final TagFlowLayout tagsLevel = mView.findViewById(R.id.tags_cashlevel);
        String[] split = mInfo.getPriceLevels().split(",");
        final List<String> list = Arrays.asList(split);
        TagAdapter tagAdapter = new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView tvTag = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, tagsLevel, false);
                tvTag.setText(o);
                return tvTag;
            }
        };
        tagsLevel.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                mSelectMoney = list.get(position);
                return true;
            }
        });
        tagsLevel.setAdapter(tagAdapter);
        tagAdapter.setSelectedList(0);
        mSelectMoney = list.get(0);
        ImageView mClose = mView.findViewById(R.id.iv_close_getcash);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView mSubmit = mView.findViewById(R.id.tv_getcash_ok);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartESAccountCenter.getCash(new MoneyDataCallBack<DrawResultInfo>() {
                    @Override
                    public void success(final DrawResultInfo drawResultInfo) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (drawResultInfo.getStatus() == 1) {
                                    //提现成功
                                    if (listener != null) {
                                        listener.cashSuccess(Integer.valueOf(mSelectMoney));
                                    }
                                }
                                ESToast.getInstance().ToastShow(mContext, drawResultInfo.getMsg());
                                dismiss();
                            }
                        });
                    }

                    @Override
                    public void fail(final String msg) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                                ESToast.getInstance().ToastShow(mContext, msg);
                            }
                        });
                    }
                }, mContext, CommonUtils.getPlayerId(mContext), mSelectMoney, CommonUtils.getServerId(mContext));
            }
        });
        try {
            content.setText(Html.fromHtml(URLDecoder.decode(mInfo.getDrawRule(), "utf-8")));
        } catch (UnsupportedEncodingException e) {
            content.setText(Html.fromHtml(mInfo.getDrawRule()));
        }
    }

    private getCashSuccess listener = null;

    public void setListener(getCashSuccess listener) {
        this.listener = listener;
    }

    public interface getCashSuccess {
        void cashSuccess(int money);
    }
}
