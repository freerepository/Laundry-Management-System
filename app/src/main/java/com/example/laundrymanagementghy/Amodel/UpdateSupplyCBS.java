package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateSupplyCBS implements Serializable{

    @SerializedName("GetstoreData")
    public ArrayList<UpdateItem> mUpdateItem;

    public static class UpdateItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("received_from")
        public String mReceived_from;
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

        @SerializedName("storeData")
        public ArrayList<storeData> mStoreData;
        public static class storeData implements Serializable{
            @SerializedName("id")
            public String mId;
            @SerializedName("pl_no")
            public String mPl_no;
            @SerializedName("qty")
            public String mQty;
            @SerializedName("item_description")
            public String mItem_description;

       }

   }
}
