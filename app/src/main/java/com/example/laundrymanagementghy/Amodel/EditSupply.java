package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EditSupply implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("success")
    public int success;
    @SerializedName("get_data")
    public ArrayList<EditSupply.ItemEdit> mEditList;

    public static class ItemEdit implements Serializable {
        @SerializedName("supply_id")
        public String mSupply_id;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("supply_date")
        public String mSupply_date;
        @SerializedName("train")
        public String mTrain_id;
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
