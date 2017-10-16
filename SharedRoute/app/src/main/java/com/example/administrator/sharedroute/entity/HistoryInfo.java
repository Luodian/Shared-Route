package com.example.administrator.sharedroute.entity;

/**
 * Created by 17990 on 10/13/2017.
 */

public class HistoryInfo {
    private String name;
    private String phone;
    private String deliverPlace;
    public HistoryInfo(String name,String phone,String deliverPlace) {
        this.deliverPlace=deliverPlace;
        this.name=name;
        this.phone=phone;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getDeliverPlace() {
        return deliverPlace;
    }

}