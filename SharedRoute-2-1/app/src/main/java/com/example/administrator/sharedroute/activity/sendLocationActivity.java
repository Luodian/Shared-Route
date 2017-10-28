package com.example.administrator.sharedroute.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.MyFragmentPagerAdapter;

public class sendLocationActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getSupportActionBar().hide();

        setContentView(R.layout.activity_send_location);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_selectexpress);
        setSupportActionBar(toolbar);

        initView();
    }
    private void initView(){
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        myFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(myFragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
    }
}
