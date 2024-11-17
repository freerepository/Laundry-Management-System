package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetLaundryType implements Serializable {

    @SerializedName("laundry_data")
    public ArrayList<Item> mLaundryType;

    public static class Item implements Serializable {
        @SerializedName("laundry_id")
        public String mLaundry_id;
        @SerializedName("laundry_name")
        public String mLaundry_name;

    }
}