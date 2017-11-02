package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.AcceptedOrderItemAdapter;
import com.example.administrator.sharedroute.adapter.MyPagerAdapter;
import com.example.administrator.sharedroute.adapter.ReleaseOrderItemAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.widget.BannerPager;
import com.example.administrator.sharedroute.widget.BannerPager.BannerClickListener;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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

import static com.example.administrator.sharedroute.R.layout.activity_receive_order;
import static com.example.administrator.sharedroute.R.layout.activity_release_order;

public class MainActivity extends AppCompatActivity implements BannerClickListener {

    private BannerPager mBanner;
    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private LayoutInflater mInflater;
    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    public static String select = "releaseOrder";
    private List<listItem> itemAcceptList = new ArrayList<>();
    private List<listItem> itemPublishList = new ArrayList<>();
    private AcceptedOrderItemAdapter adapter2;
    private ReleaseOrderItemAdapter adapter1;
    private int mMenuId;
    private OrderDao orderDao;
    private BottomNavigationView navigation;
    private SwipeRefreshLayout swipeRefresh1;
    private SwipeRefreshLayout swipeRefresh2;
    private AnimationAdapter mAnimAdapter;
    private int status;
    private View view;
    private ImageView userHeader;
    private ImageView phoneImage;
    private TextView userName;
    private TextView userPhone;
    private TextView releaseTime;
    private TextView fetchLocation;
    private ImageView statusImage;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//		view = mInflater.inflate(release_order_item_layout, null);
//		userHeader = (ImageView)view.findViewById(R.id.user_header);
//		phoneImage = (ImageView)view.findViewById(R.id.phone_image);
//		userName = (TextView) view.findViewById(R.id.user_name);
//		userPhone = (TextView) view.findViewById(R.id.user_phone);
//		releaseTime = (TextView) view.findViewById(R.id.release_time);
//		fetchLocation = (TextView) view.findViewById(R.id.fetch_location);
//		statusImage = (ImageView) view.findViewById(R.id.status_image);
//		statusText = (TextView) view.findViewById(R.id.status_text);
//		Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/youyuan.TTF");
//		userName.setTypeface(typeFace);
//		userPhone.setTypeface(typeFace);
//		releaseTime.setTypeface(typeFace);
//		fetchLocation.setTypeface(typeFace);
//		statusText.setTypeface(typeFace);
        orderDao = new OrderDao(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.nav_shop:
                        select = "releaseOrder";
                        Intent intent1 = new Intent(MainActivity.this,TaskViewActivity.class);
                        intent1.putExtra("select_order",select);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_release:
                        select = "releaseOrder";
                        Intent intent2 = new Intent(MainActivity.this,MyPublishOrder.class);
                        intent2.putExtra("select_order",select);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_receive:
                        select = "receiveOrder";
                        Intent intent3 = new Intent(MainActivity.this,MyFinishedActivity.class);
                        intent3.putExtra("select_order",select);
                        startActivity(intent3);
                        return true;
                    case R.id.release_rank:
                        select = "releaseRank";
                        Intent intent4 = new Intent(MainActivity.this,WaitingFutureActivity.class);
                        intent4.putExtra("select_order",select);
                        startActivity(intent4);
                        return true;
                    case R.id.receive_rank:
//                        select = "receiveRank";
                        Intent intent5 = new Intent(MainActivity.this,WaitingFutureActivity.class);
//                        intent5.putExtra("select_order",select);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_wallet:
                        Intent intent6 = new Intent(MainActivity.this,WaitingFutureActivity.class);
                        startActivity(intent6);
                        return true;
                    case R.id.nav_setting:
                        Intent intent7 = new Intent(MainActivity.this,WaitingFutureActivity.class);
                        startActivity(intent7);
                        return true;
                    case R.id.nav_login:
                        Intent intent8 = new Intent(MainActivity.this,LoginActivity.class);
                        intent8.putExtra("from","homePage");
                        startActivity(intent8);
                        return true;
                    default:
                }
                return true;
            }
        });

        mBanner = (BannerPager) findViewById(R.id.banner_pager);
        LayoutParams params = (LayoutParams) mBanner.getLayoutParams();
        params.height = (int) (com.example.administrator.sharedroute.utils.DisplayUtil.getSreenWidth(this) * 250f / 640f);
        mBanner.setLayoutParams(params);
        ArrayList<Integer> bannerArray = new ArrayList<>();
        bannerArray.add(R.drawable.banner_1);
        bannerArray.add(R.drawable.banner_2);
        bannerArray.add(R.drawable.banner_3);
        bannerArray.add(R.drawable.banner_4);
        bannerArray.add(R.drawable.banner_5);
        mBanner.setImage(bannerArray);
        mBanner.setOnBannerListener(this);
        mBanner.start();

        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mInflater = LayoutInflater.from(this);
        view1 = mInflater.inflate(activity_release_order, null);
        view2 = mInflater.inflate(activity_receive_order, null);
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

        mLinearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        // 在所有子控件的中间显示分割线（还可能只显示顶部、尾部和不显示分割线）
        mLinearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        // 设置分割线的距离本身（LinearLayout）的内间距
        mLinearLayout.setDividerPadding(50);
        // 设置分割线的样式
        mLinearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.divider_vertical));



        navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

        swipeRefresh1 = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_refresh_release);
        swipeRefresh1.setColorSchemeColors(Color.RED, Color.CYAN);
        swipeRefresh1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh1.setRefreshing(true);
                new refreshKeep().execute();
            }
        });

        swipeRefresh1.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh1.setRefreshing(true);
                new refreshKeep().execute();
            }
        });

        swipeRefresh2 = (SwipeRefreshLayout) view2.findViewById(R.id.swipe_refresh_receive);
        swipeRefresh2.setColorSchemeColors(Color.RED, Color.CYAN);
        swipeRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh2.setRefreshing(true);
                new refreshKeepTwo().execute();
            }
        });

        swipeRefresh2.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh1.setRefreshing(true);
                new refreshKeepTwo().execute();
            }
        });
    }

    @Override
    public void onBannerClick(int position) {
        switch (position+1){
            case 1:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://github.com/Luodian/Shared-Route"));
                startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://github.com/Luodian/Shared-Route"));
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                intent3.setData(Uri.parse("https://github.com/Luodian/Shared-Route"));
                startActivity(intent3);
                break;
            case 4:
                Intent intent4 = new Intent(Intent.ACTION_VIEW);
                intent4.setData(Uri.parse("https://github.com/Luodian/Shared-Route"));
                startActivity(intent4);
                break;
            case 5:
                Intent intent5 = new Intent(Intent.ACTION_VIEW);
                intent5.setData(Uri.parse("https://github.com/Luodian/Shared-Route"));
                startActivity(intent5);
                break;
            default:
        }
    }

    private class refreshKeep extends AsyncTask<Void, Void,ArrayList<listItem>> {

        @Override
        protected ArrayList<listItem> doInBackground(Void... pa) {
            String result = null;
            String path = "http://hitschool.free.ngrok.cc/sharedroot_server/Task";
            HttpURLConnection con = null;
            InputStream in = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(path);
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//                    String json = new String();
//                    json += "[";
//                    for (int i = 0; i < length; i++) {
//                        json += "{\"id\":" + itemPublishList.get(i).ID + "}";
//                        if (i != (length - 1)) json += ",";
//                        else json += "]";
//                    }
//                    System.out.println(json);
//                    parameters.add(new BasicNameValuePair("name", json));
                parameters.add(new BasicNameValuePair("action", "publishpost"));
                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
                String stuNum=sp.getString("now_stu_num",null);
                parameters.add(new BasicNameValuePair("PublisherID", stuNum));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                JSONArray arr = new JSONArray(result.toString());
                if (itemPublishList == null) itemPublishList = new ArrayList<listItem>();
                else  itemPublishList.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject lan = arr.getJSONObject(i);
                    listItem item = new listItem();
                    item.ID = lan.getInt("ID");
                    item.Money = lan.getDouble("Money");
                    item.PickID = lan.getString("PickID");
                    item.TaskKindID = lan.getString("TaskkindID");
                    item.PublisherName = lan.getString("PublisherName");
                    item.PublisherPhone = lan.getString("PublisherPhone");
                    item.FetchTime = lan.getString("FetchTime");
                    item.FetchLocation = lan.getString("FetchLocation");
                    item.FetcherPhone = lan.getString("FetcherPhone");
                    item.FetcherName = lan.getString("FetcherName");
                    item.FetcherID = lan.getString("FetcherID");
                    item.SendTime = lan.getString("SendTime");
                    item.SendLocation = lan.getString("SendLocation");
                    item.PublisherID = lan.getString("PublisherID");
                    item.PromiseMoney = lan.getDouble("PromiseMoney");
                    item.status = lan.getInt("Status");
                    itemPublishList.add(item);
                }
                Log.e("0-",String.valueOf(itemPublishList.size()));
                return (ArrayList<listItem>) itemPublishList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<listItem> data) {
            super.onPostExecute(data);
            if (swipeRefresh1 != null) swipeRefresh1.setRefreshing(false);
            if (itemPublishList.size()==0) Toast.makeText(MainActivity.this,"无数据更新",Toast.LENGTH_SHORT).show();
            RecyclerView releaseOrder = (RecyclerView) view1.findViewById(R.id.release_order);
            GridLayoutManager layoutManager1 = new GridLayoutManager(MainActivity.this, 1);
            releaseOrder.setLayoutManager(layoutManager1);
            adapter1 = new ReleaseOrderItemAdapter(itemPublishList);
            releaseOrder.setAdapter(adapter1);
        }
    }

    private class refreshKeepTwo extends AsyncTask<Void, Void,ArrayList<listItem>> {

        @Override
        protected ArrayList<listItem> doInBackground(Void ... pa) {
            String result = null;
            String path = "http://hitschool.free.ngrok.cc/sharedroot_server/Task";
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(path);
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//                    String json = new String();
//                    json += "[";
//                    for (int i = 0; i < length; i++) {
//                        json += "{\"id\":" + itemPublishList.get(i).ID + "}";
//                        if (i != (length - 1)) json += ",";
//                        else json += "]";
//                    }
//                    System.out.println(json);
//                    parameters.add(new BasicNameValuePair("name", json));
                //
                parameters.add(new BasicNameValuePair("action", "acceptpost"));
                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
                String stuNum=sp.getString("now_stu_num",null);
                parameters.add(new BasicNameValuePair("FetcherID", stuNum));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                JSONArray arr = new JSONArray(result.toString());
                if (itemAcceptList == null) itemAcceptList = new ArrayList<listItem>();
                else itemAcceptList.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject lan = arr.getJSONObject(i);
                    listItem item = new listItem();
                    item.ID = lan.getInt("id");
                    item.Money = lan.getDouble("Money");
                    item.PickID = lan.getString("PickID");
                    item.TaskKindID = lan.getString("TaskkindID");
                    item.PublisherName = lan.getString("PublisherName");
                    item.PublisherPhone = lan.getString("PublisherPhone");
                    item.FetchTime = lan.getString("FetchTime");
                    item.FetchLocation = lan.getString("FetchLocation");
                    item.FetcherPhone = lan.getString("FetcherPhone");
                    item.FetcherName = lan.getString("FetcherName");
                    item.FetcherID = lan.getString("FetcherID");
                    item.SendTime = lan.getString("SendTime");
                    item.SendLocation = lan.getString("SendLocation");
                    item.PublisherID = lan.getString("PublisherID");
                    item.PromiseMoney = lan.getDouble("PromiseMoney");
                    item.status = lan.getInt("Status");
                    itemAcceptList.add(item);
                }
                Log.e("0-",String.valueOf(itemAcceptList.size()));
                return  (ArrayList<listItem>) itemAcceptList;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<listItem> data) {
            super.onPostExecute(data);
            if (swipeRefresh1 != null) swipeRefresh1.setRefreshing(false);
            if (itemAcceptList.size()==0) Toast.makeText(MainActivity.this,"无数据更新",Toast.LENGTH_SHORT).show();
            RecyclerView receiveOrder = (RecyclerView) view2.findViewById(R.id.receive_order);
            GridLayoutManager layoutManager2 = new GridLayoutManager(MainActivity.this, 1);
            receiveOrder.setLayoutManager(layoutManager2);
            adapter2 = new AcceptedOrderItemAdapter(itemAcceptList);
            receiveOrder.setAdapter(adapter2);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//			mMenuId = item.getItemId();
//			for (int i = 0; i < navigation.getMenu().size(); i++) {
//				MenuItem menuItem = navigation.getMenu().getItem(i);
//				boolean isChecked = menuItem.getItemId() == item.getItemId();
//				menuItem.setChecked(isChecked);
//			}
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    JumpToActivity(PublishNeedsActivity.class);
                    finish();
                    return true;
                case R.id.navigation_notifications:
                    JumpToActivity(SearchNeedsActivity.class);
                    finish();
                    return true;
            }
            return false;
        }

    };
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//    }

    @Override
    public void onPause() {
        super.onPause();
        mDrawerLayout.closeDrawers();
    }


    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }


}
