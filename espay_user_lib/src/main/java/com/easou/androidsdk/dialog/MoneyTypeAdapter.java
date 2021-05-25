package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easou.androidsdk.StartESAccountCenter;
import com.easou.androidsdk.login.MoneyDataCallBack;
import com.easou.androidsdk.login.service.CashHistoryBean;
import com.easou.androidsdk.login.service.MoneyGroupInfo;
import com.easou.androidsdk.login.service.MoneyList;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

import java.util.ArrayList;
import java.util.List;

class MoneyTypeAdapter extends PagerAdapter {

    private Context mContext;
    private List<MoneyGroupInfo> mList;
    private List<ListView> mListView = new ArrayList<>();

    public MoneyTypeAdapter(Context mContext, List<MoneyGroupInfo> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getGroupName();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public ListView getView(int i) {
        return mListView.get(i);
    }

    @NonNull
    @Override
    public Object instantiateItem(final @NonNull ViewGroup container, final int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_moneylist, container, false);
        final ListView moneyList = view.findViewById(R.id.listview_newmoney);
        mListView.add(moneyList);
       /* ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                StartESAccountCenter.moneyListDetailInfo(new MoneyDataCallBack<MoneyList>() {
                    @Override
                    public void success(final MoneyList moneyListInfo) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (moneyListInfo.getBonusList().size() > 0) {
                                    MoneyDetailAdapter adapter = new MoneyDetailAdapter(mContext, moneyListInfo.getBonusList());
                                    moneyList.setAdapter(adapter);
                                }
                                container.addView(view);
                            }
                        });
                    }

                    @Override
                    public void fail(String msg) {

                    }
                }, mContext, CommonUtils.getPlayerId(mContext), CommonUtils.getServerId(mContext), mList.get(position).getGroupId());
            }
        });*/
        container.addView(view);
        return view;
    }
}
