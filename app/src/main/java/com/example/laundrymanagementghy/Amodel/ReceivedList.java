package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ReceivedList implements Serializable{
    public static Object TrainItem;
    @SerializedName("message")
    public String message;
    @SerializedName("Getreceived_data")
    public ArrayList<TrainItem> mTrainList;

    public static class TrainItem implements Serializable {
        @SerializedName("id")
        public String mReceived_id;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("date")
        public String mDate;
        @SerializedName("train_id")
        public String mTrain_id;

        @SerializedName("coach")
        public String mCoach;
        @SerializedName("no_of_bag")
        public String mNo_of_bag;
        @SerializedName("bs_first_ac")
        public String mBs_first_ac;
        @SerializedName("bs")
        public String mBs;
        @SerializedName("pc_first_ac")
        public String mPc_first_ac;
        @SerializedName("pc")
        public String mPc;
        @SerializedName("ft")
        public String mFt;
        @SerializedName("blanket")
        public String mBlanket;
        @SerializedName("bath_towel")
        public String mBath_towel;
        @SerializedName("blanket_cover")
        public String mBlanket_cover;
        @SerializedName("total")
        public String mTotal;
        @SerializedName("unused_packet")
        public String mUnused_packet;
        @SerializedName("remark")
        public String mRemark;
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
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("laundry_name")
        public String mLaundry_name;

    }

}
