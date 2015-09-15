package com.sbodirectory.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sbodirectory.util.Config;
import com.sbodirectory.util.JsonUtils;
import com.sbodirectory.util.Utils;

import org.json.JSONObject;

/**
 * Created by AnhNDT on 4/26/15.
 */
public class Category extends MenuItem implements Parcelable {
    public int id;
    public int parentId;
    public String name;
    public int level;

    public int getMenuId() {
        return id;
    }
    public String getMenuValue() {
        return Utils.getText(name);
    }

    public Category() {
    }

    public Category(JSONObject jso) {
        this();
        if (jso != null) {
            this.id = JsonUtils.getIntValue(jso, "id");
            this.parentId = JsonUtils.getIntValue(jso, "parent_id");
            this.name = JsonUtils.getStringValue(jso, "name");
            this.level = JsonUtils.getIntValue(jso, "level");
        }
    }

    private Category(Parcel in) {
        this.id = in.readInt();
        this.parentId = in.readInt();
        this.name = in.readString();
        this.level = in.readInt();
    }

    //Parceable implement
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.parentId);
        dest.writeString(this.name);
        dest.writeInt(this.level);
    }
}