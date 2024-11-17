package com.example.laundrymanagementghy.Bmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetReceiveddailogStoreList implements Serializable {

        @SerializedName("GetstoreDataItes")
        public ArrayList<ReceivedDataStore> mStoreData;
        public static class ReceivedDataStore implements Serializable{

            @SerializedName("item_description")
            public String mItem_description;
            @SerializedName("pl_no")
            public String mPl_no;
            @SerializedName("qty")
            public String mQty;

        }


}
