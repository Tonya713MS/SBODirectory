package com.sbodirectory.controller;

import android.util.Log;

import com.sbodirectory.model.MyResponse;
import com.sbodirectory.model.User;
import com.sbodirectory.util.Config;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserControler {
	String path = Config.SCHEME + "://" + Config.DOMAIN;
	String resStr = "";
	MyResponse myResponse = new MyResponse();
	public MyResponse logIn(User user) {
        String path = Config.SCHEME + "://" + Config.DOMAIN;
		path = path + Config.API_LOGIN +  "?username=" + user.getUserName() + "&password=" + user.getPassword();
	    HttpClient client = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
	    HttpResponse response;
	    try {
	        HttpGet post = new HttpGet(path);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", user.getUserName()));
            nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
            //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        response = client.execute(post);
	        resStr = EntityUtils.toString(response.getEntity(), "UTF-8");
	        JSONObject jObject = new JSONObject(resStr);
            boolean error = jObject.getBoolean("error");
            if(error){
            	myResponse.setError(error);
            	myResponse.setMessage(jObject.getString("message"));
            } else {
            	myResponse.setError(error);
            	user.setName(jObject.getString("name"));
            	user.setEmail(jObject.getString("email"));
            	myResponse.setUser(user);
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	        myResponse.setError(true);
        	myResponse.setMessage("Error!");
	    }
	    
		return myResponse;
	}
	
	public MyResponse signUp(User user) {
        String path = Config.SCHEME + "://" + Config.DOMAIN;
        path = path + Config.API_REGISTER;
	    HttpClient client = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
	    HttpResponse response;
	    try {
	        HttpPost post = new HttpPost(path);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", user.getUserName()));
            nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
            nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
            nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        response = client.execute(post);
	        resStr = EntityUtils.toString(response.getEntity(), "UTF-8");
	        JSONObject jObject = new JSONObject(resStr);
            boolean error = jObject.getBoolean("error");
            if(error){
            	myResponse.setError(error);
            	myResponse.setMessage(jObject.getString("message"));
            } else {
            	myResponse.setError(error);
            	myResponse.setUser(user);
			}
	    } catch (Exception e) {
            e.printStackTrace();
            myResponse = logIn(user);
	    }
	    
		return myResponse;
	}

}
