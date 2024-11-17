package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetReceivedStoreList implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("GetstoreData")

    public ArrayList<mReceivedStoreItem> mReceivedStoreList;

    public static class mReceivedStoreItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("store_id")
        public String mStore_id;
        @SerializedName("submission_date")
        public String mSubmission_date;
        @SerializedName("give_to")
        public String mGive_to;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("store_name")
        public String mStore_name;
        @SerializedName("laundry_name")
        public String mLaundry_name;
        @SerializedName("delivery_status")
        public String mDelivery_status;


        @SerializedName("storeData")
        public ArrayList<ReceivedStore> mStores;
        public static class ReceivedStore implements Serializable{

            @SerializedName("item_description")
            public String mItem_description;
            @SerializedName("pl_no")
            public String mPl_no;
            @SerializedName("qty")
            public String mQty;

        }

    }

}
