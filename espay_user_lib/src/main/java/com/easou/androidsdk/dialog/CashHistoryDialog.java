package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.login.service.CashHistoryInfo;
import com.easou.espay_user_lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class CashHistoryDialog extends BaseDialog {

    public CashHistoryDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight, CashHistoryInfo info) {
        super(context, animation, gravity, mWidth, mHeight, true);
        mContext = context;
        mInfo = info;
    }

    private View mView;
    private Context mContext;
    private CashHistoryInfo mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_moneyhistorydialog, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        final ViewPager viewPager = mView.findViewById(R.id.vp_history);
        final TextView mCurrentPage = mView.findViewById(R.id.tv_currentpage);
        final HistoryAdapter adapter = new HistoryAdapter(mContext, mInfo.getDrawList());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentPage.setText((i + 1) + "");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        ImageView mPre = mView.findViewById(R.id.iv_pre);
        mPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    mCurrentPage.setText(viewPager.getCurrentItem() + 1 + "");
                }
            }
        });
        ImageView mNext = mView.findViewById(R.id.iv_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() < adapter.getCount() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    mCurrentPage.setText(viewPager.getCurrentItem() + 1 + "");
                }
            }
        });
        ImageView mClose = mView.findViewById(R.id.iv_history_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
