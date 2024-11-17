package com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class PenaltyViewModelClass implements Serializable {
    @SerializedName("PenaltyData")
    public ArrayList<PenaltyItem> penaltyData;

    public static class PenaltyItem implements Serializable {
        @SerializedName("id")
        public String id;

        @SerializedName("laundry_id")
        public String laundryId;

        @SerializedName("depot_code")
        public String depotCode;

        @SerializedName("total_penalty")
        public String totalPenalty;

        @SerializedName("penalty_date")
        public String penaltyDate;

        @SerializedName("remark")
        public String remark;
    }
}

