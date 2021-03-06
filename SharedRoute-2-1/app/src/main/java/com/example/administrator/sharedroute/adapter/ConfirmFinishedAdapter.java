package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.ArrayAdapter;

import java.util.List;

/**
 * Created by 王烨臻 on 2017/10/14.
 */

public class ConfirmFinishedAdapter extends ArrayAdapter<listItem> {
    private List<listItem> listItems = null;
    private Context mContext;
    public ConfirmFinishedAdapter(Context mContext) {
        this.listItems = getItems();
        this.mContext=mContext;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ConfirmTaskAdapter.ViewHolder viewHolder=null;
        if (view==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.content_confirm_task,null);
            viewHolder = new ConfirmTaskAdapter.ViewHolder(view);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ConfirmTaskAdapter.ViewHolder)view.getTag();
        }
        String fetchTime =listItems.get(position).FetchTime;
        String sendTime = listItems.get(position).SendTime;
        int index1 = fetchTime.indexOf('日');
        fetchTime = fetchTime.substring(0,index1+1)+'\n'+fetchTime.substring(index1+1);
        int index2 = sendTime.indexOf('日');
        sendTime = sendTime.substring(0,index2+1)+'\n'+sendTime.substring(index2+1);


        viewHolder.expressType.setText(listItems.get(position).TaskKindID);
        viewHolder.expressSize.setText(listItems.get(position).Remark);
        viewHolder.inTimeStamp.setText(fetchTime);
        viewHolder.inLocation.setText(listItems.get(position).FetchLocation);
        viewHolder.outTimeStamp.setText(sendTime);
        viewHolder.outLocation.setText(listItems.get(position).SendLocation);
        viewHolder.price.setText(String.valueOf(listItems.get(position).Money)+"元");
        viewHolder.name.setText(listItems.get(position).PublisherName);
        return view;
    }
    final static class ViewHolder{//这是每个Item的tag信息
        TextView expressType;
        TextView expressSize;
        TextView inTimeStamp;//取件时间
        TextView inLocation;//取件地点
        TextView outTimeStamp;//送件时间
        TextView outLocation;//送件地点
        TextView price;//价格
        TextView name;
        LinearLayout item_bg;
        ViewHolder(View view){
            expressType = (TextView)view.findViewById(R.id.confirmtask_item_kinds);
            expressSize = (TextView)view.findViewById(R.id.confirmtask_item_size);
            inTimeStamp = (TextView)view.findViewById(R.id.confirmtask_fetch_time);
            inLocation = (TextView)view.findViewById(R.id.confirmtask_fetch_loc);
            outTimeStamp = (TextView)view.findViewById(R.id.confirmtask_send_time);
            outLocation = (TextView)view.findViewById(R.id.confirmtask_send_loc);
            price = (TextView)view.findViewById(R.id.confirmtask_item_price);
            name = (TextView)view.findViewById(R.id.confirmtask_name);
            item_bg=(LinearLayout)view.findViewById(R.id.item_bg);
        }
    }
}
