package com.example.administrator.sharedroute.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.activity.BlurredActivity;
import com.example.administrator.sharedroute.activity.ConfirmBlurredActivity;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.listener.OnBlurCompleteListener;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.widget.BlurBehind;

import java.util.ArrayList;
import java.util.List;

public class AcceptedOrderItemAdapter extends RecyclerView.Adapter<AcceptedOrderItemAdapter.ViewHolder> {

    private Context mContext;
    private List<listItem> mItemList;
    private int state;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView userHeader;
        ImageView phoneImage;
        TextView userName;
        TextView userPhone;
        TextView releaseTime;
        TextView fetchLocation;
        ImageView statusImage;
        TextView statusText;
        TextView sta;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            userHeader = view.findViewById(R.id.user_header);
            phoneImage = view.findViewById(R.id.phone_image);
            userName =  view.findViewById(R.id.user_name);
            userPhone = view.findViewById(R.id.user_phone);
            releaseTime =  view.findViewById(R.id.release_time);
            fetchLocation =view.findViewById(R.id.fetch_location);
            statusImage = view.findViewById(R.id.status_image);
            statusText = view.findViewById(R.id.status_text);
            sta = view.findViewById(R.id.PorA);
        }
    }

    public AcceptedOrderItemAdapter(List<listItem> ItemList,int state) {//判断是接受列表0还是发布列表1
        if (ItemList == null) mItemList = new ArrayList<>();
        else mItemList = ItemList;
        this.state = state;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }


        View view = LayoutInflater.from(mContext).inflate(R.layout.release_order_item_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(mContext,"长按可看详细信息",Toast.LENGTH_SHORT).show();
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
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("item",mItemList.get(position));
                        intent.putExtras(bundle);
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
//        listItem item = mItemList.get(position);
//        setImageView(item, holder);
//        holder.typeImage.setImageResource(R.mipmap.ic_type_book);
//        holder.releaseDate.setText(item.getOutTimeStamp());
//        holder.releaseType.setText(item.getExpressType());
//        holder.ItemStatus.setImageResource(R.mipmap.ic_none_receive_status);
//        holder.sendPlace.setText(item.getOutLocation());
        holder.userHeader.setImageResource(R.drawable.nav_icon);
        holder.phoneImage.setImageResource(R.drawable.mainpage_ringphone);
            holder.sta.setText("发布人");
            holder.userName.setText(mItemList.get(position).FetcherName);
            holder.userPhone.setText(mItemList.get(position).FetcherPhone);
        holder.releaseTime.setText(mItemList.get(position).SendTime);
        holder.fetchLocation.setText(mItemList.get(position).SendLocation);
        holder.statusImage.setImageResource(R.drawable.mainpage_semimoon);/**/
        if (mItemList.get(position).status==1) holder.statusText.setText("未接单");
        else if (mItemList.get(position).status==2) holder.statusText.setText("已接单");
        else if (mItemList.get(position).status==3) holder.statusText.setText("已完成");
        else holder.statusText.setText("未知");
    }

    @Override
    public int getItemCount(){
        return mItemList.size();
    }

//    private void setImageView(listItem e, ViewHolder holder) {
//        int express = -1;
//        String expressCompany = e.getInLocation();
//        Log.e("express", expressCompany);
//        if (expressCompany.contains("申通")) express = 1;
//        else if (expressCompany.contains("天天")) express = 2;
//        else if (expressCompany.contains("韵达")) express = 3;
//        else if (expressCompany.contains("圆通")) express = 4;
//        else if (expressCompany.contains("中通")) express = 5;
//        else if (expressCompany.contains("百事")) express = 6;
//        else if (expressCompany.contains("顺丰")) express = 7;
//        Log.e("express", String.valueOf(express));
//        switch (express) {
//            case 1: {
//                holder.expressImage.setImageResource(R.drawable.sto_express);
//                break;
//            }
//            case 2: {
//                holder.expressImage.setImageResource(R.drawable.tt_express);
//                break;
//            }
//            case 3: {
//                holder.expressImage.setImageResource(R.drawable.yd_express);
//                break;
//            }
//            case 4: {
//                holder.expressImage.setImageResource(R.drawable.yt_express);
//                break;
//            }
//            case 5: {
//                holder.expressImage.setImageResource(R.drawable.zto_express);
//                break;
//            }
//            case 6: {
//                holder.expressImage.setImageResource(R.drawable.best_express);
//                break;
//            }
//            case 7: {
//                holder.expressImage.setImageResource(R.drawable.sf_express);
//                break;
//            }
//            default:
//                break;
//        }
//
//        return;
//    }
}