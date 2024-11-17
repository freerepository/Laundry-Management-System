package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDataModel implements Serializable {

    @SerializedName("success")
    public int mStatus;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public String status;
    @SerializedName("user_data")
    public ArrayList<UserItem> mUserItems;

    public static class UserItem implements Serializable {
        @SerializedName("login_id")
        public String mLogin_id;
        @SerializedName("laundryID")
        public String mLaundryID;
        @SerializedName("name")
        public String mName;
        @SerializedName("area_name")
        public String mArea_name;
        @SerializedName("depot_name")
        public String mDepot_name;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("username")
        public String mUsername;
        @SerializedName("password")
        public String mPassword;
        @SerializedName("type")
        public String mType;
        @SerializedName("Header")
        public String mHeader;
    }
}
