package com.example.administrator.sharedroute.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.GoodsModel;

import java.util.ArrayList;

/**
 * Created by luodian on 03/10/2017.
 */

public class SearchNeedsRcViewAdapter extends RecyclerView.Adapter<SearchNeedsRcViewAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private CallBackListener mCallBackListener;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public CardView mCardView;
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(final View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.searchNeeds_card_view);
            mTextView = (TextView) itemView.findViewById(R.id.searchNeeds_item_kinds);
            mImageView = (ImageView) itemView.findViewById(R.id.trolley_icon);
            mImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mImageView != null && mCallBackListener != null)
                                mCallBackListener.callBackImg(mImageView);
                                mImageView.setClickable(false);
                                mImageView.setImageResource(R.drawable.trolley_pressed);
                        }
                    });
        }

        public void updateUI(GoodsModel goods){
            if (goods != null
                    && goods.getmGoodsBitmap() != null
                    && mImageView != null)
                mImageView.setImageBitmap(goods.getmGoodsBitmap());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchNeedsRcViewAdapter(ArrayList<String> myDataset)
    {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchNeedsRcViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_search_needs, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset == null ? 0 : mDataset.size();
    }
    /**
     * 设置回调监听
     * @author leibing
     * @createTime 2016/09/28
     * @lastModify 2016/09/28
     * @param mCallBackListener 回调监听
     * @return
     */
    public void setCallBackListener(CallBackListener mCallBackListener){
        this.mCallBackListener = mCallBackListener;
    }

    /**
     * @interfaceName: CallBackListener
     * @interfaceDescription: 回调监听
     * @author: leibing
     * @createTime: 2016/09/28
     */
    public interface CallBackListener{
        void callBackImg(ImageView goodsImg);
    }
}
