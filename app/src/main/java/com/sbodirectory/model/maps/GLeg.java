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
public class GLeg implements Parcelable{
    public List<GStep> steps;
    public GVal duration;
    public GVal distance;
    public GLocation startLocation;
    public GLocation endLocation;
    public String startAddress;
    public String endAddress;
    public GLeg() {}
    public GLeg(JSONObject jso) {
        this();
        if (jso != null) {
            JSONArray jsAs = JsonUtils.getJSONArray(jso, "steps");
            final int count = jsAs.length();
            steps = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                try {
                    JSONObject jsoStep = jsAs.getJSONObject(i);
                    steps.add(new GStep(jsoStep));
                } catch (Exception e){}
            }
            this.duration = new GVal(JsonUtils.getJSONObject(jso, "duration"));
            this.distance = new GVal(JsonUtils.getJSONObject(jso, "distance"));
            this.startLocation = new GLocation(JsonUtils.getJSONObject(jso, "start_location"));
            this.endLocation = new GLocation(JsonUtils.getJSONObject(jso, "end_location"));
            this.startAddress = JsonUtils.getStringValue(jso, "start_address");
            this.endAddress = JsonUtils.getStringValue(jso, "end_address");
        }
    }
    public GLeg(Parcel in) {
//        this.steps = (GStep[])in.readParcelableArray(GStep.class.getClassLoader());
//        in.readTypedArray(steps, GStep.CREATOR);
        this.steps = new ArrayList<GStep>();
        in.readList(this.steps, GStep.class.getClassLoader());
        this.duration = in.readParcelable(GVal.class.getClassLoader());
        this.distance = in.readParcelable(GVal.class.getClassLoader());
        this.startLocation = in.readParcelable(GLocation.class.getClassLoader());
        this.endLocation = in.readParcelable(GLocation.class.getClassLoader());
        this.startAddress = in.readString();
        this.endAddress = in.readString();
    }
    //Parceable implement
    public static final Parcelable.Creator<GLeg> CREATOR = new Parcelable.Creator<GLeg>() {
        public GLeg createFromParcel(Parcel in) {
            return new GLeg(in);
        }

        public GLeg[] newArray(int size) {
            return new GLeg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(steps);
        dest.writeParcelable(this.duration, 0);
        dest.writeParcelable(this.distance, 0);
        dest.writeParcelable(this.startLocation, 0);
        dest.writeParcelable(this.endLocation, 0);
        dest.writeString(this.startAddress);
        dest.writeString(this.endAddress);
    }

    public PolylineOptions getPolylineOptions() {
        PolylineOptions ret = null;
        if (steps != null) {
            for (GStep st : steps) {
                if (st.polyline != null) {
                    if (ret == null) ret = new PolylineOptions();
                    ret.addAll(st.polyline.getPoints());
                }
            }
        }
        return ret;
    }

    /**
     * Gets total duration in second
     * @return
     */
    public double getTotalDuration() {
        double ret = 0;
        if (steps != null) {
            for (GStep st : steps) {
                if (st.duration != null)
                    ret += st.duration.value;
            }
        }
        return ret;
    }

    /**
     * Gets total distance in m
     * @return
     */
    public float getTotalDistance() {
        float ret = 0;
        if (steps != null) {
            for (GStep st : steps) {
                if (st.distance != null)
                    ret += st.distance.value;
            }
        }
        return ret;
    }
}
