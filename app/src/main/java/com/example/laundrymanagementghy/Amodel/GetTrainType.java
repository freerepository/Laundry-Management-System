package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetTrainType implements Serializable {

    @SerializedName("train_data")
    public ArrayList<Item> mTrainType;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("train_no")
        public String mTrain_no;

    }
}