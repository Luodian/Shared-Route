package com.example.administrator.sharedroute.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.activity.BlurredActivity;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.listener.OnBlurCompleteListener;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.widget.BlurBehind;

import java.util.ArrayList;

import static com.example.administrator.sharedroute.activity.SearchNeedsActivity.selectedItem;

public class PullRecyclerViewAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private ArrayList<listItem> mDataset;
    private CallBackListener mCallBackListener;
    private OrderDao orderDao;
    private Context mContext;

    public PullRecyclerViewAdapter(ArrayList<listItem> mDataset) {
        this.mDataset = mDataset;
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
            holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    BlurBehind.getInstance().execute((Activity) mContext, new OnBlurCompleteListener() {
                        final int position = holder.getAdapterPosition();

                        @Override
                        public void onBlurComplete() {
                            Intent intent = new Intent(mContext, BlurredActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("title_name", "title" + Integer.toString(position + 1));
                            intent.putExtra("select", "release");
                            intent.putExtra("Activity", "SearchNeeds");
                            mContext.startActivity(intent);
                        }
                    });
                    return true;
                }
            });
            return holder;
        }
        else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            //holder.tv.setText(data.get(position));
            ((ItemViewHolder) holder).kindsTextView.setText(mDataset.get(position).getExpressType());
            ((ItemViewHolder) holder).fetchTimeTextView.setText(mDataset.get(position).getInTimeStamp());
            ((ItemViewHolder) holder).sendTimeTextView.setText(mDataset.get(position).getOutTimeStamp());
            ((ItemViewHolder) holder).fetchLocTextView.setText(mDataset.get(position).getInLocation());
            ((ItemViewHolder) holder).sendLocTextView.setText(mDataset.get(position).getOutLocation());
            ((ItemViewHolder) holder).priceTextView.setText(String.valueOf(mDataset.get(position).getPrice()) + "元");

            ((ItemViewHolder) holder).mImageView.setOnClickListener(new View.OnClickListener() {
                //说实话，这样监听不太好，每次滑动getView的时候都要重新new一个监听，但是必须获取ChechView所在的那个Item的position，所以只能卸载getView函数内部
                @Override
                public void onClick(View v) {//等于说对于那10个左右的ChechBox,其绑定的监听在不断的改变
                    if (((ItemViewHolder) holder).mImageView != null && mCallBackListener != null)
                        mCallBackListener.callBackImg(((ItemViewHolder) holder).mImageView);
                    assert ((ItemViewHolder) holder).mImageView != null;
                    ((ItemViewHolder) holder).mImageView.setClickable(false);
                    ((ItemViewHolder) holder).mImageView.setImageResource(R.drawable.trolley_pressed);
                    listItem item = mDataset.get(position);
                    item.setPickupCode(String.valueOf((int) (Math.random() * 1000000)));
                    orderDao.insertData(item);
                    Log.e("rrr", "!!!");
                    selectedItem.add(item);
                }
            });
        }
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
        ItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.searchNeeds_card_view);

            kindsTextView = (TextView) itemView.findViewById(R.id.searchNeeds_item_kinds);
            priceTextView = (TextView) itemView.findViewById(R.id.searchNeeds_item_price);
            sendTimeTextView = (TextView) itemView.findViewById(R.id.searchNeeds_send_time);
            fetchTimeTextView = (TextView) itemView.findViewById(R.id.searchNeeds_fetch_time);
            sendLocTextView = (TextView) itemView.findViewById(R.id.searchNeeds_send_loc);
            fetchLocTextView = (TextView) itemView.findViewById(R.id.searchNeeds_fetch_loc);

            mImageView = (ImageView) itemView.findViewById(R.id.trolley_icon);
        }
    }

    private static class FootViewHolder extends ViewHolder {

        FootViewHolder(View view) {
            super(view);
        }
    }
}