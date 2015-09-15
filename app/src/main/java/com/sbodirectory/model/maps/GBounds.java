package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbodirectory.util.JsonUtils;

import org.json.JSONObject;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class GBounds implements Parcelable{
    public GLocation southwest;
    public GLocation northeast;
    public GBounds() {}
    public GBounds(JSONObject jso) {
        this();
        if (jso != null) {
            this.southwest = new GLocation(JsonUtils.getJSONObject(jso, "southwest"));
            this.northeast = new GLocation(JsonUtils.getJSONObject(jso, "northeast"));
        }
    }
    private GBounds(Parcel in) {
        this.southwest = in.readParcelable(GLocation.class.getClassLoader());
        this.northeast = in.readParcelable(GLocation.class.getClassLoader());
    }
    //Parceable implement
    public static final Parcelable.Creator<GBounds> CREATOR = new Parcelable.Creator<GBounds>() {
        public GBounds createFromParcel(Parcel in) {
            return new GBounds(in);
        }

        public GBounds[] newArray(int size) {
            return new GBounds[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.southwest, 0);
        dest.writeParcelable(this.northeast, 0);
    }
}
