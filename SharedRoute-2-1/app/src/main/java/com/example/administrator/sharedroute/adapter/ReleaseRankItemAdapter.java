package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.activity.MyRank;
import com.example.administrator.sharedroute.entity.Client;
import com.example.administrator.sharedroute.entity.ReleaseRankItem;
import com.example.administrator.sharedroute.entity.listItem;


import java.util.ArrayList;
import java.util.List;

public class ReleaseRankItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Client> mItemList;

    public static enum ITEM_TYPE {
        ITEM_TYPE_TOP,
        ITEM_TYPE_OTHER
    }

    static class TopThreeHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView text_name;
        TextView text_ID;
        TextView pickOrdernum;
        TextView completeOrdernum;
        ImageView rankicon;
        LinearLayout rankBG;
        TextView fetchtitle;
        TextView pubtitle;
        public TopThreeHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.rank_icon_image);
            text_name =view.findViewById(R.id.rank_name);
            text_ID = view.findViewById(R.id.rank_ID);
            pickOrdernum =  view.findViewById(R.id.rank_picknum);
            completeOrdernum =  view.findViewById(R.id.rank_completenum);
            rankBG = view.findViewById(R.id.rank_bg);
            rankicon = view.findViewById(R.id.imageView13);
            fetchtitle = view.findViewById(R.id.fetch_title);
            pubtitle = view.findViewById(R.id.submit_title);
        }
    }
    static class TopOtherHolder extends RecyclerView.ViewHolder{
        TextView rank_num;
        TextView rank_name;
        TextView rank_times;
        public TopOtherHolder(View view) {
            super(view);
            rank_num = view.findViewById(R.id.rank_num);
            rank_name = view.findViewById(R.id.rank_name);
            rank_times = view.findViewById(R.id.rank_times);
        }
    }
    public ReleaseRankItemAdapter(List<Client> ItemList) {
        if (ItemList == null) mItemList = new ArrayList<>();
        else mItemList = ItemList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(mContext == null) mContext = parent.getContext();
        if (viewType == ITEM_TYPE.ITEM_TYPE_TOP.ordinal()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.compus_rank,parent,false);
            return  new TopThreeHolder(view);
        }
        else if (viewType == ITEM_TYPE.ITEM_TYPE_OTHER.ordinal()){
            View view = LayoutInflater.from(mContext).inflate(R.layout.release_rank_item,parent,false);
            return new TopOtherHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return ( mItemList.get(position).rank<=3 ) ? ITEM_TYPE.ITEM_TYPE_TOP.ordinal():ITEM_TYPE.ITEM_TYPE_OTHER.ordinal();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
            Client item = mItemList.get(position);
            if (holder instanceof TopThreeHolder) {
                ((TopThreeHolder)holder).text_ID.setText(item.studentID);
                ((TopThreeHolder)holder).text_name.setText(item.name);
                if (MyRank.PorA.equals("fetch"))
                {
                    ((TopThreeHolder)holder).pickOrdernum.setText(String.valueOf(item.pickOrderNum) + "次");
                    ((TopThreeHolder)holder).completeOrdernum.setText(String.valueOf(item.completeOrderNum) + "次");
                }
                else {
                    ((TopThreeHolder)holder).pickOrdernum.setText(String.valueOf(item.completeOrderNum) + "次");
                    ((TopThreeHolder)holder).completeOrdernum.setText(String.valueOf(item.pickOrderNum) + "次");
                    ((TopThreeHolder) holder).pubtitle.setText("接单数目");
                    ((TopThreeHolder) holder).fetchtitle.setText("发单数目");
                }
                switch (position) {
                    case 0: {
                        ((TopThreeHolder)holder).rankBG.setBackgroundResource(R.drawable.top1);
                        ((TopThreeHolder)holder).rankicon.setImageResource(R.drawable.rank_1);
                        break;
                    }
                    case 1: {
                        ((TopThreeHolder)holder).rankBG.setBackgroundResource(R.drawable.top2);
                        ((TopThreeHolder)holder).rankicon.setImageResource(R.drawable.rank_2);
                        break;
                    }
                    case 2: {
                        ((TopThreeHolder)holder).rankBG.setBackgroundResource(R.drawable.top3);
                        ((TopThreeHolder)holder).rankicon.setImageResource(R.drawable.rank_3);
                        break;
                    }
                    default:
                        break;
                }
            }
            else if (holder instanceof TopOtherHolder){
                ((TopOtherHolder) holder).rank_name.setText(item.name);
                if (MyRank.PorA.equals("submit")) {
                   ((TopOtherHolder) holder).rank_times.setText(String.valueOf(item.completeOrderNum)+"次");
                }
                else {
                    ((TopOtherHolder) holder).rank_times.setText(String.valueOf(item.pickOrderNum)+"次");
                }
               ((TopOtherHolder) holder).rank_num.setText(String .valueOf(position + 1));
            }
    }

    @Override
    public int getItemCount(){
        return mItemList.size();
    }
}