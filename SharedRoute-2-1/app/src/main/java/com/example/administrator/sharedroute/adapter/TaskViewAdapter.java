package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.activity.TaskViewActivity;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 王烨臻 on 2017/10/1.
 */

public class TaskViewAdapter extends ArrayAdapter<listItem> {
    private Context mContext;
    private List<listItem> listItems = null;
    private HashMap<Integer,Boolean> checkStatus;
    private Toolbar mToolbar;
    private LinearLayout mLinearLayout;
    private static int count = 0;
    //private HashMap<String,Boolean> radioMap;

    public TaskViewAdapter(Context mContext) {
        this.mContext=mContext;
        this.listItems=getItems();
        mToolbar = (Toolbar) (((TaskViewActivity)mContext).findViewById(R.id.toolbartaskview));
        mLinearLayout=(LinearLayout) (((TaskViewActivity)mContext).findViewById(R.id.bottom_toolbar));
        checkStatus = new HashMap<Integer, Boolean>(listItems.size());
        for (int i = 0; i < listItems.size(); i++) {
            checkStatus.put(i,listItems.get(i).isCheckBoxElected());
        }
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        listItem item = listItems.get(position);
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.content_task_view,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Log.e("???0",String.valueOf(count++));

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
        viewHolder.checkBox.setChecked(listItems.get(position).isCheckBoxElected());
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {//说实话，这样监听不太好，每次滑动getView的时候都要重新new一个监听，但是必须获取ChechView所在的那个Item的position，所以只能卸载getView函数内部
            @Override
            public void onClick(View v) {//等于说对于那10个左右的ChechBox,其绑定的监听在不断的改变
                ((listItem)getItem(position)).setCheckBoxElected(((CheckBox)v).isChecked());
                notifyDataSetChanged();
            }
        });
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
        CheckBox checkBox;
        ViewHolder(View view){
            expressType = (TextView)view.findViewById(R.id.TaskView_item_kinds);
            expressSize = (TextView)view.findViewById(R.id.TaskView_item_size);
            inTimeStamp = (TextView)view.findViewById(R.id.TaskView_fetch_time);
            inLocation = (TextView)view.findViewById(R.id.TaskView_fetch_loc);
            outTimeStamp = (TextView)view.findViewById(R.id.TaskView_send_time);
            outLocation = (TextView)view.findViewById(R.id.TaskView_send_loc);
            price = (TextView)view.findViewById(R.id.TaskView_item_price);
            item_bg=(LinearLayout)view.findViewById(R.id.item_bg);
            checkBox=(CheckBox)view.findViewById(R.id.TaskView_checkBox);
            name = (TextView)view.findViewById(R.id.TaskView_name);
        }
    }
}
