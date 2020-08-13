package com.easou.androidsdk.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easou.androidsdk.login.service.GiftInfo;
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


    public GiftAdapter(Context context, List<GiftInfo> list) {

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

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gift, null);
            holder.giftName = (TextView) convertView.findViewById(R.id.tv_newgift);
            holder.giftInfo = (TextView) convertView.findViewById(R.id.tv_giftinfo);
            holder.giftCode = (TextView) convertView.findViewById(R.id.tv_getGiftCode);
            final ViewHolder finalHolder = holder;
            holder.giftCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        finalHolder.giftCode.setText("已领取");
                        listener.onClick(position);
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.giftName.setText(list.get(position).getGiftname());
        holder.giftInfo.setText((list.get(position).getGiftinfo()));
        holder.giftCode.setText(list.get(position).getStatus() == 1 ? "已领取" : "领取礼包码");
        return convertView;
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
