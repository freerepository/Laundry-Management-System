package com.example.laundrymanagementghy.resoures;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class QPenaltyModel implements Serializable {
       @SerializedName("success")
       public int mStatus;
       @SerializedName("message")
       public String message;

    @SerializedName("GetPenaltyQuestions")
    public ArrayList<QueItem> mQueItems;
    public static class QueItem implements Serializable {

        @SerializedName("id")
        public String mCatId;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("category_name")
        public String mCategory_name;
        @SerializedName("question_name")
        public String mQuestion_name;
        @SerializedName("Unit")
        public String mUnit;
        @SerializedName("qty")
        public String mQty;
        @SerializedName("penalty_amount")
        public String mPenalty_amount;
        @SerializedName("shortfall")
        public String mShortfall;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("del_status")
        public String mDel_status;


    }

}
