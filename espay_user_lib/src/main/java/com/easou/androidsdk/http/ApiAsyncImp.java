package com.easou.androidsdk.http;

import android.content.Context;

import com.easou.androidsdk.data.ApiType;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.login.service.RequestInfo;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.DialogerUtils;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.Md5SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class ApiAsyncImp extends BaseAsyncTask<Void, Void, String[]> {
    private Map<String, String> map;
    private Context mContext;
    private ApiType type;
    public DataFinishListener dataFinishListener;

    public void setDataFinishListener(DataFinishListener dataFinishListener) {
        this.dataFinishListener = dataFinishListener;
    }

    public ApiAsyncImp(Context context, Map<String, String> map, ApiType type) {
        // TODO Auto-generated constructor stub
        this.map = map;
        this.mContext = context;
        this.type = type;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        // TODO Auto-generated method stub
        String[] result = null;
        switch (type) {
            case AUTOREGISTER:
             /*   try {
                    EucApiResult<AuthBean> userInfo = RegisterAPI.autoRegist(true, getRequestInfo(mContext), mContext);
                    result = new String[13];
                    result[0] = userInfo.getResultCode();
                    result[1] = String.valueOf(userInfo.getResult().getUser().getId());
                    result[2] = userInfo.getResult().getUser().getName();
                    result[3] = userInfo.getResult().getToken().token;
                } catch (EucAPIException e) {

                }*/
                break;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String[] result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        DialogerUtils.dismiss(mContext);
        JSONObject json = new JSONObject();
        if (result == null) {
            return;
        }
        if (result[1] != null
                && result[1].equals(Constant.FLAG_TRADE_RESULT_SUC)) {
            ESPayLog.d("响应数据：" + result.toString());
            switch (type) {
            }
            dataFinishListener.setJson(json);
        } else {
            if (result[0] != null && !result[0].equals("")) {
                ESToast.getInstance().ToastShow(mContext, result[0]);
            }
        }
    }

    public interface DataFinishListener {
        void setJson(Object object);
    }
}
