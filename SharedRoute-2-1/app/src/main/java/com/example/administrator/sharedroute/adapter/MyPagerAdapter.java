package com.example.administrator.sharedroute.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sharedroute.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends PagerAdapter {
    private List<View> mViewList;
    private List<String> mTitleList = new ArrayList<String>();

    public MyPagerAdapter(List<View> mViewList) {
        this.mViewList = mViewList;
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
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));//删除页卡
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //添加页卡标题
        if(MainActivity.select.equals("releaseOrder") || MainActivity.select.equals("receiveOrder")) {
            mTitleList.add("已发布订单");
            mTitleList.add("已接受订单");
        }else{
            mTitleList.add("发单排行");
            mTitleList.add("接单排行");
        }
        return mTitleList.get(position);//页卡标题
    }

}