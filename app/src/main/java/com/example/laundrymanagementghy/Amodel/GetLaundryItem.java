package com.example.laundrymanagementghy.Amodel;

import com.example.laundrymanagementghy.resoures.CatPenaltyModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetLaundryItem implements Serializable {

    @SerializedName("laundry_items")
    public ArrayList<GetLaundryItem.CatItem> mCatItems;
    public static class CatItem implements Serializable {

        @SerializedName("id")
        public String mCatId;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("item_name")
        public String mCat_title;


        @SerializedName("qty")
        public String mQty;
        @SerializedName("ref_no")
        public String mRef_no;
        @SerializedName("price_rate")
        public String mPrice_rate;
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


        @SerializedName("question_name")
        public String mQueationName;


        @SerializedName("penalty_amount")
        public String mPenaltyAmount;





    }

}
