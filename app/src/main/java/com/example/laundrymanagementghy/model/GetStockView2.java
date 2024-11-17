package com.example.laundrymanagementghy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetStockView2 implements Serializable{

        //my url is -> http://lmsguwahati.projectrailway.in/api/getStockDataItems

        @SerializedName("GetData")
        public ArrayList<itemsData> mItemsData;
        public static class itemsData implements Serializable{
            @SerializedName("id")
            public String mId;
            @SerializedName("item")
            public String mItem;
            @SerializedName("item_name")
            public String mItem_name;
            @SerializedName("qty")
            public String mQty;
            @SerializedName("reason")
            public String mReason;
   }
}
