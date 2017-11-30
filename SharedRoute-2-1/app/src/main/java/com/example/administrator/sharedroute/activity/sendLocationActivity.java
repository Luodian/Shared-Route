package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
                    .LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_selectexpress);
        setSupportActionBar(toolbar);

        initView();
    }

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }

//    @Override
//    public void onBackPressed() {
//        JumpToActivity(MainActivity.class);
//        finish();
//    }

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
