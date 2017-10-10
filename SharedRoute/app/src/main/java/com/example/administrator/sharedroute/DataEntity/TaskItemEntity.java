package com.example.administrator.sharedroute.DataEntity;

/**
 * Created by luodian on 03/10/2017.
 */

public class TaskItemEntity {
    public String name;
    public int price;
    public String kinds;
    public String info;
    public String fetch_time;
    public String fetch_loc;
    public String send_time;
    public String send_loc;
    public int campus;
    TaskItemEntity(String _name,int _price,String _kinds,String _info,String _fetch_time,String _fetch_loc,String _send_time,String _send_loc,int _campus) {
        name = _name;
        price = _price;
        kinds = _kinds;
        info = _info;
        fetch_time = _fetch_time;
        fetch_loc = _fetch_loc;
        send_time = _send_time;
        send_loc = _send_loc;
        campus = _campus;
    }
}
