package com.example.laundrymanagementghy.Amodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CoachTypeList implements Serializable{
    @SerializedName("message")
    public String message;
//    @SerializedName("success")
//    public int success;
    @SerializedName("getCoach")
    public ArrayList<CoachItem> mCoachList;

    public static class CoachItem implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("coach_type")
        public String mCoach_type;

    }

}
