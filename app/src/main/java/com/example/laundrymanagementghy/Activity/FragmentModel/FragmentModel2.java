package com.example.laundrymanagementghy.Activity.FragmentModel;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class FragmentModel2 implements Serializable {


    @SerializedName("message")
    public static String message;

    @SerializedName("Getsupply_data")
    public List<mStoreItem> mUserItems;

    public static class mStoreItem implements Serializable {

        @SerializedName("id")
        public String id;

        @SerializedName("laundry_id")
        public String laundryId;

        @SerializedName("laundry_supply_id")
        public String laundrySupplyId;

        @SerializedName("supply_date")
        public String supplyDate;

        @SerializedName("depot_code")
        public String depotCode;

        @SerializedName("train_no")
        public String trainNo;

        @SerializedName("coach")
        public String coach;

        @SerializedName("no_of_bag")
        public String noOfBag;

        @SerializedName("bedShhet")
        public String bedSheet;

        @SerializedName("pillow")
        public String pillow;

        @SerializedName("pillowCover")
        public String pillowCover;

        @SerializedName("faceTowel")
        public String faceTowel;

        @SerializedName("blanket_cover")
        public String blanketCover;

        @SerializedName("bath_towel")
        public String bathTowel;

        @SerializedName("blanket")
        public String blanket;

        @SerializedName("no_blanket")
        public String noBlanket;

        @SerializedName("packet_count")
        public String packetCount;

        @SerializedName("status")
        public String status;

        @SerializedName("laundry_area")
        public String laundryArea;

        @SerializedName("depot_name")
        public String depotName;

        @SerializedName("delivery_status")
        public String deliveryStatus;
    }
}
