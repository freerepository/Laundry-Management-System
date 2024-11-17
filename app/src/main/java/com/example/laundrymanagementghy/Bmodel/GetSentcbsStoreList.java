package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetSentcbsStoreList implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("GetstoreData")
    public ArrayList<GetSentcbsStoreList.mStoreItem> mList;

    public static class mStoreItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("submission_date")
        public String mSubmission_date;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("store")
        public String mStore;
        @SerializedName("received_from")
        public String mReceived_from;
        @SerializedName("depot_name")
        public String mDepot_name;
        @SerializedName("delivery_status")
        public String mDelivery_status;
    }

}

