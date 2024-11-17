package com.example.laundrymanagementghy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetSoiledView implements Serializable{

        @SerializedName("laundry_data")
        public ArrayList<itemsData> mItemsData;
        public static class itemsData implements Serializable{
            @SerializedName("id")
            public String mId;
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
            @SerializedName("pc")
            public String mPc;
            @SerializedName("ft")
            public String mFt;
            @SerializedName("blanket_cover")
            public String mBlanket_cover;
            @SerializedName("bath_towel")
            public String mBath_towel;
            @SerializedName("blanket")
            public String mBlanket;
            @SerializedName("total")
            public String mTotal;
            @SerializedName("status")
            public String mStatus;
            @SerializedName("qr_url")
            public String mQR_URL;
            @SerializedName("remark")
            public String mRemark;
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
            @SerializedName("delivery_status")
            public String mDelivery_status;
            @SerializedName("depot_name")
            public String mDepot_name;





}
}
