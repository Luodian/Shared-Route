package com.example.administrator.sharedroute.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.SearchNeedsRcViewAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.fragment.PageFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.sharedroute.activity.MainActivity.select;


public class SearchNeedsActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private BottomNavigationView navigation;

    private SimpleFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private String tabTitles[] = new String[]{"一校区","二校区","三校区"};

    public static int goodsCount = 0;
    public static FloatingActionButton mfab;
    private DrawerLayout mDrawerLayout;

    private int curTab=0;

    public static ArrayList<listItem> selectedItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_search_needs);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("寻找需求");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        selectedItem = new ArrayList<>();
        mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent =new Intent(SearchNeedsActivity.this,TaskViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listItemList",selectedItem);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        navigation = (BottomNavigationView) findViewById(R.id.search_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_notifications).setChecked(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_shop:
                        select = "releaseOrder";
                        Intent intent1 = new Intent(SearchNeedsActivity.this,TaskViewActivity.class);
                        intent1.putExtra("select_order",select);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_release:
                        select = "releaseOrder";
                        Intent intent2 = new Intent(SearchNeedsActivity.this,MyOrder.class);
                        intent2.putExtra("select_order",select);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_receive:
                        select = "receiveOrder";
                        Intent intent3 = new Intent(SearchNeedsActivity.this,MyOrder.class);
                        intent3.putExtra("select_order",select);
                        startActivity(intent3);
                        return true;
                    case R.id.release_rank:
                        select = "releaseRank";
                        Intent intent4 = new Intent(SearchNeedsActivity.this,MyRank.class);
                        intent4.putExtra("select_order",select);
                        startActivity(intent4);
                        return true;
                    case R.id.receive_rank:
                        select = "receiveRank";
                        Intent intent5 = new Intent(SearchNeedsActivity.this,MyRank.class);
                        intent5.putExtra("select_order",select);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_wallet:
                        Intent intent6 = new Intent(SearchNeedsActivity.this,BandCard.class);
                        startActivity(intent6);
                        return true;
                    case R.id.nav_setting:
                        return true;
                    default:
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        JumpToActivity(MainActivity.class);
        finish();
    }

    @Override
    public void onPause(){
        super.onPause();
        mDrawerLayout.closeDrawers();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    JumpToActivity(MainActivity.class);
                    finish();
                    return true;
                case R.id.navigation_dashboard:
                    JumpToActivity(PublishNeedsActivity.class);
                    finish();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
        overridePendingTransition(0,0);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                JumpToActivity(MainActivity.class);
                finish();
                return true;
        }
        return true;
    }


    private void initView() {
        mTabLayout = (TabLayout) this.findViewById(R.id.searchNeeds_tablayout);
        mViewPager = (ViewPager) this.findViewById(R.id.searchNeeds_viewpager);

        for(int i=0; i<tabTitles.length; i++){
            PageFragment fragment = new PageFragment(curTab);
            fragment.setTabPos(i);
            mFragments.add(fragment);
        }
        mAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((PageFragment)mAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        SimpleFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //防止fragment自动销毁
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}

