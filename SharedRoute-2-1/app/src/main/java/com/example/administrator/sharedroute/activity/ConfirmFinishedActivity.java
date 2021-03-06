package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.DialogMenuItem;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.fragment.FailFragment;
import com.example.administrator.sharedroute.fragment.SuccessFragment;
import com.example.administrator.sharedroute.localdatabase.OrderDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConfirmFinishedActivity extends AppCompatActivity {

    public static ArrayList<DialogMenuItem> testItems = new ArrayList<>();
    private TabLayout mTabLayout;
    private PagerAdapter mpagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<listItem> successList ;
    private ArrayList<listItem> failList ;
    private Button mButton;
    public static int lastPosition = 0;
    private  ArrayList<listItem> arrayList;
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
            case R.id.back:
                clearTrolly();
                startActivity(new Intent(ConfirmFinishedActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        clearTrolly();
        startActivity(new Intent(ConfirmFinishedActivity.this,MainActivity.class));
        finish();
    }

    private void initView(){
        mTabLayout = (TabLayout) findViewById(R.id.confirmfinished_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.confirmfinished_viewpager);
        mButton = (Button) findViewById(R.id.confirm_finished_return);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTrolly();
                Intent intent =new Intent(ConfirmFinishedActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        if (bundle!=null ){
            successList= bundle.getParcelableArrayList("successList");
            failList = bundle.getParcelableArrayList("failList");
        }
        if (successList == null) successList = new ArrayList<listItem>();
        if (failList == null) failList = new ArrayList<listItem>();
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
        //注册PageViewr的Adapter

    }

    private void clearTrolly(){
        OrderDao orderDao =new OrderDao(this);
        List<listItem> arrayList = orderDao.getAllDate();
        if (arrayList != null)
        for (listItem e:arrayList) orderDao.deleteOrder(e);
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