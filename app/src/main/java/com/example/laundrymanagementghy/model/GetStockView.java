package com.example.laundrymanagementghy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetStockView implements Serializable{

    @SerializedName("StockDataItems")
    public ArrayList<itemsData> mItemsData;
    public static class itemsData implements Serializable{
        @SerializedName("id")
        public String mId;
        @SerializedName("item_name")
        public String mItem_name;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("price_rate")
        public String mPrice_rate;
        @SerializedName("qty")
        public String mQty;
        @SerializedName("ref_no")
        public String mRef_no;

    }
}
