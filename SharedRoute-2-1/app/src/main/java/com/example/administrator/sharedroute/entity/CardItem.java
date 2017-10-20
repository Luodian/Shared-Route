package com.example.administrator.sharedroute.entity;


public class CardItem {

    public String operName;
    public int resId;

    public String getOperName() {
        return operName;
    }

    public int getResId() {
        return resId;
    }

    public CardItem(String operName, int resId)
    {
        this.operName = operName;
        this.resId = resId;
    }
}
