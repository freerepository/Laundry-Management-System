package com.example.laundrymanagementghy.Amodel;

public class QanswerData1 {
    private String quest_id;
    private String item_name;
    private String quantity;
    private String reason;

    private int buffer;
    public int getBuffr(){
        return buffer;
    }

    public QanswerData1 setBuffer(int bfr){
        this.buffer = bfr;
        return this;
    }

    //set other field to set your data in your map

    public String getQuest_id() {
        return quest_id;
    }
public String getItem_name() {
        return item_name;

    }

    public QanswerData1 setQuestId(String quest_id) {
        this.quest_id = quest_id;
        return this;
    }
    public QanswerData1 setItemName(String name) {
        this.item_name = name;
        return this;
    }

    public String getQuantity() {
        return quantity;
    }

    public QanswerData1 setQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public QanswerData1 setReason(String reason) {
        this.reason = reason;
        return this;

    }
}
