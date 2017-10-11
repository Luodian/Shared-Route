package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.fragment.OneFragment;
import com.example.administrator.sharedroute.fragment.ThreeFragment;
import com.example.administrator.sharedroute.fragment.TwoFragment;

public class SearchNeedsActivity extends AppCompatActivity
{
    private TabLayout mTabLayout;
    private PagerAdapter mpagerAdapter;
    private ViewPager mViewPager;

    public static int lastPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_needs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchNeeds_toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.searchNeeds_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.searchNeeds_viewpager);

        //注册PageViewr的Adapter
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(),SearchNeedsActivity.this);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager,true);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                lastPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });


    }
    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "一校区", "二校区", "三校区" };
        Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new OneFragment();
                case 1:
                    return new TwoFragment();
                case 2:
                    return new ThreeFragment();
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}

