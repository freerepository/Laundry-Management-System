package com.example.laundrymanagementghy.Activity.FragmentModel;

import com.example.laundrymanagementghy.Bmodel.GetCondemendList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FragmentModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("Summary_items")
    public ArrayList<FragmentModel.mStoreItem> mList;

    public static class mStoreItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("item_name")
        public String mItemName;
        @SerializedName("quantity_received_from_depot_store")
        public String mQuantityReceivedFromDepotStore;
        @SerializedName("Lost_stolen")
        public String mLostStolen;
        @SerializedName("Total_Condemned")
        public String mTotalCondemned;
        @SerializedName("Total_Buffer_provided_to_depot")
        public String mTotalBufferProvidedToDepot;
        @SerializedName("Total_In_Hand")
        public String mTotalInHand;
    }
}
