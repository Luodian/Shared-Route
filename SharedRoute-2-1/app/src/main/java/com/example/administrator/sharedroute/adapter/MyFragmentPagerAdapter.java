package com.example.administrator.sharedroute.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.sharedroute.fragment.Fragment_campus_one;
import com.example.administrator.sharedroute.fragment.Fragment_campus_two;

/**
 * Created by 王烨臻 on 2017/9/25.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"一校区","二校区"};
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new Fragment_campus_one();
            }
            case 1:{
                return new Fragment_campus_two();
            }
            default: return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
