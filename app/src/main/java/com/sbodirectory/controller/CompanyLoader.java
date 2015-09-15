package com.sbodirectory.controller;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.sbodirectory.model.Category;
import com.sbodirectory.model.Company;
import com.sbodirectory.util.Config;
import com.sbodirectory.util.ServiceConnection;
import com.sbodirectory.util.Utils;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class CompanyLoader extends BaseCompanyLoader {
    private Location mMyLocation;
    private Category mSelectedCategory;

    public CompanyLoader(Context context, Location myLocation) {
        super(context);
        this.mMyLocation = myLocation;
    }
    public CompanyLoader(Context context, Location myLocation, Category category) {
        this(context, myLocation);
        mSelectedCategory = category;
    }
    public void setSelectedCategory(Category category) {
        mSelectedCategory = category;
    }
    protected String getResult(ServiceConnection connection) {
        String result = null;

        URI uri = null;
        if (mMyLocation == null) {
            final String domain = Config.SCHEME + "://" + Config.DOMAIN + Config.API_LIST_COMPANY;
            final String serverPage = (mSelectedCategory != null) ? domain + "?category_id=" + mSelectedCategory.id : domain;
            Log.i("SBOLoader", "serverPage: " + serverPage);
            try {
                uri = new URI(serverPage);
            } catch (URISyntaxException e) {
                uri = Utils.getServerPage(Config.API_LIST_COMPANY);
            }
//            uri = Utils.getServerPage(Config.API_LIST_COMPANY);
            result = connection.get(uri);
        } else {
            uri = Utils.getServerPage(Config.API_FIND_NEAR);
            Log.i("SBOLoader", "serverPage: " + uri);
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("latitude", "" + mMyLocation.getLatitude()));
            params.add(new BasicNameValuePair("longitude", "" + mMyLocation.getLongitude()));
            if(mSelectedCategory != null) {
                params.add(new BasicNameValuePair("category_id", "" + mSelectedCategory.getMenuId()));
            }
            result = connection.post(uri, params);
        }

        return result;
    }
}
