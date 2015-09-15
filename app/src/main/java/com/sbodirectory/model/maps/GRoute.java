package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.PolylineOptions;
import com.sbodirectory.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class GRoute implements Parcelable{
    public String sumary;
    public List<GLeg> legs;
    public GBounds bounds;
    public GPolyline overviewPolyline;
    public GRoute() {}
    public GRoute(JSONObject jso) {
        this();
        if (jso != null) {
            this.sumary = JsonUtils.getStringValue(jso, "summary");

            JSONArray jsAs = JsonUtils.getJSONArray(jso, "legs");
            final int count = jsAs.length();
            legs = new ArrayList<GLeg>();
            for (int i = 0; i < count; i++) {
                try {
                    JSONObject jsoLeg = jsAs.getJSONObject(i);
                    legs.add(new GLeg(jsoLeg));
                } catch (Exception e){}
            }

            this.bounds = new GBounds(JsonUtils.getJSONObject(jso, "bounds"));
            this.overviewPolyline = new GPolyline(JsonUtils.getJSONObject(jso, "overview_polyline"));
        }
    }
    private GRoute(Parcel in) {
        this.sumary = in.readString();
        this.legs = new ArrayList<GLeg>();
        in.readList(this.legs, GLeg.class.getClassLoader());
        this.bounds = in.readParcelable(GBounds.class.getClassLoader());
        this.overviewPolyline = in.readParcelable(GPolyline.class.getClassLoader());

    }
    //Parceable implement
    public static final Parcelable.Creator<GRoute> CREATOR = new Parcelable.Creator<GRoute>() {
        public GRoute createFromParcel(Parcel in) {
            return new GRoute(in);
        }

        public GRoute[] newArray(int size) {
            return new GRoute[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sumary);
        dest.writeList(this.legs);
        dest.writeParcelable(this.bounds, 0);
        dest.writeParcelable(this.overviewPolyline, 0);
    }

    public PolylineOptions getPolylineOptions() {
        PolylineOptions ret = null;
        if (legs != null) {
            for (GLeg st : legs) {
                PolylineOptions p = st.getPolylineOptions();
                if (p != null) {
                    if (ret == null) ret = new PolylineOptions();
                    ret.addAll(p.getPoints());
                }
            }
        }
        return ret;
    }

    public PolylineOptions getOverviewPolylineOptions() {
        PolylineOptions ret = null;
        if (overviewPolyline != null) {
            if (ret == null) ret = new PolylineOptions();
            ret.addAll(overviewPolyline.getPoints());
        }
        return ret;
    }

    /**
     * Gets total duration in minutes
     * @return
     */
    public double getTotalDuration() {
        double ret = 0;
        if (legs != null) {
            for (GLeg st : legs) {
                ret += st.getTotalDuration();
            }
        }
        return ret;
    }
    public String getTotalDurationString() {
        double totalDuration = getTotalDuration();
        int hours = (int) (totalDuration / 3600);
        int minutes = (int) (totalDuration - hours * 3600) / 60;
        int seconds = (int) (totalDuration - 3600 * hours - 60 * minutes);
        if (hours > 0) {
            if (minutes > 0) {
                return hours + " hours " + minutes + " minutes";
            } else {
                return hours + " hours ";
            }
        } else if (minutes > 0) {
            return minutes + " minutes";
        } else {
            return seconds + " seconds";
        }
    }

    /**
     * Gets total distance in m
     * @return
     */
    public float getTotalEstimatedDistance() {
        float ret = 0;
        if (legs != null) {
            for (GLeg st : legs) {
                ret += st.getTotalDistance();
            }
        }
        return ret;
    }
}
