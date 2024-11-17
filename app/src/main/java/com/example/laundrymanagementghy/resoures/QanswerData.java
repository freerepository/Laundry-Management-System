package com.example.laundrymanagementghy.resoures;

import com.example.laundrymanagementghy.Amodel.QanswerData1;

import java.io.Serializable;

public class QanswerData implements Serializable {
    public int totalValue;
    public int itemname;
    public String quest_id = "",
            cat_id = "",
            quantity = "",
            rate = "",
            given_amount = "",
            price_rate = "",
            po_number = "",
            quantity_receipt = "",
            total_penalty_amount = "",
            unit = "",
            pl_number = "",
            item_description = "",
            item_Name = "",
            questionname = "",
            penlatyamount = "";

    public QanswerData() {
    }


    public int getTotalValue() {
        return totalValue;
    }


    public QanswerData setTotalValue(int totalValue) {
        this.totalValue = totalValue;
        return  this;
    }

    public QanswerData setQuestId(String id) {
        this.quest_id = id;
        return this;
    }

    public QanswerData setCatId(String id) {
        this.cat_id = id;
        return this;
    }

    public QanswerData setQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public QanswerData setTotalPAmount(String pamount) {
        this.total_penalty_amount = pamount;
        return this;
    }

    public QanswerData setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public QanswerData setRate(String rate) {
        this.rate = rate;
        return this;
    }

    public QanswerData setGivenAmount(String gamount) {
        this.given_amount = gamount;
        return this;
    }

    public QanswerData setPlnumber(String pl_number) {
        this.pl_number = pl_number;
        return this;
    }

    public QanswerData Setitem_description(String item_description) {
        this.item_description = item_description;
        return this;
    }

    public QanswerData SetItemName(String item_Name) {
        this.item_Name=item_Name;
        return  this;

    }
    public QanswerData SetPonumber(String po_number) {
        this.po_number=po_number;
        return  this;

    }
    public QanswerData SetQuantityReceipt(String quantity_receipt) {
        this.quantity_receipt=quantity_receipt;
        return  this;

    }
    public QanswerData SetPriceRate(String price_rate) {
        this.price_rate=price_rate;
        return  this;

    }


    public QanswerData SetQuestionName(String question_name) {
        this.questionname=question_name;
        return  this;
    }

    public QanswerData SetPenaltyAmount(String amount) {
        this.penlatyamount=amount;
        return  this;
    }

    @Override
    public String toString() {
        return "QanswerData{" +
                "totalValue=" + totalValue +
                ", itemname=" + itemname +
                ", quest_id='" + quest_id + '\'' +
                ", cat_id='" + cat_id + '\'' +
                ", quantity='" + quantity + '\'' +
                ", rate='" + rate + '\'' +
                ", given_amount='" + given_amount + '\'' +
                ", price_rate='" + price_rate + '\'' +
                ", po_number='" + po_number + '\'' +
                ", quantity_receipt='" + quantity_receipt + '\'' +
                ", total_penalty_amount='" + total_penalty_amount + '\'' +
                ", unit='" + unit + '\'' +
                ", pl_number='" + pl_number + '\'' +
                ", item_description='" + item_description + '\'' +
                ", item_Name='" + item_Name + '\'' +
                ", questionname='" + questionname + '\'' +
                ", penlatyamount='" + penlatyamount + '\'' +
                '}';
    }
}

