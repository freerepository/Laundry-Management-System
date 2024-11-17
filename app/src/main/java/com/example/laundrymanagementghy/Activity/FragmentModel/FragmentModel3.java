package com.example.laundrymanagementghy.Activity.FragmentModel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FragmentModel3 implements Serializable {

    @SerializedName("message")
    public String message;
    @SerializedName("BalanceSummaryData")
    public ArrayList<FragmentModel3.mStoreItem> mList;

    public static class mStoreItem implements Serializable {
        @SerializedName("item_name")
        public String itemName;

        @SerializedName("issued_to_depot_Train")
        public String issuedToDepotTrain;

        @SerializedName("fresh_washed_available")
        public int freshWashedAvailable; // Changed to int to handle negative values

        @SerializedName("soiled_available")
        public String soiledAvailable;
    }
}