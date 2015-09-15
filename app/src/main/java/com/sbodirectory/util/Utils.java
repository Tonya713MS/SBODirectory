package com.sbodirectory.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class Utils {
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

    public static String getText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return text;
    }
    public static URI getServerPage(String api) {
        try {
            return new URI(Config.SCHEME, Config.DOMAIN, api, null);
        }catch (URISyntaxException e) {}
        return null;
    }
    public static URI getServerPage(String api, String query) {
        try {
//            Uri.Builder builder = new Uri.Builder().scheme(Config.SCHEME).authority(Config.DOMAIN).path(api);
//            if (params != null && params.length > 0) {
//                for (BasicNameValuePair param : params) {
//                    builder.appendQueryParameter(param.getName(), param.getValue());
//                }
//            }
//            new URI()
            return new URI(Config.SCHEME, Config.DOMAIN, api, query, null);
        }catch (URISyntaxException e) {}
        return null;
    }
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    public static String getUriEncode(String url) {
        String urlEncoded = Uri.encode(url, ALLOWED_URI_CHARS);
        return urlEncoded;
    }

    public static boolean checkGPSLocatorEnalable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public static float convertPxToDp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float ret = (px) / scale;
        return ret;
    }
    public static float convertDpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float ret = dp * scale;;
        return ret;
    }
    public static float convertSpToPx(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        float ret = sp * scale;
        return ret;
    }
    public static void setLogin(Context context, boolean logined) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isLogined", logined).commit();
    }
    public static boolean isLogined(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isLogined", false);
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
    public static double convertMileToKM(double miles) {
        return miles * 1.609344;
    }
    public static double convertKMToMile(double kilometers) {
        return kilometers * 1.609344;
    }
}
