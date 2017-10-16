package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.DialogMenuItem;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.fragment.FailFragment;
import com.example.administrator.sharedroute.fragment.SuccessFragment;

import java.util.ArrayList;
import java.util.List;

public class ConfirmFinishedActivity extends AppCompatActivity {

    public static ArrayList<DialogMenuItem> testItems = new ArrayList<>();
    private List<listItem> listItemList;
    private TabLayout mTabLayout;
    private PagerAdapter mpagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<listItem> successList = new ArrayList<listItem>();
    private ArrayList<listItem> failList = new ArrayList<listItem>();
    private FrameLayout mFrameLayout;
    public static int lastPosition = 0;
    private int mMenuId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_finished);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("接单详情");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.back:
                startActivity(new Intent(ConfirmFinishedActivity.this,MainActivity.class));
                return true;
        }
        return true;
    }

    private void initView(){
        mTabLayout = (TabLayout) findViewById(R.id.confirmfinished_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.confirmfinished_viewpager);
        mFrameLayout = (FrameLayout) findViewById(R.id.confirm_finished_return);
        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ConfirmFinishedActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        listItemList= bundle.getParcelableArrayList("listItemList");
        randomApart(listItemList);
        //注册PageViewr的Adapter
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(),ConfirmFinishedActivity.this);


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
    private void randomApart(List<listItem> listItemList){
        for (int i =0 ;i<listItemList.size();i++){
            double random = Math.random();
            if (random < 0.5){
                successList.add(listItemList.get(i));
            }
            else{
                failList.add(listItemList.get(i));
            }
        }
    }


    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "成功接单项","失败接单项"};
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
                case 0:{
                    SuccessFragment successFragment = new SuccessFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("listItemList",successList);
                    successFragment.setArguments(bundle);
                    return successFragment;
                }
                case 1: {
                    FailFragment failFragment = new FailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("listItemList",failList);
                    failFragment.setArguments(bundle);
                    return failFragment;
                }
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}