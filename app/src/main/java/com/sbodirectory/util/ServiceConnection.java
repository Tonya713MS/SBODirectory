package com.sbodirectory.util;

import android.net.ParseException;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class ServiceConnection {
    public static final String CANNOT_CONNNECT_SERVER = "can_not_connect_to_server";

    private String handleResponse(HttpResponse response) throws IOException{
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            //error
            return CANNOT_CONNNECT_SERVER;
        }
        HttpEntity entity = response.getEntity();
        String ret = EntityUtils.toString(entity, "utf-8");
        return ret;
    }

    private HttpResponse doPost(URI serverPage, List<BasicNameValuePair> params) throws IOException {
        HttpResponse response = null;

        DefaultHttpClient httpclient = getNewHttpClient();

        HttpContext localContext = new BasicHttpContext();
        HttpPost httppost = new HttpPost(serverPage);
        if (params != null && params.size() > 0) {
            httppost.setEntity(
                new UrlEncodedFormEntity(params, "utf-8")
            );
        }
        response = httpclient.execute(httppost, localContext);

        return response;
    }

    public String post(URI serverPage, List<BasicNameValuePair> ps) {
        try {
            HttpResponse response = this.doPost(serverPage, ps);
            return handleResponse(response);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String post(URI serverPage, BasicNameValuePair... params) {
        List<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
        if (params != null && params.length > 0) {
            for (BasicNameValuePair p : params) {
                ps.add(p);
            }
        }
        return post(serverPage, ps);
    }

    private HttpResponse doGet(URI serverPage) throws IOException {
        HttpResponse response = null;

        DefaultHttpClient httpclient = getNewHttpClient();

        HttpContext localContext = new BasicHttpContext();
        HttpGet httpget = new HttpGet(serverPage);
        response = httpclient.execute(httpget, localContext);

        return response;
    }
    public String get(URI serverPage) {
        try {
            HttpResponse response = this.doGet(serverPage);
            return handleResponse(response);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private DefaultHttpClient getNewHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        return httpClient;
    }
}
