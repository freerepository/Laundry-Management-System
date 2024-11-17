package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetPenaltyList implements Serializable {
//    @SerializedName("message")
//    public String message;
    @SerializedName("PenaltyData")
    public ArrayList<GetPenaltyList.mItem> mList;

    public static class mItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("supervisor_id")
        public String mSupervisor_id;
        @SerializedName("depot_id")
        public String mDepot_id;
        @SerializedName("penalty_from")
        public String mPenalty_from;
        @SerializedName("total_penalty")
        public String mTotal_penalty;
        @SerializedName("penalty_date")
        public String mPenalty_date;

        @SerializedName("quest_id")
        public String mQuest_id;
        @SerializedName("qty")
        public String mQty;
        @SerializedName("amount")
        public String mAmount;
        @SerializedName("remark")
        public String mRemark;
        @SerializedName("signature")
        public String mSignature;


    }

}

