package com.easou.androidsdk.login.util;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.easou.androidsdk.login.service.JBean;
import com.easou.androidsdk.login.service.JBody;
import com.easou.androidsdk.login.service.JDesc;
import com.easou.androidsdk.login.service.JHead;
import com.easou.androidsdk.login.service.JReason;
import com.easou.androidsdk.login.service.MoneyGroupInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

	private static Type headType = new TypeToken<JHead>() {}.getType();
	private static Type bodyType = new TypeToken<Map<String, JsonElement>>() {}.getType();
	private static Type descType = new TypeToken<List<JReason>>() {}.getType();
	private static Type beanType = new TypeToken<Map<String, JsonElement>>() {}.getType();
	
	private static Gson gson = new GsonBuilder()
			.registerTypeAdapter(Object.class, new NaturalDeserializer())
			.registerTypeAdapter(JsonElement.class, new JElementDeserializer())
			.create();

	public static String toJson(Object src) {
		return gson.toJson(src);
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	public static <T> T fromJson(String json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static <T> T fromJson(JsonElement json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	public static List<MoneyGroupInfo> getList(String data) {
		return gson.fromJson(data, new TypeToken<List<MoneyGroupInfo>>() {
		}.getType());
	}

	public static <T> T fromJson(JsonElement json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static JBean extraJsonBean(String json) {
		JBean jbean = new JBean();
		try {
			Map<String, JsonElement> beanMap = GsonUtil.fromJson(json, beanType);
			JsonElement headElement = beanMap.get("head");
			JHead head = GsonUtil.fromJson(headElement, headType);
			jbean.setHead(head);
			JsonElement descElement = beanMap.get("desc");
			LinkedList<JReason> descList = GsonUtil.fromJson(descElement, descType);
			if (descList == null)
				descList = new LinkedList<JReason>();
			JDesc desc = new JDesc();
			for (Iterator<JReason> iterator = descList.iterator(); iterator.hasNext(); ) {
				JReason jReason = iterator.next();
				desc.add(jReason);
			}
			jbean.setDesc(desc);
			JsonElement bodyElement = beanMap.get("body");
			Map<String, JsonElement> bodyMap = GsonUtil.fromJson(bodyElement, bodyType);
			JBody body = new JBody();
			if(null!=bodyMap) {
				body.putAll(bodyMap);
			}
			jbean.setBody(body);
			
		}catch(Exception e){
			return jbean;
		}
		return jbean;
	}
}
