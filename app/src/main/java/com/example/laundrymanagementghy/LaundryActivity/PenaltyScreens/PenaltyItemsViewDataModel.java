package com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class PenaltyItemsViewDataModel implements Serializable {

    @SerializedName("PenaltyItemsData")
    public ArrayList<PenaltyItem> penaltyItemsData;

    public static class PenaltyItem implements Serializable {

        @SerializedName("id")
        public String id;

        @SerializedName("tans_id")
        public String transactionId;

        @SerializedName("tans_type")
        public String transactionType;

        @SerializedName("laundry_id")
        public String laundryId;

        @SerializedName("item_id")
        public String itemId;

//        @SerializedName("item_name")
//        public String itemName;

        @SerializedName("item_name")
        public String itemName;

        @SerializedName("qty")
        public String quantity;

        @SerializedName("rate")
        public String rate;

        @SerializedName("amount")
        public String amount;

        @SerializedName("created_date")
        public String createdDate;

        @SerializedName("supervisor_id")
        public String supervisorId;
    }
}
