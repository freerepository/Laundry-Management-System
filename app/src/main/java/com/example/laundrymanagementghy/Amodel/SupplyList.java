package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SupplyList implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("Getsuplly_data")
    public ArrayList<TrainItem> mSupplyList;

    public static class TrainItem implements Serializable {
        @SerializedName("id")
        public String mSupply_id;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("date")
        public String mSupply_date;
        @SerializedName("supply_date")
        public String mSupply_date2;
        @SerializedName("train")
        public String mTrain_id;
        @SerializedName("coach")
        public String mCoach;
        @SerializedName("no_of_bag")
        public String mNo_of_bag;
        @SerializedName("packet_count")
        public String mPacket_count;
        @SerializedName("blanket")
        public String mBlanket;
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
        @SerializedName("pillow")
        public String mPillow;
        @SerializedName("pillow_return")
        public String mPillow_return;
        @SerializedName("ft")
        public String mFt;
        @SerializedName("bath_towel")
        public String mBath_towel;
        @SerializedName("blanket_cover")
        public String mBlanket_cover;
        @SerializedName("remark")
        public String mRemark;
        @SerializedName("depot_remark")
        public String mDepot_Remark;
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
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("laundry")
        public String mLaundry;
        @SerializedName("laundry_id")
        public String mLaundryId;


        /////////////////////////////////////////////////////////////////////////////////////////

        @SerializedName("bs_return")
        public String bsReturn;

        @SerializedName("pc_return")
        public String pcReturn;

        @SerializedName("ft_return")
        public String ftReturn;

        @SerializedName("blk_return")
        public String blkReturn;

        @SerializedName("blanket_return")
        public String blanketReturn;

        @SerializedName("bathtowel_return")
        public String bathTowelReturn;


        //unused item
        @SerializedName("bs_unused")
        public String mBs_unsed;
        @SerializedName("pillow_unused")
        public String mPillow_unused;
        @SerializedName("pc_unused")
        public String mPc_unused;
        @SerializedName("blanket_unused")
        public String mBlanket_unused;
        @SerializedName("blc_unused")
        public String mBlc_unused;
        @SerializedName("ht_unused")
        public String mHt_unused;

    }
}
