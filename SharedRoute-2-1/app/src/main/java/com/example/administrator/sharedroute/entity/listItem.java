package com.example.administrator.sharedroute.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.LinearLayout;

/**
 * Created by 王烨臻 on 2017/10/1.
 */

public class listItem implements Parcelable{

    public int ID;
    public double Money;
    public String PickID; //取货码
    public String TaskKindID; //物品种类
    public String FetchLocation;
    public String SendLocation;
    public String FetchTime;
    public String SendTime;
    public int WhichPay;
    public String Remark;
    public String PublisherID;
    public String PublisherName;
    public String FetcherID;
    public String PublisherPhone;
    public String FetcherPhone;
    public String FetcherName;
    public int status;
    public double PromiseMoney;

    private boolean isCheckBoxElected;
    public LinearLayout item_bg;

    public boolean isCheckBoxElected() {
        return isCheckBoxElected;
    }

    public void setCheckBoxElected(boolean checkBoxElected) {
        isCheckBoxElected = checkBoxElected;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeDouble(Money);
        dest.writeString(PickID);
        dest.writeString(TaskKindID);
        dest.writeString(FetchLocation);
        dest.writeString(SendLocation);
        dest.writeString(FetchTime);
        dest.writeString(SendTime);
        dest.writeInt(WhichPay);
        dest.writeString(Remark);
        dest.writeString(PublisherID);
        dest.writeString(PublisherName);
        dest.writeString(FetcherID);
        dest.writeString(PublisherPhone);
        dest.writeString(FetcherPhone);
        dest.writeString(FetcherName);
        dest.writeInt(status);
        dest.writeDouble(PromiseMoney);
    }
    public static final Creator<listItem> CREATOR = new Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            listItem item = new listItem();
            item.ID = source.readInt();
            item.Money = source.readDouble();
            item.PickID = source.readString();
            item.TaskKindID = source.readString();
            item.FetchLocation = source.readString();
            item.SendLocation = source.readString();
            item.FetchTime = source.readString();
            item.SendTime = source.readString();
            item.WhichPay = source.readInt();
            item.Remark = source.readString();
            item.PublisherID = source.readString();
            item.PublisherName = source.readString();
            item.FetcherID = source.readString();
            item.PublisherPhone = source.readString();
            item.FetcherPhone = source.readString();
            item.FetcherName = source.readString();
            item.status = source.readInt();
            item.PromiseMoney = source.readDouble();
            return item;
        }

        @Override
        public listItem[] newArray(int size) {
            return new listItem[size];
        }
    };

}
