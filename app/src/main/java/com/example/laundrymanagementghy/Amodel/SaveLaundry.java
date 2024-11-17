package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveLaundry implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("success")
    public int success;
    @SerializedName("get_data")
    public ArrayList<UserItem> mUserItems;

    public static class UserItem implements Serializable {

        @SerializedName("sent_id")
        public String mSent_id;
        @SerializedName("date")
        public String mDate;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("sent_depot_id")
        public String mSent_depot_id;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("bs")
        public String mBs;
        @SerializedName("pc")
        public String mPc;
        @SerializedName("ft")
        public String mFt;
        @SerializedName("blanket")
        public String mBlanket;
        @SerializedName("total")
        public String mTotal;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;
        @SerializedName("created_by")
        public String mCreated_by;
        @SerializedName("created_date")
        public String mCreated_date;
        @SerializedName("updated_by")
        public String mUpdated_by;
        @SerializedName("updated_date")
        public String mUpdated_date;
    }
}
