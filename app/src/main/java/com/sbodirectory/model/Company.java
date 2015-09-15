package com.sbodirectory.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sbodirectory.util.Config;
import com.sbodirectory.util.JsonUtils;
import com.sbodirectory.util.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class Company implements Parcelable{
    public int id;
    public String name;
    public String address;
    public String streetNumber;
    public String postalCode;
    public String city;
    public String country;
    public String phone;
    public String website;
    public Location location;
    public String thumbnail;
    public String category;

    public String getName() {
        return Utils.getText(name);
    }
    public String getAddress() {
        return Utils.getText(streetNumber) + " " + Utils.getText(address) + " " + getAddressExtra();
    }
    public String getAddressExtra() {
        return Utils.getText(city) + ", " + Utils.getText(country) + " " + Utils.getText(postalCode);
    }
    public String getCountry() {
        return Utils.getText(country);
    }
    public String getPhone() {
        return Utils.getText(phone);
    }
    public String getWebsite() {
        return Utils.getText(website);
    }

    public String getCategory(){
        return Utils.getText(category);
    }

    public Company() {}
    public Company(JSONObject jso) {
        this();
        if (jso != null) {
            this.id = JsonUtils.getIntValue(jso, "id");
            this.name = JsonUtils.getStringValue(jso, "name");
            this.address = JsonUtils.getStringValue(jso, "address");
            this.streetNumber = JsonUtils.getStringValue(jso, "street_number");
            this.postalCode = JsonUtils.getStringValue(jso, "postalCode");
            this.city = JsonUtils.getStringValue(jso, "city");
            this.country = JsonUtils.getStringValue(jso, "county");
            this.phone = JsonUtils.getStringValue(jso, "phone");
            this.website = JsonUtils.getStringValue(jso, "website");
            this.location = new Location(JsonUtils.getDoubleValue(jso, "latitude"), JsonUtils.getDoubleValue(jso, "longitude"));
            this.category = JsonUtils.getStringValue(jso, "category");
            this.thumbnail = JsonUtils.getStringValue(jso, "logoLocation");
            if (!TextUtils.isEmpty(thumbnail)) {
                this.thumbnail = Config.DOMAIN_PHOTO + Utils.getUriEncode(this.thumbnail);
            }
        }
    }

    /**
     * If return false this company can not be add to the list
     * If true it's will
     * @return the valid of Company
     */
    public boolean isValid() {
        return true;
//        return !TextUtils.isEmpty(this.thumbnail);
    }

    private Company(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.address = in.readString();
        this.streetNumber = in.readString();
        this.postalCode = in.readString();
        this.city = in.readString();
        this.country = in.readString();
        this.phone = in.readString();
        this.website = in.readString();
        this.category = in.readString();
        this.thumbnail = in.readString();
        try {
            this.location = in.readParcelable(Location.class.getClassLoader());
        } catch (Exception e){}
    }
    //Parceable implement
    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.streetNumber);
        dest.writeString(this.postalCode);
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeString(this.phone);
        dest.writeString(this.website);
        dest.writeString(this.category);
        dest.writeString(this.thumbnail);
        if (this.location != null) {
            dest.writeParcelable(this.location, 0);
        }
    }
}
