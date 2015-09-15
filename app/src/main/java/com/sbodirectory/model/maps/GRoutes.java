package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbodirectory.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class GRoutes implements Parcelable{
    public String status;
    public List<GRoute> routes;
    public GRoutes() {}
    public GRoutes(JSONObject jso) {
        this();
        if (jso != null) {
            this.status = JsonUtils.getStringValue(jso, "status");
            JSONArray jsAs = JsonUtils.getJSONArray(jso, "routes");
            final int count = jsAs.length();
            routes = new ArrayList<GRoute>(count);
            for (int i = 0; i < count; i++) {
                try {
                    JSONObject jsoStep = jsAs.getJSONObject(i);
                    routes.add(new GRoute(jsoStep));
                } catch (Exception e){}
            }
        }
    }

    private GRoutes(Parcel in) {
        this.status = in.readString();
        this.routes = new ArrayList<GRoute>();
        in.readList(this.routes, GRoute.class.getClassLoader());
    }
    //Parceable implement
    public static final Parcelable.Creator<GRoutes> CREATOR = new Parcelable.Creator<GRoutes>() {
        public GRoutes createFromParcel(Parcel in) {
            return new GRoutes(in);
        }

        public GRoutes[] newArray(int size) {
            return new GRoutes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeList(routes);
    }
}
