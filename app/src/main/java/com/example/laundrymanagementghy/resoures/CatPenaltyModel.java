package com.example.laundrymanagementghy.resoures;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CatPenaltyModel implements Serializable {
    /*
    @SerializedName("success")
    public int mStatus;
    @SerializedName("message")
    public String message;
     */
    @SerializedName("penaltyCategory")
    public ArrayList<CatItem> mCatItems;
    public static class CatItem implements Serializable {

        @SerializedName("id")
        public String mCatId;
        @SerializedName("laundry_name")
        public String mCat_title;
        @SerializedName("laundry_code")
        public String mLaundry_code;
        @SerializedName("laundry_short_name")
        public String mLaundry_short_name;

    }

}
