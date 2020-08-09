package com.easou.androidsdk.util;

import java.lang.reflect.Type;
import java.util.List;

import com.easou.androidsdk.data.LoginNameInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

public class GsonUtil {

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

	public static <T> T fromJson(JsonElement json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}


	public static <T> T jsonToList(String strJsonData, Class<?> cls) {
		Gson gson = new Gson();
		T t = gson.fromJson(strJsonData, new TypeToken<T>() {
		}.getType());

		return t;
	}
}
