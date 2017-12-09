package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.base.BaseDialog;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.fragment.PageFragment;
import com.example.administrator.sharedroute.fragment.WaitingFutureFragment;
import com.example.administrator.sharedroute.localdatabase.OrderDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.sharedroute.activity.MainActivity.select;


public class SearchNeedsActivity extends AppCompatActivity {

    public static int goodsCount;
    public static FloatingActionButton mfab;
    public static ArrayList<listItem> selectedItem;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private BottomNavigationView navigation;
    private SimpleFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private String tabTitles[] = new String[]{"一校区","二校区"};
    private DrawerLayout mDrawerLayout;
    private int curTab=0;
    private ActionBarDrawerToggle mDrawerToggle;

    private FetchUserInfo mFetchTask;
    private String usrid = "";
    private String usrphone = "";
    private double usraccount = 0;

    private TextView UserID;
    private TextView UserName;
    private TextView UserAccount;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_search_needs);
        if (!MainActivity.activityList.contains(SearchNeedsActivity.this)) MainActivity.activityList.add(SearchNeedsActivity.this);
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
                    .LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);

        OrderDao orderDao = new OrderDao(this);
        List<listItem> num = orderDao.getAllDate();
        if (num == null) goodsCount = 0;
        else goodsCount = num.size();

        toolbar.setTitle("寻找需求");
        setSupportActionBar(toolbar);
        BaseDialog.fromActivity = "search";
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }
        initView();
        selectedItem = new ArrayList<>();


        mfab = findViewById(R.id.fab);
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
        navigation = findViewById(R.id.search_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_notifications).setChecked(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {// drawer滑动的回调
                if (usrid != null) {
                    UserID.setText(MessageFormat.format("学号：{0}", usrid));
                    mFetchTask = new FetchUserInfo(usrid);
                    mFetchTask.execute();
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);

        View nav_header_view = navView.getHeaderView(0);

        UserID = nav_header_view.findViewById(R.id.nav_header_id);
        UserName = nav_header_view.findViewById(R.id.nav_header_name);
        UserAccount = nav_header_view.findViewById(R.id.nav_header_account);
        SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
        if (sp.getString("now_stu_num", null) != null) {
            UserID.setText(MessageFormat.format("学号：{0}", sp.getString("now_stu_num", null)));
        }
        UserName.setText(MessageFormat.format("电话：{0}", sp.getString("now_phone",null)));
        UserAccount.setText(MessageFormat.format("余额：{0}", sp.getString("now_account_money",null)));

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch (item.getItemId()){
                    case R.id.nav_shop:
                        select = "releaseOrder";
                        Intent intent1 = new Intent(SearchNeedsActivity.this,TaskViewActivity.class);
                        intent1.putExtra("select_order",select);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_release:
                        select = "releaseOrder";
                        Intent intent2 = new Intent(SearchNeedsActivity.this,MyPublishOrder.class);
                        intent2.putExtra("select_order",select);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_receive:
                        select = "receiveOrder";
                        Intent intent3 = new Intent(SearchNeedsActivity.this,MyFinishedActivity.class);
                        intent3.putExtra("select_order",select);
                        startActivity(intent3);
                        return true;
                    case R.id.release_rank:
                        select = "releaseRank";
                        Intent intent4 = new Intent(SearchNeedsActivity.this,WaitingFutureActivity.class);
                        intent4.putExtra("select_order",select);
                        startActivity(intent4);
                        return true;
                    case R.id.receive_rank:
//                        select = "receiveRank";
                        Intent intent5 = new Intent(SearchNeedsActivity.this,WaitingFutureActivity.class);
//                        intent5.putExtra("select_order",select);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_wallet:
                        Intent intent6 = new Intent(SearchNeedsActivity.this,BugSendActivity.class);
                        startActivity(intent6);
                        return true;
                    case R.id.nav_setting:
                        Intent intent7 = new Intent(SearchNeedsActivity.this,WaitingFutureActivity.class);
                        startActivity(intent7);
                        return true;
                    case R.id.nav_login:
                        Intent intent8 = new Intent(SearchNeedsActivity.this,LoginActivity.class);
                        intent8.putExtra("from","homePage");
                        startActivity(intent8);
                        finish();
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

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
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
        mTabLayout = this.findViewById(R.id.searchNeeds_tablayout);
        mViewPager = this.findViewById(R.id.searchNeeds_viewpager);


        PageFragment fragment = new PageFragment(curTab,SearchNeedsActivity.this);
        fragment.setTabPos(0);
        mFragments.add(fragment);

        WaitingFutureFragment waitingFutureFragment = new WaitingFutureFragment();
        mFragments.add(waitingFutureFragment);

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
                if ((mAdapter.getClass()).equals(PageFragment.class)) ((PageFragment)mAdapter.getItem(position)).sendMessage();
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

    public class FetchUserInfo extends AsyncTask<String, Void, Boolean> {

        private String id = null;
        private String result = null;

        FetchUserInfo(String UserID) {
            id = UserID;
        }

        @Override
        protected Boolean doInBackground(String... ID) {
            String result = null;
            String path = getResources().getString(R.string.url) + "/Task?action=FetchUserID&ID=" + id;
            HttpURLConnection con = null;
            InputStream in = null;

            try {
                URL url = new URL(path);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(5 * 1000);
                /*
                * http响应码：getResponseCode
                  200：成功 404：未找到 500：发生错误
              */
                if (con.getResponseCode() == 200) {
                    System.out.println("连接成功");
                    in = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    //InputStreamReader isr = new InputStreamReader(getAssets().open("get_data.json"),"UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    //StringBuilder 缓存区 StringBuffer
                    StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        builder.append(line);
                    }
                    br.close();
                    isr.close();
                    result = builder.toString();
                    if (result != "fail") {
                        System.out.println(builder.toString());
                        JSONObject lan = new JSONObject(result);
                        usrphone = lan.getString("Phone");
                        usraccount = lan.getDouble("Account");
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (con != null) {
                    con.disconnect();
                    //断开连接
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFetchTask = null;
            if (success) {
                UserName.setText(MessageFormat.format("电话：{0}", usrphone));
                UserAccount.setText(MessageFormat.format("余额：{0}", String.valueOf(usraccount)));

                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);

                sp.edit().putString("now_account_money", String.valueOf(usraccount)).commit();
            } else {
                Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
        }
    }
}

