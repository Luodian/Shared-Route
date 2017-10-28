package com.example.administrator.sharedroute.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.activity.BlurredActivity;
import com.example.administrator.sharedroute.entity.ReleaseOrderItem;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.listener.OnBlurCompleteListener;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.widget.BlurBehind;

import java.util.ArrayList;
import java.util.List;

public class ReleaseOrderItemAdapter extends RecyclerView.Adapter<ReleaseOrderItemAdapter.ViewHolder> {

    private Context mContext;
    private List<listItem> mItemList;
    private OrderDao orderDao;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView expressImage;
        ImageView typeImage;
        ImageView ItemStatus;
        TextView releaseDate;
        TextView releaseType;
        TextView sendPlace;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            expressImage = (ImageView)view.findViewById(R.id.express_image);
            typeImage = (ImageView)view.findViewById(R.id.type_image);
            releaseDate = (TextView) view.findViewById(R.id.send_time);
            sendPlace = (TextView) view.findViewById(R.id.send_location);
            releaseType = (TextView) view.findViewById(R.id.type);
            ItemStatus = (ImageView) view.findViewById(R.id.item_status);
        }
    }

    public ReleaseOrderItemAdapter(List<listItem> ItemList) {
        if (ItemList == null) mItemList = new ArrayList<listItem>();
        else mItemList = ItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        if (orderDao == null) orderDao = new OrderDao(mContext);


        View view = LayoutInflater.from(mContext).inflate(R.layout.release_order_item_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(v.getContext(),"长按订单可查看详细信息!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                final int position = holder.getAdapterPosition();
                BlurBehind.getInstance().execute((Activity) mContext, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(mContext, BlurredActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("title_name","title"+Integer.toString(position+1));
                        intent.putExtra("select","release");
                        intent.putExtra("Activity","Main");
                        mContext.startActivity(intent);
                    }
                });
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        listItem item = mItemList.get(position);
        setImageView(item, holder);
        holder.typeImage.setImageResource(R.mipmap.ic_type_book);
        holder.releaseDate.setText(item.getOutTimeStamp());
        holder.releaseType.setText(item.getExpressType());
        holder.ItemStatus.setImageResource(R.mipmap.ic_none_receive_status);
        holder.sendPlace.setText(item.getOutLocation());
    }

    @Override
    public int getItemCount(){
        return mItemList.size();
    }

    private void setImageView(listItem e, ViewHolder holder) {
        int express = -1;
        String expressCompany = e.getInLocation();
        Log.e("express", expressCompany);
        if (expressCompany.contains("申通")) express = 1;
        else if (expressCompany.contains("天天")) express = 2;
        else if (expressCompany.contains("韵达")) express = 3;
        else if (expressCompany.contains("圆通")) express = 4;
        else if (expressCompany.contains("中通")) express = 5;
        else if (expressCompany.contains("百事")) express = 6;
        else if (expressCompany.contains("顺丰")) express = 7;
        Log.e("express", String.valueOf(express));
        switch (express) {
            case 1: {
                holder.expressImage.setImageResource(R.drawable.sto_express);
                break;
            }
            case 2: {
                holder.expressImage.setImageResource(R.drawable.tt_express);
                break;
            }
            case 3: {
                holder.expressImage.setImageResource(R.drawable.yd_express);
                break;
            }
            case 4: {
                holder.expressImage.setImageResource(R.drawable.yt_express);
                break;
            }
            case 5: {
                holder.expressImage.setImageResource(R.drawable.zto_express);
                break;
            }
            case 6: {
                holder.expressImage.setImageResource(R.drawable.best_express);
                break;
            }
            case 7: {
                holder.expressImage.setImageResource(R.drawable.sf_express);
                break;
            }
            default:
                break;
        }

        return;
    }
}