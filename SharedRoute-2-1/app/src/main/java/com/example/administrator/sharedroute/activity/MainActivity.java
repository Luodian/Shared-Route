package com.example.administrator.sharedroute.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.AcceptedOrderItemAdapter;
import com.example.administrator.sharedroute.adapter.MainPageReleaseAdapter;
import com.example.administrator.sharedroute.adapter.ReleaseOrderItemAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.utils.DisplayUtil;
import com.example.administrator.sharedroute.widget.BannerPager;
import com.example.administrator.sharedroute.widget.BannerPager.BannerClickListener;

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
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BannerClickListener,View.OnClickListener {

//    private static final String HOST = "47.95.194.146";
//    private static final int PORT = 9986;

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
    public static List<Activity> activityList = new ArrayList<Activity>();
    private OrderDao orderDao;
    private BottomNavigationView navigation;
    private SwipeRefreshLayout swipeRefresh1;
    private SwipeRefreshLayout swipeRefresh2;

    private FetchUserInfo mFetchTask;
    public String usrid = "";
    public String usrphone = "";
    public double usraccount = 0;

    private TextView UserID;
    private TextView UserName;
    private TextView UserAccount;
    private RecyclerView mRecyclerView;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView9;
    private ImageView imageView10;
    private ImageView imageView11;
    private ImageView imageView12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!activityList.contains(MainActivity.this)) activityList.add(MainActivity.this);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置状态栏颜色
            getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        }
        Bundle bundle = getIntent().getExtras();   //得到传过来的bundle
        if (bundle != null) {
            usrid = bundle.getString("ID");
        }
        else
        {
            SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
            usrid = sp.getString("now_stu_num",null);
        }

        orderDao = new OrderDao(this);
//        if (!orderDao.isDataExist()) orderDao.initTable();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

       // View nav_header_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.nav_header,null);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        View nav_header_view = navView.getHeaderView(0);

        UserID = nav_header_view.findViewById(R.id.nav_header_id);
        UserName = nav_header_view.findViewById(R.id.nav_header_name);
        UserAccount = nav_header_view.findViewById(R.id.nav_header_account);

        if (usrid != null) {
            UserID.setText(MessageFormat.format("学号：{0}", usrid));
            mFetchTask = new FetchUserInfo(usrid);
            mFetchTask.execute();
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
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
                        Intent intent4 = new Intent(MainActivity.this,RankActivity.class);
                        intent4.putExtra("select_order",select);
                        startActivity(intent4);
                        return true;
                    case R.id.receive_rank:
//                        select = "receiveRank";
                        Intent intent5 = new Intent(MainActivity.this,RankActivity.class);
//                        intent5.putExtra("select_order",select);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_wallet:
                        Intent intent6 = new Intent(MainActivity.this,BugSendActivity.class);
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
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageView10 = findViewById(R.id.imageView10);
        imageView11 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);
        imageView6.setOnClickListener(this);
        imageView7.setOnClickListener(this);
        imageView8.setOnClickListener(this);
        imageView9.setOnClickListener(this);
        imageView10.setOnClickListener(this);
        imageView11.setOnClickListener(this);
        imageView12.setOnClickListener(this);
        mBanner = (BannerPager) findViewById(R.id.banner_pager);
        LayoutParams params = (LayoutParams) mBanner.getLayoutParams();
        params.height = (int) (DisplayUtil.getSreenWidth(this) * 250f / 640f);
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
        new refreshKeep().execute();
//        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
//        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
//        mInflater = LayoutInflater.from(this);
//        view1 = mInflater.inflate(activity_release_order, null);
//        view2 = mInflater.inflate(activity_receive_order, null);
//        //添加页卡视图
//        mViewList.add(view1);
//        mViewList.add(view2);
//
//        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
//        //给ViewPager设置适配器
//        mViewPager.setAdapter(mAdapter);
//        //将TabLayout和ViewPager关联起来
//        mTabLayout.setupWithViewPager(mViewPager);
//        //给Tabs设置适配器
//        mTabLayout.setTabsFromPagerAdapter(mAdapter);

//        mLinearLayout = (LinearLayout) mTabLayout.getChildAt(0);
//        // 在所有子控件的中间显示分割线（还可能只显示顶部、尾部和不显示分割线）
//        mLinearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        // 设置分割线的距离本身（LinearLayout）的内间距
//        mLinearLayout.setDividerPadding(50);
//        // 设置分割线的样式
//        mLinearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.divider_vertical));


        navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

