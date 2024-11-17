package com.example.laundrymanagementghy.resoures;

import java.io.Serializable;

public class QtestCheckanswerData implements Serializable {
    public String quest_id="", cat_id="", total_penalty_amount="",item_no="",wmi="",rate="",item_name="";
    public QtestCheckanswerData(){ }
    public QtestCheckanswerData setQuestId(String id){
        this.quest_id=id;
        return this;
    }
    public QtestCheckanswerData setCatId(String id){
        this.cat_id=id;
        return this;
    }

    public QtestCheckanswerData setTotalPAmount(String pamount){
        this.total_penalty_amount=pamount;
        return this;
    }



    public QtestCheckanswerData setItem_no(String item_no){
        this.item_no=item_no;
        return this;
    }
    public QtestCheckanswerData setWmi(String wmi){
        this.wmi=wmi;
        return this;
    }
    public QtestCheckanswerData setRate(String rate){
        this.rate=rate;
        return this;
    }
    public QtestCheckanswerData setItemName(String item_name){
        this.item_name=item_name;
        return this;
    }
}
