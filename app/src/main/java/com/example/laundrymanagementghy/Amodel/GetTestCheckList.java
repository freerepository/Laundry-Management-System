package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetTestCheckList implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("GetstoreData")
    public ArrayList<GetTestCheckList.mGetTestItem> mTestCheckList;

    public static class mGetTestItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("user_type")
        public String mUser_type;
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("laundry")
        public String mLaundry;
        @SerializedName("item_id")
        public String mItem_id;
        @SerializedName("item_no")
        public String mItem_no;
        @SerializedName("wmi")
        public String mWmi;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("remark")
        public String mRemark;
        @SerializedName("penalty")
        public String mPenalty;
        @SerializedName("image")
        public String mImage;
        @SerializedName("image1")
        public String mIage1;
        @SerializedName("image2")
        public String mIage2;
        @SerializedName("image3")
        public String mIage3;
        @SerializedName("image4")
        public String mIage4;
        @SerializedName("image5")
        public String mIage5;
        @SerializedName("image6")
        public String mIage6;
        @SerializedName("image7")
        public String mIage7;
        @SerializedName("image8")
        public String mIage8;
        @SerializedName("image9")
        public String mIage9;
        @SerializedName("signature")
        public String mSignature;
        @SerializedName("check_date")
        public String mCheck_date;
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
        @SerializedName("item_name")
        public String mItem_name;
        @SerializedName("delivery_status")
        public String mDelivery_status;


    }

}

