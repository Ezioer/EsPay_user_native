package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.login.UserAPI;
import com.easou.androidsdk.login.service.CashHistoryBean;
import com.easou.androidsdk.login.service.GiftBean;
import com.easou.androidsdk.login.service.GiftInfo;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

import java.util.List;

/**
 * created by xiaoqing.zhou
 * on 2020/8/12
 * fun
 */
class MoneyDetailLogAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<CashHistoryBean> list;
    private Context mContext;

    public MoneyDetailLogAdapter(Context context, List<CashHistoryBean> list) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View item = null;
        if (convertView == null) {
            holder = new ViewHolder();
            item = mInflater.inflate(R.layout.item_moneydetaillog, null);
            holder.tv_moneycontent = (TextView) item.findViewById(R.id.tv_moneycontent);
            holder.tv_moneyvalue = (TextView) item.findViewById(R.id.tv_moneyvalue);
            holder.tv_timevalue = (TextView) item.findViewById(R.id.tv_timevalue);
            holder.tv_moneystate = (TextView) item.findViewById(R.id.tv_moneystate);
            item.setTag(holder);
        } else {
            item = convertView;
            holder = (ViewHolder) item.getTag();
        }

        final ViewHolder finalHolder = holder;

        if (list.size() > 0) {
            CashHistoryBean bean = list.get(position);
            String type = "";
            if (bean.getBalanceType() == 1) {
                type = "红包领取";
            } else if (bean.getBalanceType() == 2) {
                type = "红包提现";
            } else if (bean.getBalanceType() == 3) {
                type = "提现失败";
            }

            holder.tv_moneycontent.setText(type);
            holder.tv_timevalue.setText(bean.getUpdateTime());
            holder.tv_moneyvalue.setText(bean.getMoney() + "");
            holder.tv_moneystate.setText(bean.getMsg());
        }
        return item;
    }

    public final class ViewHolder {
        public TextView tv_moneycontent;
        public TextView tv_timevalue;
        public TextView tv_moneyvalue;
        public TextView tv_moneystate;
    }
}
