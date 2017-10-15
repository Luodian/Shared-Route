package com.example.administrator.sharedroute.entity;

public class ReleaseOrderItem {
    private int expressimageId;
    private int tpyeimageId;
    private int statusimageId;
    private String date;
    private String type;

//    public ReleaseOrderItem(int expressimageId,int tpyeimageId,int statuscolorId,String date,String type){
//        this.statusimageId = statuscolorId;
//        this.expressimageId = expressimageId;
//        this.tpyeimageId = tpyeimageId;
//        this.date = date;
//        this.type = type;
//    }

    public int getStatusimageId() {
        return statusimageId;
    }

    public int getExpressimageId() {
        return expressimageId;
    }

    public int getTpyeimageId() {
        return tpyeimageId;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public void setExpressimageId(int expressimageId) {
        this.expressimageId = expressimageId;
    }

    public void setTpyeimageId(int tpyeimageId) {
        this.tpyeimageId = tpyeimageId;
    }

    public void setStatusimageId(int statusimageId) {
        this.statusimageId = statusimageId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }
}