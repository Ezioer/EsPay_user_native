package com.easou.androidsdk.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easou.androidsdk.login.service.CashHistoryBean;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.espay_user_lib.R;

import java.util.List;

class MoneyTypeAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mTitleList;
    private List<Integer> mIdList;

    public MoneyTypeAdapter(Context mContext, List<String> mList, List<Integer> mIdList) {
        this.mContext = mContext;
        this.mTitleList = mList;
        this.mIdList = mIdList;
    }

    @Override
    public int getCount() {
        return mTitleList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_moneylist, container, false);
        container.addView(view);

        return view;
    }
}
