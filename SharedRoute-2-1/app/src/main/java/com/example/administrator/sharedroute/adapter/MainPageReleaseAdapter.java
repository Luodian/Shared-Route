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
import com.example.administrator.sharedroute.widget.BlurBehind;

import java.util.List;

/**
 * Created by 王烨臻 on 2017/11/21.
 */

public class MainPageReleaseAdapter extends RecyclerView.Adapter<MainPageReleaseAdapter.ViewHolder> {
    Context mContext;
    List<listItem> mItemList;

    public MainPageReleaseAdapter(Context mContext, List<listItem> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView taskState;
//        TextView publishDate;
        TextView taskPrice;
        TextView expressCompany;
        TextView expressPicknum;
        TextView publishname;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view.findViewById(R.id.card_bg);
            taskState = (TextView)view.findViewById(R.id.task_state);
//            publishDate = (TextView)view.findViewById(R.id.publish_date);
            taskPrice = (TextView)view.findViewById(R.id.task_price);
            expressCompany = (TextView)view.findViewById(R.id.express_company);
            expressPicknum = (TextView)view.findViewById(R.id.express_picknum);
            publishname = (TextView)view.findViewById(R.id.publish_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.compus_main_item,null,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final int position = holder.getAdapterPosition();
                BlurBehind.getInstance().execute((Activity) mContext, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        if (mItemList.get(position).status == 3) {
                            Toast.makeText(mContext,"此订单已完成",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(mContext, ConfirmBlurredActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle bundle = new Bundle();
                        System.out.println( mItemList.get(position).ID+"|");
                        bundle.putParcelable("item",mItemList.get(position));
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.publishname.setText(mItemList.get(position).PublisherName);
//        holder.publishDate.setText(mItemList.get(position).FetchTime);
        holder.expressPicknum.setText(mItemList.get(position).PickID);
        holder.expressCompany.setText(mItemList.get(position).FetchLocation);
        holder.taskPrice.setText(String.valueOf(mItemList.get(position).Money)+"元");
        if (mItemList.get(position).status==1) holder.taskState.setText("未接单");
        else if (mItemList.get(position).status==2) holder.taskState.setText("已接单");
        else if (mItemList.get(position).status==3) holder.taskState.setText("已完成");
        else holder.taskState.setText("未知");
    }

    @Override
    public int getItemCount() {
        if (mItemList != null) return mItemList.size();
        else return -1;
    }
}
