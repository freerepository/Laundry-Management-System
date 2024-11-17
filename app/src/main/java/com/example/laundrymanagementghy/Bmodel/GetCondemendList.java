package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetCondemendList implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("GetData")
    public ArrayList<GetCondemendList.mStoreItem> mList;

    public static class mStoreItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("submission_date")
        public String mSubmission_date;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("laundry")
        public String mLaundry;
        @SerializedName("depot_name")
        public String mDepot_name;
        @SerializedName("delivery_status")
        public String mDelivery_status;
    }

}

