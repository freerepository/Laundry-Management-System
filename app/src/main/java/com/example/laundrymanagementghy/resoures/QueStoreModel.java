package com.example.laundrymanagementghy.resoures;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class QueStoreModel implements Serializable {

    @SerializedName("store_items")
    public ArrayList<CatItem> mCatItems;
    public static class CatItem implements Serializable {

        @SerializedName("id")
        public String mCatId;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("item_name")
        public String mItem_name;
        @SerializedName("pl_number")
        public String mPl_number;


    }

}
