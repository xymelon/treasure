package com.xycoding.treasure.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class JsonUtils {

    public static <T> T getObj(String jsonString, Class<T> cls) {
        try {
            return new Gson().fromJson(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getObj(String jsonString, TypeToken<T> token) {
        try {
            return new Gson().fromJson(jsonString, token.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getObj(JSONObject jsonObject, Class<T> cls) {
        try {
            return new Gson().fromJson(jsonObject.toString(), cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> getList(String jsonString, Class<T> cls) {
        try {
            return new Gson().fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> getList(JSONArray jsonArray, Class<T> cls) {
        try {
            return new Gson().fromJson(jsonArray.toString(), new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String getString(T json, Class<T> cls) {
        try {
            return new Gson().toJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String getString(List<T> list, Class<T> cls) {
        try {
            return new Gson().toJson(list, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> JSONObject getJSObj(T json, Class<T> cls) {
        try {
            String jsonString = new Gson().toJson(json, cls);
            return new JSONObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> JSONArray getJSArray(List<T> list, Class<T> cls) {
        try {
            String jsonString = new Gson().toJson(list, new TypeToken<List<T>>() {
            }.getType());
            return new JSONArray(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}