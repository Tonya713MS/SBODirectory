package com.sbodirectory.controller;

import android.content.Context;
import android.location.Location;

import com.sbodirectory.model.Category;
import com.sbodirectory.model.Company;
import com.sbodirectory.util.Config;
import com.sbodirectory.util.ServiceConnection;
import com.sbodirectory.util.Utils;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class FindNearWithDistanceCompanyLoader extends BaseCompanyLoader {
    private Location mMyLocation;
    private double minKm, maxKm;
    private Category mSelectedCategory;

    public FindNearWithDistanceCompanyLoader(Context context, Location myLocation, double minKm, double maxKm) {
        super(context);
        this.minKm = minKm;
        this.maxKm = maxKm;
        this.mMyLocation = myLocation;
    }
    public FindNearWithDistanceCompanyLoader(Context context, Location myLocation, double minKm, double maxKm, Category category) {
        this(context, myLocation, minKm, maxKm);
        mSelectedCategory = category;
    }
    public void setSelectedCategory(Category category) {
        mSelectedCategory = category;
    }
    protected String getResult(ServiceConnection connection) {
        if (mMyLocation == null) return null;
        URI uri = Utils.getServerPage(Config.API_FIND_NEAR);
        List<BasicNameValuePair> params = new ArrayList<>();
        if(mSelectedCategory != null) {
            params.add(new BasicNameValuePair("category_id", "" + mSelectedCategory.getMenuId()));
        }
        params.add(new BasicNameValuePair("latitude", "" + mMyLocation.getLatitude()));
        params.add(new BasicNameValuePair("longitude", "" + mMyLocation.getLongitude()));
        params.add(new BasicNameValuePair("min", "" + minKm));
        params.add(new BasicNameValuePair("max", "" + maxKm));
        String result = connection.post(uri, params);
        return result;
    }
}
