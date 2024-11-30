package com.example.laundrymanagementghy.DeportActivity.bedroll;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BedRollDataModel implements Serializable {

    @SerializedName("message")
    public String message;

    @SerializedName("BufferStockData")
    public ArrayList<BedRollItem> bufferlist ;

    public class BedRollItem implements Serializable{
        @SerializedName("id")
        public String id;
        @SerializedName("total_bed_sheets")
        public String totalBedSheet;
        @SerializedName("total_pillows")
        public String totalPillow;
        @SerializedName("total_pillow_cover")
        public String totalPillowCover;
        @SerializedName("total_blanket")
        public String totalBlanket;
        @SerializedName("total_blanket_cover")
        public String totalBlanketCover;
        @SerializedName("total_hand_towel")
        public String totalHandTowel;
    }
}
