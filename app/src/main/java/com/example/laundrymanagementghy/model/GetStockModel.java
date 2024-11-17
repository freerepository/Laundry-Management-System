package com.example.laundrymanagementghy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetStockModel implements Serializable {

    @SerializedName("StockData")
    public ArrayList<stockDataItem> mUpstockItem;

    public static class stockDataItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("date")
        public String mDate;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("supervisor_id")
        public String mSupervisor_id;

        //@SerializedName("store")
        //public String mStore;
    }
}
