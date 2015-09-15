package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.sbodirectory.util.JsonUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class GPolyline implements Parcelable {
    public String points;

    public GPolyline() {
    }

    public GPolyline(JSONObject jso) {
        this();
        if (jso != null) {
            this.points = JsonUtils.getStringValue(jso, "points");
        }
    }

    public GPolyline(String points) {
        this.points = points;
    }

    private GPolyline(Parcel in) {
        this.points = in.readString();
    }

    //Parceable implement
    public static final Parcelable.Creator<GPolyline> CREATOR = new Parcelable.Creator<GPolyline>() {
        public GPolyline createFromParcel(Parcel in) {
            return new GPolyline(in);
        }

        public GPolyline[] newArray(int size) {
            return new GPolyline[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.points);
    }

    public static ArrayList<LatLng> getPoints(String encodedPoints) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encodedPoints.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public ArrayList<LatLng> getPoints() {
        return getPoints(points);
    }
}