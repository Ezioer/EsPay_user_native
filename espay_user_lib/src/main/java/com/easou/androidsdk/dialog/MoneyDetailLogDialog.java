package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easou.androidsdk.login.service.CashHistoryBean;
import com.easou.androidsdk.login.service.CashHistoryInfo;
import com.easou.espay_user_lib.R;

import java.util.List;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class MoneyDetailLogDialog extends BaseDialog {

    public MoneyDetailLogDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, List<CashHistoryBean> info) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mInfo = info;
    }

    private View mView;
    private Context mContext;
    private List<CashHistoryBean> mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_moneydetaillog, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        ListView mList = findViewById(R.id.list_moneylog);
        MoneyDetailLogAdapter adapter = new MoneyDetailLogAdapter(mContext, mInfo);
        mList.setAdapter(adapter);
        ImageView mClose = mView.findViewById(R.id.iv_moneydetail_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
