package com.easou.androidsdk.http;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.PayItem;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.Tools;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

public class EAPayInter {

	public static String domain = Constant.DOMAIN + Tools.getHostName();
	private static final String TAG = "EAPayInter";

	public static String queryUserEB(String token, String appId) {
		
		String url = "/basePay/getUserBalance.e";
		String param = Constant.EASOUTGC + "=" + token + "&appId=" + appId;
		String result = HttpGroupUtils.sendGet(domain + url, param, token);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			result_arr[0] = jsonObject.getString("msg");
			result_arr[1] = jsonObject.getString("status");
			result_arr[2] = jsonObject.getString("userEbBalance"); //e币余额
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result_arr[2];
	}
	
	
	public static String[] chargeAlipay(String token,String cost,String invoiceId) {
		String url = "/ecenter/ali2!aliCharge.e";
		String params = "money="+cost+"&ty=android"+"&invoiceId="+invoiceId;
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		String[] result_list = new String[2];
		try{
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			if(status.equals(Constant.FLAG_TRADE_RESULT_SUC)){
				JSONObject jsonData = jsonObject.getJSONObject("data");
				result_list[0] = jsonData.getString("url");
				result_list[1] = jsonData.getString("invoice");
			}else{
				result_list = new String[]{"",""};
			}
		}catch(Exception e){
		}
		
		return result_list;
	}
	
	
	public static String[] cardCharge(String token,int channel,String card_num,String card_pass,String value,String invoiceId) {
		String url = "/ecenter/card!cardCharge.e";
//		String params = "channelType=12&cardAmt=0.01&cardNumber=6017299923&cardPwd=141965193105388";
		String params = "channelType="+channel+"&cardAmt="+value+"&cardNumber="+card_num+"&cardPwd="+card_pass+"&invoiceId="+invoiceId;
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		String[] result_arr = new String[2];
		try{
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			result_arr[0] = status;
			if(status.equals(Constant.FLAG_TRADE_RESULT_SUC)){
				result = jsonObject.getString("invoice");
				result_arr[1] = result;
			}else{
				result_arr[1] = jsonObject.getString("msg");
			}
		}catch(Exception e){
		}
		return result_arr;
	}
	
	/**
	 * 银联的充值。
	 * @param token
	 * @param value
	 * @param invoiceId
	 * @return
	 */
	public static String[] cardChargeYinLian(String token,String count_money,String invoiceId){
		String url = "/ecenter/uni2!uniCharge2.e";
		//	URL url = new URL(Constant.URL_YINLIAN+"?money="+count_money +"&invoiceId="+invoiceId +
		String params  =  "money="+count_money +"&invoiceId="+invoiceId ;
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		String[] result_arr = new String[13];
		try{

			JSONObject jsonObject = new JSONObject(result);
			JSONObject data = jsonObject.getJSONObject("data");
			result_arr[0] = jsonObject.getString("msg");
			result_arr[1] = jsonObject.getString("status");

			result_arr[2] = data.getString("Version"); //通讯协议版本号
			result_arr[3] = data.getString("MerchantId"); // 商户代码
			result_arr[4] = data.getString("MerchOrderId"); // 商户订单号
			result_arr[5] = data.getString("Amount"); // 商户订单金额
			
			result_arr[6] = data.getString("TradeTime");//商户订单时间

			result_arr[7] = data.getString("OrderId"); // 易联订单号
			result_arr[8] = data.getString("Sign"); // 签名

			ESPayLog.d(TAG ,"银联解析完毕。");
		}catch(Exception e){
//			e.printStackTrace();
		}
		return result_arr;
		
		
	}
	
	/**
	 * 银联的充值。
	 * @param token
	 * @param value
	 * @param invoiceId
	 * @return
	 */
	public static String[] cardChargeWeiXin(String token,String count_money,String invoiceId){
		String url = "/ecenter/wechat!weixinCharge.e";
		//	URL url = new URL(Constant.URL_YINLIAN+"?money="+count_money +"&invoiceId="+invoiceId +
		String params  =  "money="+count_money +"&invoiceId="+invoiceId ;
		ESPayLog.d(TAG ,"url:"+domain+url);
		ESPayLog.d(TAG ,"params:"+params);
		ESPayLog.d(TAG ,"token:"+token);
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		String[] result_arr = new String[13];
		try{

			JSONObject jsonObject = new JSONObject(result);
			JSONObject data = jsonObject.getJSONObject("data");
			result_arr[0] = jsonObject.getString("msg");
			result_arr[1] = jsonObject.getString("status");

			result_arr[2] = data.getString("aid"); //通讯协议版本号
			result_arr[3] = data.getString("bn"); // 商户代码
			result_arr[4] = data.getString("tid"); // 商户订单号

			ESPayLog.d(TAG ,"银联解析完毕。");
		}catch(Exception e){
//			e.printStackTrace();
		}
		return result_arr;
		
		
	}
	
	
	
	
	public static String[] tradeResult(String token,String invoice) {
		String url = "/ecenter/tradeResult.e";
//		String params = "ti=" + System.currentTimeMillis() + "&invoice=be23cc1df01f441e8ac7dbbf3bf986ad";
		String params = "ti=" + System.currentTimeMillis() + "&invoice="+invoice;
		String[] result_arr = new String[2];
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		try{
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			String msg =  jsonObject.getString("msg");
			if(status.equals("0")){
				result_arr[0] = Constant.FLAG_TRADE_RESULT_COMMIT;
			}else{
				if(status.equals("1")){
					result_arr[0] = Constant.FLAG_TRADE_RESULT_SUC;
				}else{
					result_arr[0] = Constant.FLAG_TRADE_RESULT_FAIL;
					result_arr[1] = msg;
				}
			}
		}catch(Exception e){
		}
		return result_arr;
	}
	
	
	public static LinkedList<PayItem> tradeHistory(String token, String appId, int page) {
//		String url = "/ecenter/tradeHistory.e";
		String url = "/basePay/userTradeHistory.e";
		String params = "size=10&page="+String.valueOf(page)+"&appId="+appId;
//		String result = EsPayNetGetPost.sendGet(domain + url,params,"TGT-47810-ubAKACcEV5Qm5hsOOxCTSGTYZjnQEHSGewf2Kpbe9EPqCoEA04-sso");
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		
		try{
			JSONObject jsonObject = new JSONObject(result);
			JSONObject data = jsonObject.getJSONObject("data");
			boolean hasNext = data.getBoolean("hasNext");
			
			if(data!=null){
				JSONArray jArray = data.getJSONArray("list");
				LinkedList<PayItem> list = new LinkedList<PayItem>();
				Gson gson = new Gson(); 
				PayItem[] users = gson.fromJson(jArray.toString(), PayItem[].class);
				if (hasNext == false) {
					users[0].setHasNext(false);
				}
                for (int i = 0; i < users.length; i++) {
                    list.add(users[i]);
                }
                return list;
            }

        } catch (Exception e) {
        }
        return null;
    }

    public static int isUploadPay(String userId, String appId) {
        try {
            String url = "https://listener.eayou.com/sa/todayUser.do?accountid=" + userId + "&appid=" + appId + "&t=" + System.currentTimeMillis();
            String result = EsPayNetGetPost.sendGet(url, null, "");
            //数据为null，有可能是请求出错
            if (result == null) {
                return -1;
            }

            //不上传头条付费日志
            if (result.equals("0")) {
                return 0;
            }
            //上传头条付费日志
            return 1;
        } catch (Exception e) {
            ESdkLog.d(e.toString());
            return -1;
        }
    }

}
