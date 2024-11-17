package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LaundryCategory implements Serializable{
    @SerializedName("message")
    public String message;
    @SerializedName("GetCategory")

    public ArrayList<Item> CatList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mID;
        @SerializedName("category_name")
        public String mCategory_name;

    }
}