//        swipeRefresh1 = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_refresh_release);
//        swipeRefresh1.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
//                android.R.color.holo_orange_light, android.R.color.holo_green_light);
//        swipeRefresh1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefresh1.setRefreshing(true);
//                new refreshKeep().execute();
//            }
//        });
//
//        swipeRefresh1.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefresh1.setRefreshing(true);
//                new refreshKeep().execute();
//            }
//        });
//
//        swipeRefresh2 = (SwipeRefreshLayout) view2.findViewById(R.id.swipe_refresh_receive);
//        swipeRefresh2.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
//                android.R.color.holo_orange_light, android.R.color.holo_green_light);
//        swipeRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefresh2.setRefreshing(true);
//                new refreshKeepTwo().execute();
//            }
//        });
//
//        swipeRefresh2.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefresh1.setRefreshing(true);
//                new refreshKeepTwo().execute();
//            }
//        });
    }

    @Override
    public void onBannerClick(int position) {
        switch (position+1){
            case 1:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://fir.im/luodian1km"));
                startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://fir.im/luodian1km"));
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                intent3.setData(Uri.parse("https://fir.im/luodian1km"));
                startActivity(intent3);
                break;
            case 4:
                Intent intent4 = new Intent(Intent.ACTION_VIEW);
                intent4.setData(Uri.parse("https://fir.im/luodian1km"));
                startActivity(intent4);
                break;
            case 5:
                Intent intent5 = new Intent(Intent.ACTION_VIEW);
                intent5.setData(Uri.parse("https://fir.im/luodian1km"));
                startActivity(intent5);
                break;
            default:
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.imageView6:{
                Intent intent1 = new Intent(MainActivity.this,TaskViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("lastActivity","main");
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            }
            case R.id.imageView7:{
                Intent intent1 = new Intent(MainActivity.this,SearchNeedsActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.imageView8:{
                Intent intent1 = new Intent(MainActivity.this,PublishNeedsActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.imageView9:{
                Intent intent1 = new Intent(MainActivity.this, RankActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.imageView10:{
                Intent intent1 = new Intent(MainActivity.this, RankActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.imageView11:{
                Intent intent1 = new Intent(MainActivity.this,MyPublishOrder.class);
                startActivity(intent1);
                break;
            }
            case R.id.imageView12:{
                Intent intent1 = new Intent(MainActivity.this,MyFinishedActivity.class);
                startActivity(intent1);
                break;
            }
        }
    }

    private class refreshKeep extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void...voids) {
            String result = null;
//            String path = "http://47.95.194.146:8080/sharedroot_server/Task";
            String path = getResources().getString(R.string.url)+"/Task";
            HttpURLConnection con = null;
            InputStream in = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(path);


                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(path);
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("action", "fetchpost"));
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
                    itemAcceptList.add(item);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void e) {
            super.onPostExecute(e);
//            if (swipeRefresh1 != null) swipeRefresh1.setRefreshing(false);
            if (itemPublishList.size()==0) Toast.makeText(getApplicationContext(),"无数据更新",Toast.LENGTH_SHORT).show();
//            RecyclerView releaseOrder = (RecyclerView) view1.findViewById(R.id.release_order);
//            GridLayoutManager layoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
//            releaseOrder.setLayoutManager(layoutManager1);
//            adapter1 = new ReleaseOrderItemAdapter(itemPublishList);
//            releaseOrder.setAdapter(adapter1);
            mRecyclerView = findViewById(R.id.mainRecycler1);
            MainPageReleaseAdapter mainPageReleaseAdapter = new MainPageReleaseAdapter(MainActivity.this,itemPublishList,1);
            mRecyclerView.setAdapter(mainPageReleaseAdapter);
             GridLayoutManager layoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
            mRecyclerView.setLayoutManager(layoutManager1);

            mRecyclerView = findViewById(R.id.mainRecycler2);
            MainPageReleaseAdapter mainPageReleaseAdapter2 = new MainPageReleaseAdapter(MainActivity.this,itemAcceptList,0);
            mRecyclerView.setAdapter(mainPageReleaseAdapter2);
            GridLayoutManager layoutManager2 = new GridLayoutManager(getApplicationContext(), 1);
            mRecyclerView.setLayoutManager(layoutManager2);
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.share_icon_with_background)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                .setTitle("退出1KM配送")
                .setMessage("您真的要退出吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Thread thread = new Thread() {
                            public void run(){
                                Socket anotherSocket = null;
                                try {
                                    anotherSocket = new Socket(getResources().getString(R.string.HOST), Integer.parseInt(getResources().getString(R.string.PORT)));
                                    PrintStream out1 = new PrintStream(anotherSocket.getOutputStream());
                                    out1.println("action=send;name="+ usrid + ";msg=byebye");
                                    out1.flush();
                                    out1.close();
                                    anotherSocket.close();

                                    LoginActivity.in.close();
                                    LoginActivity.out.close();
                                    LoginActivity.socket.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        for (Activity a:activityList){
                            if (a != null) a.finish();
                        }
                    }
                }).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDrawerLayout.closeDrawers();
    }

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }

    private class FetchUserInfo extends AsyncTask<String, Void, Boolean> {

        private String id = null;
        private String result = null;

        FetchUserInfo(String UserID) {
            id = UserID;
        }

        @Override
        protected Boolean doInBackground(String... ID) {
            String result = null;
            String path = getResources().getString(R.string.url)+"/Task?action=FetchUserID&ID=" + id;
            HttpURLConnection con=null;
            InputStream in=null;

            try
            {
                URL url=new URL(path);
                con= (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5*1000);
                con.setReadTimeout(5*1000);
                /*
                * http响应码：getResponseCode
                  200：成功 404：未找到 500：发生错误
              */
                if (con.getResponseCode()==200)
                {
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
                    if (result != "fail")
                    {
                        System.out.println(builder.toString());
                        JSONObject lan = new JSONObject(result);
                        usrphone = lan.getString("Phone");
                        usraccount = lan.getDouble("Account");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (in!=null){
                    try
                    {
                        in.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (con!=null){
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

                sp.edit().putString("now_account_money",String.valueOf(usraccount)).commit();
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
