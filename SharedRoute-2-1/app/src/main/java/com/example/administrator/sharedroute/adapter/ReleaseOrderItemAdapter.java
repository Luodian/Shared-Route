package com.example.administrator.sharedroute.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.administrator.sharedroute.entity.ReleaseOrderItem;
import com.example.administrator.sharedroute.listener.OnBlurCompleteListener;
import com.example.administrator.sharedroute.widget.BlurBehind;

import java.util.List;

public class ReleaseOrderItemAdapter extends RecyclerView.Adapter<ReleaseOrderItemAdapter.ViewHolder> {

    private Context mContext;
    private List<ReleaseOrderItem> mItemList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView expressImage;
        ImageView typeImage;
        ImageView ItemStatus;
        TextView releaseDate;
        TextView releaseType;


        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            expressImage = (ImageView)view.findViewById(R.id.express_image);
            typeImage = (ImageView)view.findViewById(R.id.type_image);
            releaseDate = (TextView) view.findViewById(R.id.release_date);
            releaseType = (TextView) view.findViewById(R.id.type);
            ItemStatus = (ImageView) view.findViewById(R.id.item_status);
        }
    }

    public ReleaseOrderItemAdapter(List<ReleaseOrderItem> ItemList){
        mItemList = ItemList;
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
        ReleaseOrderItem item = mItemList.get(position);
        holder.expressImage.setImageResource(item.getExpressimageId());
        holder.typeImage.setImageResource(item.getTpyeimageId());
        holder.releaseDate.setText(item.getDate());
        holder.releaseType.setText(item.getType());
        holder.ItemStatus.setImageResource(item.getStatusimageId());
    }

    @Override
    public int getItemCount(){
        return mItemList.size();
    }
}