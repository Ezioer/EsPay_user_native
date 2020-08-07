package com.easou.androidsdk.http;

import android.os.AsyncTask;
import android.widget.Toast;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.ui.LoadingDialog;
import com.easou.androidsdk.ui.UIHelper;
import com.easou.androidsdk.util.DialogerUtils;

/**
 * 用于请求服务器的Task封装类
 * @author ：Heavy
 * @time   ：2014年10月28日
 * 
 */
public abstract class HttpAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	
    @Override
	protected void onPostExecute(Result result) {
		LoadingDialog.dismiss();
		super.onPostExecute(result);
	}

	public final AsyncTask<Params, Progress, Result> executeProxy(Params... params) {
    	if(NetHelper.isNet(Constant.context))
    		return execute(params);
		else{
			onHttpFailedExecute();
			LoadingDialog.dismiss();
			if(Constant.context != null) {
				UIHelper.isClicked = false;
			}
			DialogerUtils.dismiss(Constant.context);
			Toast.makeText(Constant.context, Constant.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
			return null;
		}
    }

	/**
	 * 在网络无法连接时调用
	 */
	protected void onHttpFailedExecute(){
		LoadingDialog.dismiss();
//		Toast.makeText(AppConstant.context, Constant.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
	}

}
