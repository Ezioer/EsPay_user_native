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
class GiftAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<GiftInfo> list;
    private Context mContext;

    public GiftAdapter(Context context, List<GiftInfo> list) {
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
            item = mInflater.inflate(R.layout.item_gift, null);
            holder.giftName = (TextView) item.findViewById(R.id.tv_newgift);
            holder.giftInfo = (TextView) item.findViewById(R.id.tv_giftinfo);
            holder.giftCode = (TextView) item.findViewById(R.id.tv_getGiftCode);
            item.setTag(holder);
        } else {
            item = convertView;
            holder = (ViewHolder) item.getTag();
        }

        final ViewHolder finalHolder = holder;
        holder.giftCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && list.size() > 0) {
                    int ps = list.get(position).getPs();
                    int us = list.get(position).getUs();

                    if (list.get(position).getStatus() == 1) {
                        listener.onClick(position);
                    } else {
                        if (ps == 0 && us == 1) {
                            //需要实名认证才能领取
                            if (TextUtils.isEmpty(Starter.loginBean.getUser().getIdentityNum())) {
                                ESToast.getInstance().ToastShow(mContext, "该礼包需实名认证后才可领取");
                                return;
                            }
                        } else if (ps == 1 && us == 0) {
                            //需要绑定手机才能领取
                            if (TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())) {
                                ESToast.getInstance().ToastShow(mContext, "该礼包需绑定手机后才可领取");
                                return;
                            }
                        } else if (ps == 1 && us == 1) {
                            if (TextUtils.isEmpty(Starter.loginBean.getUser().getIdentityNum()) ||
                                    TextUtils.isEmpty(Starter.loginBean.getUser().getMobile())) {
                                ESToast.getInstance().ToastShow(mContext, "该礼包需绑定手机且实名认证后才可领取");
                                return;
                            }
                        }
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                final GiftBean bean = UserAPI.getGiftUse(mContext, String.valueOf(list.get(position).getActivityId()));
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (bean != null) {
                                            if (bean.getResultCode() == 1) {
                                                list.get(position).setStatus(1);
                                                finalHolder.giftCode.setText("已领取");
                                                listener.onClick(position);
                                            } else {
                                                ESToast.getInstance().ToastShow(mContext, bean.getMsg());
                                            }
                                        } else {
                                            ESToast.getInstance().ToastShow(mContext, "出错啦");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        if (list.size() > 0) {
            holder.giftName.setText(list.get(position).getGiftname());
            holder.giftInfo.setText((list.get(position).getGiftinfo()));
            holder.giftCode.setText(list.get(position).getStatus() == 1 ? "已领取" : "领取礼包码");
        }
        return item;
    }

    public final class ViewHolder {

        public TextView giftName;
        public TextView giftInfo;
        public TextView giftCode;
    }

    private GetCodeClickListener listener = null;

    public void setCodeListener(GetCodeClickListener listener) {
        this.listener = listener;
    }

    public interface GetCodeClickListener {
        void onClick(int pos);
    }
}
