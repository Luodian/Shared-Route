package com.example.administrator.sharedroute.entity;

import android.widget.ImageView;

/**
 * Created by 王烨臻 on 2017/11/23.
 */

public class Client {
    public String studentID;
    public String name;
    public int pickOrderNum;
    public int completeOrderNum;
    public ImageView icno;

    public Client(String studentID, String name, int pickOrderNum, int completeOrderNum, ImageView icno) {
        this.studentID = studentID;
        this.name = name;
        this.pickOrderNum = pickOrderNum;
        this.completeOrderNum = completeOrderNum;
        this.icno = icno;
    }

    public Client(String studentID, String name, int pickOrderNum, int completeOrderNum) {
        this.studentID = studentID;
        this.name = name;
        this.pickOrderNum = pickOrderNum;
        this.completeOrderNum = completeOrderNum;
    }
}
