package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetBufferIssueList implements Serializable {
//    @SerializedName("message")
//    public String message;
    @SerializedName("BufferStockData")
    public ArrayList<GetBufferIssueList.mItem> mList;

    public static class mItem implements Serializable {
        @SerializedName("id")
        public String mId;
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
//        "depot_code": "KYQ",
        @SerializedName("blanket_cover")
        public String mBlanket_cover;
        @SerializedName("depot_code")
        public String mDepotCode;

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
        @SerializedName("created_date")
        public String mCreated_date;
        @SerializedName("created_by")
        public String mCreated_by;
        @SerializedName("updated_date")
        public String mUpdated_date;
        @SerializedName("updated_by")
        public String mUpdated_by;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;

    }

}

