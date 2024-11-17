package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SoiledModelList implements Serializable {
    @SerializedName("Getreceived_data")
    public ArrayList<mSupItem> mLists;

    public static class mSupItem implements Serializable {
        @SerializedName("id")
        public String mSent_id;
        @SerializedName("date")
        public String mDate;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("sent_depot_id")
        public String mSent_depot_id;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("coach")
        public String mCoach;
        @SerializedName("no_of_bag")
        public String mNo_of_bag;
        @SerializedName("bs_first_ac")
        public String mBs_first_ac;
        @SerializedName("pc_first_ac")
        public String mPc_first_ac;
        @SerializedName("bs")
        public String mBs;
        @SerializedName("pillow")
        public String mPillow;
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
        @SerializedName("status")
        public String mStatus;
        @SerializedName("is_verified")
        public String mIs_verified;
        @SerializedName("is_cleaned")
        public String mIs_cleaned;
        @SerializedName("bs_return")
        public String mBs_return;
        @SerializedName("pillow_return")
        public String mPillow_return;
        @SerializedName("pc_return")
        public String mPc_return;
        @SerializedName("ft_return")
        public String mFt_return;
        @SerializedName("blk_return")
        public String mBlk_return;
        @SerializedName("blanket_return")
        public String mBlanket_return;
        @SerializedName("bathtowel_return")
        public String mBathtowel_return;
        @SerializedName("qr_url")
        public String mQr_url;
        @SerializedName("remark")
        public String mRemark;
        @SerializedName("depot_remark")
        public String mDepot_remark;
        @SerializedName("scan_by")
        public String mScan_by;
        @SerializedName("scan_by_staff")
        public String mScan_by_staff;
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
        @SerializedName("laundry_code")
        public String mLaundry_code;
        @SerializedName("depot_name")
        public String mDepot_name;
        @SerializedName("delivery_status")
        public String mDelivery_status;
    }
}
