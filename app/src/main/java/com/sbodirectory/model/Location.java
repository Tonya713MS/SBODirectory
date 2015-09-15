package com.sbodirectory.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sbodirectory.util.Config;
import com.sbodirectory.util.JsonUtils;
import com.sbodirectory.util.Utils;

import org.json.JSONObject;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class Location implements Parcelable{
    public double latitude;
    public double longitude;
    public Location() {};
    public Location(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }
    public Location(JSONObject jso) {
        this();
        if (jso != null) {
            this.latitude = JsonUtils.getDoubleValue(jso, "latitude");
            this.longitude = JsonUtils.getDoubleValue(jso, "longitude");
        }
    }
    public boolean isValid() {
        return this.latitude != 0 || this.longitude != 0;
    }

    private Location(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }
    //Parceable implement
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }
}
