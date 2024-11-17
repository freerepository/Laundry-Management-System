package com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetPanaltyQuestionItem implements Serializable {

    @SerializedName("GetPenaltyQuestions")
    public ArrayList<QuestionItem> questionItems ;

    public static class QuestionItem{

        @SerializedName("id")
        public String id;

        @SerializedName("question_name")
        public String questionName;

        @SerializedName("penalty_amount")
        public String penaltyAmount;

    }


}
