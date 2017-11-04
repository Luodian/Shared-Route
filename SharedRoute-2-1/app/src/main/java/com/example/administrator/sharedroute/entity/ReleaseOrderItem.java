package com.example.administrator.sharedroute.entity;

public class ReleaseOrderItem {
    private int headImageId;
    private int phoneImageId;
    private String userName;
    private String userPhone;
    private String releaseTime;
    private String fetchLocation;
    private int statusImageId;
    private String statusText;

    public int getHeadImageId() {
        return headImageId;
    }

    public void setHeadImageId(int headImageId) {
        this.headImageId = headImageId;
    }

    public int getPhoneImageId() {
        return phoneImageId;
    }

    public void setPhoneImageId(int phoneImageId) {
        this.phoneImageId = phoneImageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getFetchLocation() {
        return fetchLocation;
    }

    public void setFetchLocation(String fetchLocation) {
        this.fetchLocation = fetchLocation;
    }

    public int getStatusImageId() {
        return statusImageId;
    }

    public void setStatusImageId(int statusImageId) {
        this.statusImageId = statusImageId;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}