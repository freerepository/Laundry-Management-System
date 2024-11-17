package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BufferReceivedModel implements Serializable{
//    @SerializedName("message")
//    public String message;
    @SerializedName("BufferStockData")
    public ArrayList<ReceivedItem> mReceivedList;

    public static class ReceivedItem implements Serializable {
        @SerializedName("id")
        public String mSupply_id;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("supervisor_id")
        public String mSupervisor_id;
        @SerializedName("depot_id")
        public String mDepot_id;
        @SerializedName("bed_sheet")
        public String mBed_sheet;
        @SerializedName("pillow")
        public String mPillow;
        @SerializedName("pillow_cover")
        public String mPillow_cover;
        @SerializedName("blanket")
        public String mBlanket;
        @SerializedName("blanket_cover")
        public String mBlanket_cover;
        @SerializedName("hand_towel")
        public String mHand_towel;
        @SerializedName("submission_date")
        public String mSubmission_date;
        @SerializedName("signature")
        public String mSignature;
        @SerializedName("reason")
        public String mReason;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("created_by")
        public String mCreated_by;
        @SerializedName("created_date")
        public String mCreated_date;
        @SerializedName("updated_by")
        public String mUpdated_by;
        @SerializedName("updated_date")
        public String mUpdated_date;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;
        @SerializedName("laundry_name")
        public String mLaundry_name;
//        @SerializedName("delivery_status")
//        public String mDelivery_status;

    }

}
