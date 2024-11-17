package com.example.laundrymanagementghy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetStockDataModel implements Serializable{

    @SerializedName("StockDataItems")
    public ArrayList<GetStockDataModel.itemsData> mUpstockItem;

    public static class itemsData implements Serializable{
        @SerializedName("id")
        public String mId;
        @SerializedName("item")
        public String mItem;
        @SerializedName("item_name")
        public String mItem_name;
        @SerializedName("reason")
        public String mReason;
        @SerializedName("qty")
        public String mQty;

    }
}
