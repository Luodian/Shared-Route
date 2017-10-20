package com.example.administrator.sharedroute.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sharedroute.R;

import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter{
    private List<CardView> mViewList;
    private float mBaseElevation;

    public CardPagerAdapter(List<CardView> mViewList) {
        this.mViewList = mViewList;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViewList.get(position);
    }

    @Override
    public int getCount() {
        return mViewList.size();//页卡数
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//官方推荐写法
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));//添加页卡
        CardView cardView = (CardView)mViewList.get(position).findViewById(R.id.cardView);
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViewList.set(position, cardView);
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));//删除页卡
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

}