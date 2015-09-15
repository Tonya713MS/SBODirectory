package com.sbodirectory.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class JsonUtils {
    public static String getStringValue(JSONObject jso, String key) {
        try {
            return jso.getString(key);
        } catch (Exception e){}
        return null;
    }
    public static int getIntValue(JSONObject jso, String key) {
        try {
            return jso.getInt(key);
        } catch (Exception e){}
        return -1;
    }
    public static double getDoubleValue(JSONObject jso, String key) {
        try {
            return jso.getDouble(key);
        } catch (Exception e){}
        return -1.f;
    }
    public static JSONObject getJSONObject(JSONObject jso, String key) {
        try {
            return jso.getJSONObject(key);
        } catch (Exception e){}
        return null;
    }
    public static JSONArray getJSONArray (JSONObject jso, String key) {
        try {
            return jso.getJSONArray(key);
        } catch (Exception e){}
        return null;
    }
}
