package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class SoildedReturnToLaundryUpdateModel implements Serializable {

    @SerializedName("success")
    public int success;

    @SerializedName("message")
    public String message;

    @SerializedName("get_data")
    public ArrayList<ReturnedItemFromSoilde> returnedItemFromSoildes;

    public static class ReturnedItemFromSoilde implements Serializable {
        @SerializedName("id")
        public String id;

        @SerializedName("date")
        public String date;

        @SerializedName("laundry_id")
        public String laundryId;

        @SerializedName("sent_depot_id")
        public String sentDepotId;

        @SerializedName("depot_code")
        public String depotCode;

        @SerializedName("train_no")
        public String trainNo;

        @SerializedName("coach")
        public String coach;

        @SerializedName("no_of_bag")
        public String noOfBag;

        @SerializedName("bs_first_ac")
        public String bsFirstAc;

        @SerializedName("pc_first_ac")
        public String pcFirstAc;

        @SerializedName("bs")
        public String bs;

        @SerializedName("pc")
        public String pc;

        @SerializedName("ft")
        public String ft;

        @SerializedName("blanket_cover")
        public String blanketCover;

        @SerializedName("bath_towel")
        public String bathTowel;

        @SerializedName("blanket")
        public String blanket;

        @SerializedName("total")
        public String total;

        @SerializedName("unused_packet")
        public String unusedPacket;

        @SerializedName("status")
        public String status;

        @SerializedName("bs_return")
        public String bsReturn;

        @SerializedName("pc_return")
        public String pcReturn;

        @SerializedName("ft_return")
        public String ftReturn;

        @SerializedName("blk_return")
        public String blkReturn;

        @SerializedName("blanket_return")
        public String blanketReturn;

        @SerializedName("bathtowel_return")
        public String bathTowelReturn;

        @SerializedName("qr_url")
        public String qrUrl;

        @SerializedName("remark")
        public String remark;

        @SerializedName("depot_remark")
        public String depotRemark;

        @SerializedName("scan_by")
        public String scanBy;

        @SerializedName("scan_by_staff")
        public String scanByStaff;

        @SerializedName("act_status")
        public String actStatus;

        @SerializedName("del_status")
        public String delStatus;

        @SerializedName("created_by")
        public String createdBy;

        @SerializedName("created_date")
        public String createdDate;

        @SerializedName("updated_by")
        public String updatedBy;

        @SerializedName("updated_date")
        public String updatedDate;
    }
}

