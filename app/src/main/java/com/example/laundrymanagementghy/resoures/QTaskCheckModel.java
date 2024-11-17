package com.example.laundrymanagementghy.resoures;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class QTaskCheckModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("laundry_items")
    public ArrayList<QueItem> mItemList;

    public static class QueItem implements Serializable {

        @SerializedName("id")
        public String mCatId;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("item_name")
        public String mItem_name;
        @SerializedName("item_no")
        public String mItem_no;
        @SerializedName("wmi")
        public String mWmi;

    }
}