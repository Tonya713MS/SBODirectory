package com.sbodirectory.model.maps;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sbodirectory.model.Location;
import com.sbodirectory.util.Config;
import com.sbodirectory.util.JsonUtils;
import com.sbodirectory.util.Utils;

import org.json.JSONObject;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class GVal implements Parcelable{
    public double value;
    public String text;
    public GVal() {}
    public GVal(JSONObject jso) {
        this();
        if (jso != null) {
            this.value = JsonUtils.getDoubleValue(jso, "value");
            this.text = JsonUtils.getStringValue(jso, "text");
        }
    }
    public GVal(double value, String text) {
        this.value = value;
        this.text = text;
    }
    private GVal(Parcel in) {
        this.value = in.readDouble();
        this.text = in.readString();
    }
    //Parceable implement
    public static final Parcelable.Creator<GVal> CREATOR = new Parcelable.Creator<GVal>() {
        public GVal createFromParcel(Parcel in) {
            return new GVal(in);
        }

        public GVal[] newArray(int size) {
            return new GVal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.value);
        dest.writeString(this.text);
    }
}
