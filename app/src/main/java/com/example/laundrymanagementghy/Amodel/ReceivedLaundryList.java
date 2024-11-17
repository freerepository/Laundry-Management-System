package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ReceivedLaundryList implements Serializable{
    @SerializedName("message")
    public String message;
    @SerializedName("Getreceived_data")
    public ArrayList<ReceivedItem> mReceivedList;

    public static class ReceivedItem implements Serializable {
        @SerializedName("id")
        public String mSupply_id;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("supply_date")
        public String mSupply_date;
        @SerializedName("depot")
        public String mDepot;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("coach")
        public String mCoach;
        @SerializedName("no_of_bag")
        public String mNo_of_bag;
        @SerializedName("packet_count")
        public String mPacket_count;
        @SerializedName("qr_url")
        public String mQr_url;
        @SerializedName("no_blanket")
        public String mNo_blanket;
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
        @SerializedName("remark")
        public String mRemark;
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
        @SerializedName("laundry_area")
        public String mLaundry_area;
        @SerializedName("delivery_status")
        public String mDelivery_status;

    }

}
