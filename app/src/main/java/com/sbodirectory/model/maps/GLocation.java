package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbodirectory.util.JsonUtils;

import org.json.JSONObject;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class GLocation implements Parcelable{
    public double latitude;
    public double longitude;
    public GLocation() {};
    public GLocation(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }
    public GLocation(JSONObject jso) {
        this();
        if (jso != null) {
            this.latitude = JsonUtils.getDoubleValue(jso, "latitude");
            this.longitude = JsonUtils.getDoubleValue(jso, "longitude");
        }
    }
    public boolean isValid() {
        return this.latitude > 0 || this.longitude > 0;
    }

    private GLocation(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }
    //Parceable implement
    public static final Creator<GLocation> CREATOR = new Creator<GLocation>() {
        public GLocation createFromParcel(Parcel in) {
            return new GLocation(in);
        }

        public GLocation[] newArray(int size) {
            return new GLocation[size];
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
