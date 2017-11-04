package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.activity.SearchNeedsActivity;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;

import java.util.ArrayList;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;

import static com.example.administrator.sharedroute.activity.SearchNeedsActivity.selectedItem;

public class PullRecyclerViewAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private ArrayList<listItem> mDataset;
    private CallBackListener mCallBackListener;
    private OrderDao orderDao;
    private Context mContext;
    private TabLayout mTablayout;
    private boolean isMoreData = true;

    public PullRecyclerViewAdapter(ArrayList<listItem> mDataset ,TabLayout mTablayout) {
        this.mDataset = mDataset;
        this.mTablayout = mTablayout;
    }

    @Override
    public int getItemCount() {
       return mDataset.size() == 0 ? 0 : mDataset.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            if (mContext == null) {
                mContext = parent.getContext();
            }
            view = LayoutInflater.from(mContext).inflate(R.layout.content_search_needs, parent, false);
            if (orderDao == null) orderDao = new OrderDao(mContext);
            final ItemViewHolder holder = new ItemViewHolder(view);
            return holder;
        }
        else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent,
                    false);
            Log.e("!!!!!","111111111111");
            return new FootViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            //holder.tv.setText(data.get(position));
            ((ItemViewHolder) holder).kindsTextView.setText(mDataset.get(position).TaskKindID);
            ((ItemViewHolder) holder).fetchTimeTextView.setText(mDataset.get(position).FetchTime);
            ((ItemViewHolder) holder).sendTimeTextView.setText(mDataset.get(position).SendTime);
            ((ItemViewHolder) holder).fetchLocTextView.setText(mDataset.get(position).FetchLocation);
            ((ItemViewHolder) holder).sendLocTextView.setText(mDataset.get(position).SendLocation);
            ((ItemViewHolder) holder).priceTextView.setText(String.valueOf(mDataset.get(position).Money) + "元");
            ((ItemViewHolder) holder).name.setText(mDataset.get(position).PublisherName);
            ((ItemViewHolder) holder).mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    FlipShareView shareBottom = new FlipShareView.Builder((SearchNeedsActivity)mContext, mTablayout)
                            .addItem(new ShareItem("发布者："+mDataset.get(position).PublisherName, Color.WHITE,0xff00bcd4))
                            .addItem(new ShareItem("联系方式："+mDataset.get(position).PublisherPhone, Color.WHITE, 0xff00bcd4))
                            .addItem(new ShareItem("类型："+mDataset.get(position).TaskKindID, Color.WHITE, 0xff00bcd4))
                            .addItem(new ShareItem("描述："+mDataset.get(position).Remark, Color.WHITE, 0xff00bcd4))
                            .addItem(new ShareItem("取件时间："+mDataset.get(position).FetchTime, Color.WHITE, 0xffff4081))
                            .addItem(new ShareItem("取件地点："+mDataset.get(position).FetchLocation, Color.WHITE, 0xffff4081))
                            .addItem(new ShareItem("送件时间："+mDataset.get(position).SendTime, Color.WHITE, 0xffff4081))
                            .addItem(new ShareItem("送件地点："+mDataset.get(position).SendLocation, Color.WHITE, 0xffff4081))
                            .addItem(new ShareItem("价格："+mDataset.get(position).Money, Color.WHITE,0xffff4081))
                            .setItemDuration(200)
                            .setBackgroundColor(0x60000000)
                            .setAnimType(FlipShareView.TYPE_SLIDE)
                            .create();
                    return true;
                }
            });
            int num = orderDao.getItemCount(mDataset.get(position).ID);
            if (num != 0)  ((ItemViewHolder) holder).mImageView.setImageResource(R.drawable.trolley_pressed);
            else {
                ((ItemViewHolder) holder).mImageView.setOnClickListener(new View.OnClickListener() {
                    //说实话，这样监听不太好，每次滑动getView的时候都要重新new一个监听，但是必须获取ChechView所在的那个Item的position，所以只能卸载getView函数内部
                    @Override
                    public void onClick(View v) {//等于说对于那10个左右的ChechBox,其绑定的监听在不断的改变
                        if ( ((ItemViewHolder) holder).mImageView != null && mCallBackListener != null)
                            mCallBackListener.callBackImg( ((ItemViewHolder) holder).mImageView);
                        assert  ((ItemViewHolder) holder).mImageView != null;
                        ((ItemViewHolder) holder).mImageView.setClickable(false);
                        ((ItemViewHolder) holder).mImageView.setImageResource(R.drawable.trolley_pressed);
                        listItem item = mDataset.get(position);
                        orderDao.insertData(item);
                        Log.e("rrr", "!!!");
                        selectedItem.add(item);
                    }
                });
            }
        }
//        else
//        {
//            if (!isMoreData)
//            {
//                ((FootViewHolder) holder).pb.setVisibility(View.INVISIBLE);
//                ((FootViewHolder) holder).tips.setText("已经加载完毕...");
//            }
//            else
//            {
//                ((FootViewHolder) holder).pb.setVisibility(View.VISIBLE);
//                ((FootViewHolder) holder).tips.setText("正在加载中...");
//            }
//        }
    }

    public void setNoMoreData()
    {
        isMoreData = false;
    }

    public void setHasMoreData()
    {
        isMoreData = true;
    }

    public void setCallBackListener(CallBackListener mCallBackListener){
        this.mCallBackListener = mCallBackListener;
    }

    public interface CallBackListener{
        void callBackImg(ImageView goodsImg);
    }

    private static class ItemViewHolder extends ViewHolder {
        // each data item is just a string in this case
        CardView mCardView;
        TextView kindsTextView;
        TextView priceTextView;
        TextView sendTimeTextView;
        TextView fetchTimeTextView;
        TextView sendLocTextView;
        TextView fetchLocTextView;
        ImageView mImageView;
        TextView name;
        ItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.searchNeeds_card_view);

            kindsTextView = (TextView) itemView.findViewById(R.id.searchNeeds_item_kinds);
            priceTextView = (TextView) itemView.findViewById(R.id.searchNeeds_item_price);
            sendTimeTextView = (TextView) itemView.findViewById(R.id.searchNeeds_send_time);
            fetchTimeTextView = (TextView) itemView.findViewById(R.id.searchNeeds_fetch_time);
            sendLocTextView = (TextView) itemView.findViewById(R.id.searchNeeds_send_loc);
            fetchLocTextView = (TextView) itemView.findViewById(R.id.searchNeeds_fetch_loc);
            name = (TextView)itemView.findViewById(R.id.searchneed_name);
            mImageView = (ImageView) itemView.findViewById(R.id.trolley_icon);
        }
    }

    private static class FootViewHolder extends ViewHolder {
        TextView tips;
        ProgressBar pb;
        FootViewHolder(View view) {
            super(view);
            pb = (ProgressBar) view.findViewById(R.id.progress);
            tips = (TextView) view.findViewById(R.id.text_footer);
        }
    }
}