package com.easou.androidsdk.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
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

class HistoryAdapter extends PagerAdapter {

    private Context mContext;
    private List<CashHistoryBean> mList;

    public HistoryAdapter(Context mContext, List<CashHistoryBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        if (mList.size() % 3 == 0) {
            return (mList.size() / 3);
        } else {
            return (mList.size() / 3) + 1;
        }

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_historyitem, container, false);
        LinearLayout mOne = view.findViewById(R.id.ll_history_one);
        LinearLayout mTwo = view.findViewById(R.id.ll_history_two);
        LinearLayout mThree = view.findViewById(R.id.ll_history_three);
        try {
            if (position + 1 != getCount()) {
                int k = 0;
                mOne.setVisibility(View.VISIBLE);
                mTwo.setVisibility(View.VISIBLE);
                mThree.setVisibility(View.VISIBLE);
                for (int i = position * 3; i < 3 * (position + 1); i++) {
                    if (k == 0) {
                        final CashHistoryBean bean = mList.get(i);
                        TextView contentOne = view.findViewById(R.id.tv_moneycontent_one);
                        TextView timeOne = view.findViewById(R.id.tv_timevalue_one);
                        TextView valueOne = view.findViewById(R.id.tv_moneyvalue_one);
                        TextView stateOne = view.findViewById(R.id.tv_moneystate_one);
                        valueOne.setText(bean.getMoney() + "元");
//                        timeOne.setText(CommonUtils.formatDate(bean.getDrawTime()));
                    } else if (k == 1) {
                        final CashHistoryBean bean = mList.get(i);
                        TextView contentTwo = view.findViewById(R.id.tv_moneycontent_two);
                        TextView timeTwo = view.findViewById(R.id.tv_timevalue_two);
                        TextView valueTwo = view.findViewById(R.id.tv_moneyvalue_two);
                        TextView statetwo = view.findViewById(R.id.tv_moneystate_two);
                        valueTwo.setText(bean.getMoney() + "元");
//                        timeTwo.setText(CommonUtils.formatDate(bean.getDrawTime()));
                    } else {
                        final CashHistoryBean bean = mList.get(i);
                        TextView contentThree = view.findViewById(R.id.tv_moneycontent_three);
                        TextView timeThree = view.findViewById(R.id.tv_timevalue_three);
                        TextView valueThree = view.findViewById(R.id.tv_moneyvalue_three);
                        TextView stateThree = view.findViewById(R.id.tv_moneystate_three);
                        valueThree.setText(bean.getMoney() + "元");
//                        timeThree.setText(CommonUtils.formatDate(bean.getDrawTime()));
                    }
                    k++;
                }
            } else {
                int k = 0;
                for (int i = position * 3; i < mList.size(); i++) {
                    if (k == 0) {
                        mOne.setVisibility(View.VISIBLE);
                        final CashHistoryBean bean = mList.get(i);
                        TextView contentOne = view.findViewById(R.id.tv_moneycontent_one);
                        TextView timeOne = view.findViewById(R.id.tv_timevalue_one);
                        TextView valueOne = view.findViewById(R.id.tv_moneyvalue_one);
                        TextView stateOne = view.findViewById(R.id.tv_moneystate_one);
                        valueOne.setText(bean.getMoney() + "元");
//                        timeOne.setText(CommonUtils.formatDate(bean.getDrawTime()));

                    } else if (k == 1) {
                        mTwo.setVisibility(View.VISIBLE);
                        final CashHistoryBean bean = mList.get(i);
                        TextView contentTwo = view.findViewById(R.id.tv_moneycontent_two);
                        TextView timeTwo = view.findViewById(R.id.tv_timevalue_two);
                        TextView valueTwo = view.findViewById(R.id.tv_moneyvalue_two);
                        TextView statetwo = view.findViewById(R.id.tv_moneystate_two);
                        valueTwo.setText(bean.getMoney() + "元");
//                        timeTwo.setText(CommonUtils.formatDate(bean.getDrawTime()));
                    } else {
                        mThree.setVisibility(View.VISIBLE);
                        final CashHistoryBean bean = mList.get(i);
                        TextView contentThree = view.findViewById(R.id.tv_moneycontent_three);
                        TextView timeThree = view.findViewById(R.id.tv_timevalue_three);
                        TextView valueThree = view.findViewById(R.id.tv_moneyvalue_three);
                        TextView stateThree = view.findViewById(R.id.tv_moneystate_three);
                        valueThree.setText(bean.getMoney() + "元");
//                        timeThree.setText(CommonUtils.formatDate(bean.getDrawTime()));
                    }
                    k++;
                }
            }
        } catch (Exception e) {

        }
        container.addView(view);
        return view;
    }
}
