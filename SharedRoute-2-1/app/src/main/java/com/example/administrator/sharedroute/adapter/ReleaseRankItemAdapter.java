package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.Client;
import com.example.administrator.sharedroute.entity.ReleaseRankItem;
import com.example.administrator.sharedroute.entity.listItem;

import java.util.ArrayList;
import java.util.List;

public class ReleaseRankItemAdapter extends RecyclerView.Adapter<ReleaseRankItemAdapter.ViewHolder>{

    private Context mContext;
    private List<Client> mItemList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView text_name;
        TextView text_ID;
        TextView pickOrdernum;
        TextView completeOrdernum;
        ImageView rankicon;
        LinearLayout rankBG;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.rank_icon_image);
            text_name = (TextView) view.findViewById(R.id.rank_name);
            text_ID = (TextView) view.findViewById(R.id.rank_ID);
            pickOrdernum = (TextView) view.findViewById(R.id.rank_picknum);
            completeOrdernum = (TextView) view.findViewById(R.id.rank_completenum);
            rankBG = (LinearLayout)view.findViewById(R.id.rank_bg);
            rankicon = view.findViewById(R.id.imageView13);
        }
    }

    public ReleaseRankItemAdapter(List<Client> ItemList) {
        if (ItemList == null) mItemList = new ArrayList<>();
        else mItemList = ItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.compus_rank,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Client item = mItemList.get(position);
        holder.text_ID.setText( item.studentID);
        holder.text_name.setText(item.name);
        holder.pickOrdernum.setText(String.valueOf(item.pickOrderNum)+"次");
        holder.completeOrdernum.setText(String.valueOf(item.completeOrderNum)+"次");
        int judge = position % 3;
        switch (judge){
            case 0:{
                holder.rankBG.setBackgroundResource(R.drawable.shadow1);
                if (position == 0)
                holder.rankicon.setImageResource(R.drawable.rank_1);
                break;
            }
            case 1:{
                holder.rankBG.setBackgroundResource(R.drawable.shadow2);
                if (position == 1)
                holder.rankicon.setImageResource(R.drawable.rank_2);
                break;
            }
            case 2:{
                holder.rankBG.setBackgroundResource(R.drawable.shadow3);
                if (position == 2)
                holder.rankicon.setImageResource(R.drawable.rank_3);
                break;
            }
            default:break;
        }
    }

    @Override
    public int getItemCount(){
        return mItemList.size();
    }
}