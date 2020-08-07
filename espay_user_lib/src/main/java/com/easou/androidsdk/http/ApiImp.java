package com.easou.androidsdk.http;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.ui.LoadingDialog;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.Tools;

import org.json.JSONObject;

import java.util.Map;

public class ApiImp {

	private static final String TAG = "ApiImp";
	
	/**
	 * 游客登录
	 * @param param
	 * @return
	 */
	public static String[] autoRegister(String param, String head) {
		//TODO
		String result =HttpPostImp.sendPost(ApiInterface.apiTest+ApiInterface.autoRegister, param, head);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				
				JSONObject data = jsonObject.getJSONObject("data");
				
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
				result_arr[2] = data.getString("aid"); //通讯协议版本号
				result_arr[3] = data.getString("bn"); // 商户代码
				result_arr[4] = data.getString("tid"); // 商户订单号
				ESPayLog.d("EAPayInter" , result_arr[0]+"/n" + result_arr[1]+"/n" + result_arr[2]+"/n"
			+ result_arr[3] +"/n"+ result_arr[4]);
				ESPayLog.d(TAG , "微信解析完毕。");
			} else {
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG , e.toString());
		}
		return result_arr;
	}

	/**
	 * 实名认证
	 * @param param
	 * @return
	 */
	public static String[] userAuthen(String param, String head) {
		//TODO
		String result =HttpPostImp.sendPost(ApiInterface.apiTest+ApiInterface.authen, param, head);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");

			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {

				JSONObject data = jsonObject.getJSONObject("data");

				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
				result_arr[2] = data.getString("aid"); //通讯协议版本号
				result_arr[3] = data.getString("bn"); // 商户代码
				result_arr[4] = data.getString("tid"); // 商户订单号
				ESPayLog.d("EAPayInter" , result_arr[0]+"/n" + result_arr[1]+"/n" + result_arr[2]+"/n"
						+ result_arr[3] +"/n"+ result_arr[4]);
				ESPayLog.d(TAG , "微信解析完毕。");
			} else {
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG , e.toString());
		}
		return result_arr;
	}
}
