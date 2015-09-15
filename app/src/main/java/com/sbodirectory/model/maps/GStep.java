package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbodirectory.model.Location;
import com.sbodirectory.util.JsonUtils;

import org.json.JSONObject;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class GStep implements Parcelable {
    public String travelMode;
    public GLocation startLocation;
    public GLocation endLocation;
    public GPolyline polyline;
    public GVal duration;
    public String htmlInstructions;
    public GVal distance;

    public GStep() {}
    public GStep(JSONObject jso) {
        this();
        if (jso != null) {
            this.travelMode = JsonUtils.getStringValue(jso, "travel_mode");
            this.startLocation = new GLocation(JsonUtils.getJSONObject(jso, "start_location"));
            this.endLocation = new GLocation(JsonUtils.getJSONObject(jso, "end_location"));
            this.polyline = new GPolyline(JsonUtils.getJSONObject(jso, "polyline"));
            this.duration = new GVal(JsonUtils.getJSONObject(jso, "duration"));
            this.htmlInstructions = JsonUtils.getStringValue(jso, "html_instructions");
            this.distance = new GVal(JsonUtils.getJSONObject(jso, "distance"));
        }
    }

    private GStep(Parcel in) {
        this.travelMode = in.readString();
        this.startLocation = in.readParcelable(GLocation.class.getClassLoader());
        this.endLocation = in.readParcelable(GLocation.class.getClassLoader());
        this.polyline = in.readParcelable(GPolyline.class.getClassLoader());
        this.duration = in.readParcelable(GVal.class.getClassLoader());
        this.htmlInstructions = in.readString();
        this.distance = in.readParcelable(GVal.class.getClassLoader());
    }

    //Parceable implement
    public static final Parcelable.Creator<GStep> CREATOR = new Parcelable.Creator<GStep>() {
        public GStep createFromParcel(Parcel in) {
            return new GStep(in);
        }

        public GStep[] newArray(int size) {
            return new GStep[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.travelMode);
        dest.writeParcelable(this.startLocation, 0);
        dest.writeParcelable(this.endLocation, 0);
        dest.writeParcelable(this.polyline, 0);
        dest.writeParcelable(this.duration, 0);
        dest.writeString(this.htmlInstructions);
        dest.writeParcelable(this.distance, 0   );
    }
}