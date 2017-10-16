package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.CardItem;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<CardItem> {

    private int resId;

    public ListViewAdapter(Context context, int textViewResId, List<CardItem> objects){
        super(context,textViewResId,objects);
        resId = textViewResId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CardItem item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.infoImage = (ImageView)view.findViewById(R.id.info_image);
            viewHolder.infoText = (TextView)view.findViewById(R.id.info_text);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.infoText.setText(item.getOperName());
        viewHolder.infoImage.setImageResource(item.getResId());
        return view;
    }

    class ViewHolder{
        ImageView infoImage;
        TextView infoText;
    }
}