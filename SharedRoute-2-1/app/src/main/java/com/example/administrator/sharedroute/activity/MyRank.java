package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyRank extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private String select;

    private String [] receive_rank_data = {"item1","item2","item3","item4","item5"
            ,"item6","item7","item8","item9","item10"};
    private String [] release_rank_data = {"item1","item2","item3","item4","item5"
            ,"item6","item7","item8","item9","item10"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rank);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("排行榜");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mInflater = LayoutInflater.from(this);

        view1 = mInflater.inflate(R.layout.activity_release_rank, null);
        view2 = mInflater.inflate(R.layout.activity_receive_rank, null);
        ArrayAdapter<String> ReleaseAdapter = new ArrayAdapter<String>(MyRank.this,android.R.layout.simple_list_item_1,release_rank_data);
        ListView ReleaseListView = (ListView)view1.findViewById(R.id.release_rank);
        ReleaseListView.setAdapter(ReleaseAdapter);
        ArrayAdapter<String> ReceiveAdapter = new ArrayAdapter<String>(MyRank.this,android.R.layout.simple_list_item_1,receive_rank_data);
        ListView ReceiveListView = (ListView)view2.findViewById(R.id.receive_rank);
        ReceiveListView.setAdapter(ReceiveAdapter);

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        //给ViewPager设置适配器
        mViewPager.setAdapter(mAdapter);
        //将TabLayout和ViewPager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        //给Tabs设置适配器
        mTabLayout.setTabsFromPagerAdapter(mAdapter);

        Intent intent = getIntent();
        select = intent.getStringExtra("select_order");
        Log.d("MyOrder",select);
        if(select.equals( "releaseRank")){
            mViewPager.setCurrentItem(0);
        }else{
            mViewPager.setCurrentItem(1);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                select = "";
                finish();
                return true;
        }
        return true;
    }

}
