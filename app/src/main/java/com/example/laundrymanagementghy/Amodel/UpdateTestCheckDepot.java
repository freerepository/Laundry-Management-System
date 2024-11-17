package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateTestCheckDepot implements Serializable{

    @SerializedName("GetstoreData")
    public ArrayList<UpdateItem> mUpdateItem;

    public static class UpdateItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("user_type")
        public String mUser_type;
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("laundry")
        public String mLaundry;
        @SerializedName("item_id")
        public String item_id;
        @SerializedName("item_name")
        public String item_name;
        @SerializedName("item_no")
        public String item_no;
        @SerializedName("wmi")
        public String wmi;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("remark")
        public String mRemark;
        @SerializedName("penalty")
        public String mPenalty;
        @SerializedName("image")
        public String image;
        @SerializedName("image1")
        public String image1;
        @SerializedName("image2")
        public String image2;
        @SerializedName("image3")
        public String image3;
        @SerializedName("image4")
        public String image4;
        @SerializedName("image5")
        public String image5;
        @SerializedName("image6")
        public String image6;
        @SerializedName("image7")
        public String image7;
        @SerializedName("image8")
        public String image8;
        @SerializedName("image9")
        public String image9;
        @SerializedName("signature")
        public String mSignature;
        @SerializedName("check_date")
        public String mCheck_date;
        @SerializedName("received_type")
        public String mReceived_type;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;
        @SerializedName("created_date")
        public String mCreated_date;
        @SerializedName("created_by")
        public String mCreated_by;
        @SerializedName("updated_date")
        public String mUpdated_date;
        @SerializedName("updated_by")
        public String mUpdated_by;
        @SerializedName("delivery_status")
        public String mDelivery_status;

        @SerializedName("testcheckData")
        public ArrayList<storeData> mTestcheckData;
        public static class storeData implements Serializable{
            @SerializedName("item_id")
            public String mItem_id;
            @SerializedName("item_name")
            public String mItem_name;
            @SerializedName("item_no")
            public String mItem_no;
            @SerializedName("wmi")
            public String mWmi;

       }

   }
}
