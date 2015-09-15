package com.sbodirectory.controller;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.sbodirectory.model.Category;
import com.sbodirectory.model.Company;
import com.sbodirectory.util.Config;
import com.sbodirectory.util.ServiceConnection;
import com.sbodirectory.util.Utils;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class FeatureCompanyLoader extends BaseCompanyLoader {
    private Category mSelectedCategory;

    public FeatureCompanyLoader(Context context) {
        super(context);
    }
    public FeatureCompanyLoader(Context context, Category category) {
        this(context);
        mSelectedCategory = category;
    }
    public void setSelectedCategory(Category category) {
        mSelectedCategory = category;
    }
    protected String getResult(ServiceConnection connection) {
//        final String serverPage = (mSelectedCategory != null) ? Config.API_FEATURE_COMPANY + "?company_id=" + mSelectedCategory : Config.API_FEATURE_COMPANY;
//        URI uri = Utils.getServerPage(serverPage);
        URI uri = null;
        final String domain = Config.SCHEME + "://" + Config.DOMAIN + Config.API_FEATURE_COMPANY;
        final String serverPage = (mSelectedCategory != null) ? domain + "?category_id=" + mSelectedCategory.id : domain;
        Log.i("SBOLoader", "serverPage: " + serverPage);
        try {
            uri = new URI(serverPage);
        } catch (URISyntaxException e) {
            uri = Utils.getServerPage(Config.API_FEATURE_COMPANY);
        }
        String result = connection.get(uri);
        return result;
    }
}
