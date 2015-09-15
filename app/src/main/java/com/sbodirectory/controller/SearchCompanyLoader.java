package com.sbodirectory.controller;

import android.content.Context;
import android.text.TextUtils;

import com.sbodirectory.util.Config;
import com.sbodirectory.util.ServiceConnection;
import com.sbodirectory.util.Utils;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class SearchCompanyLoader extends BaseCompanyLoader {
    private String mKeySearch;
    public SearchCompanyLoader(Context context, String keySearch) {
        super(context);
        mKeySearch = keySearch;
    }
    protected String getResult(ServiceConnection connection) {
        if (TextUtils.isEmpty(mKeySearch)) return null;
        URI uri = Utils.getServerPage(Config.API_SEARCH);
        String result = connection.post(uri, new BasicNameValuePair("key", mKeySearch));
        return result;
    }
}
