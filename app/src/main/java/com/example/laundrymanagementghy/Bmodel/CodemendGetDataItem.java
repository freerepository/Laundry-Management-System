package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class CodemendGetDataItem implements Serializable {

    @SerializedName("GetData")
    public ArrayList<itemsData> mItemsData;

    public static class itemsData implements Serializable {
        @SerializedName("id")
        public String id;

        @SerializedName("item")
        public String mItem;

        @SerializedName("item_name")
        public String mItemName;

        @SerializedName("reason")
        public String mReason;

        @SerializedName("qty")
        public String mQuantity;
    }
}

