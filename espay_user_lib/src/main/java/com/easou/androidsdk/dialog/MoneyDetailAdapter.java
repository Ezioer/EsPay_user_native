package com.easou.androidsdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easou.androidsdk.login.service.DrawResultInfo;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.GainMoneyInfo;
import com.easou.androidsdk.login.service.MoneyInfo;
import com.easou.androidsdk.login.service.MoneyListDetail;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

import java.util.List;

/**
 * created by xiaoqing.zhou
 * on 2020/8/12
 * fun
 */
class MoneyDetailAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<MoneyListDetail> list;
    private Context mContext;

    public MoneyDetailAdapter(Context context, List<MoneyListDetail> list) {
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
        MoneyViewHolder holder = null;
        View item = null;
        if (convertView == null) {
            holder = new MoneyViewHolder();
            item = mInflater.inflate(R.layout.item_moneytask, null);
            holder.moneyValue = (TextView) item.findViewById(R.id.tv_money_value);
            holder.taskInfo = (TextView) item.findViewById(R.id.tv_taskinfo);
            holder.taskState = (TextView) item.findViewById(R.id.tv_taskstate);
            item.setTag(holder);
        } else {
            item = convertView;
            holder = (MoneyViewHolder) item.getTag();
        }

        final MoneyViewHolder finalHolder = holder;
        holder.taskState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && list.size() > 0 && list.get(position).getStatus() == 0) {
                    //已完成才会去领取
                    final String playerId = CommonUtils.getPlayerId(mContext);
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            final DrawResultInfo moneyTask = EucService.getInstance(mContext).
                                    getMoney(playerId, CommonUtils.getServerId(mContext), list.get(position).getId());
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (moneyTask == null) {
                                        ESToast.getInstance().ToastShow(mContext, "领取失败");
                                        return;
                                    }
                                    if (moneyTask.getStatus() == 1) {
                                        list.get(position).setStatus(2);
                                        finalHolder.taskState.setText("已领取");
                                        finalHolder.taskState.setTextColor(Color.parseColor("#67ae5b"));
                                        listener.onClick(position, Integer.valueOf(list.get(position).getMoney()));
                                    }
                                    ESToast.getInstance().ToastShow(mContext, moneyTask.getMsg());
                                }
                            });
                        }
                    });
                }
            }
        });

        if (list.size() > 0) {
            holder.moneyValue.setText(list.get(position).getMoney() + "");
            holder.taskInfo.setText((list.get(position).getDesc()));
            String statusText = "";
            int status = list.get(position).getStatus();
            if (status == 0) {
                statusText = "已完成";
                holder.taskState.setTextColor(Color.parseColor("#f82172"));
            } else if (status == 2) {
                statusText = "已领取";
                holder.taskState.setTextColor(Color.parseColor("#67ae5b"));
            } else if (status == 1) {
                statusText = "待完成";
                holder.taskState.setTextColor(Color.parseColor("#90000000"));
            } else if (status == -1) {
                statusText = "已过期";
                holder.taskState.setTextColor(Color.parseColor("#90000000"));
            }
            holder.taskState.setText(statusText);
        }
        return item;
    }

    public final class MoneyViewHolder {

        public TextView moneyValue;
        public TextView taskInfo;
        public TextView taskState;
    }

    private TaskClickListener listener = null;

    public void setTaskListener(TaskClickListener listener) {
        this.listener = listener;
    }

    public interface TaskClickListener {
        void onClick(int pos, int moneyNum);
    }
}
