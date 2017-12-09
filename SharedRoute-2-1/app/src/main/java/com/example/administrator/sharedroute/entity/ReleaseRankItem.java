package com.example.administrator.sharedroute.entity;

public class ReleaseRankItem {
    int rankImageID;
    String rankNum;
    String userName;
    String releaseNum;

    public int getRankImageID() {
        return rankImageID;
    }

    public void setRankImageID(int rankImageID) {
        this.rankImageID = rankImageID;
    }

    public String getRankNum() {
        return rankNum;
    }

    public void setRankNum(String rankNum) {
        this.rankNum = rankNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }
}