package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EditSupplyModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("success")
    public int success;
    @SerializedName("get_data")
    public ArrayList<EditSupplyModel.ItemEdit> mEditList;

    public static class ItemEdit implements Serializable {
        @SerializedName("supply_id")
        public String mSupply_id;
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("supply_date")
        public String mSupply_date;
        @SerializedName("depot")
        public String mDepot;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("packet_count")
        public String mPacket_count;
        @SerializedName("blanket")
        public String mBlanket;
        @SerializedName("no_blanket")
        public String mNo_blanket;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;
        @SerializedName("created_by")
        public String mCreated_by;
        @SerializedName("created_date")
        public String mCreated_date;
        @SerializedName("updated_by")
        public String mUpdated_by;
        @SerializedName("updated_date")
        public String mUpdated_date;
    }

}
