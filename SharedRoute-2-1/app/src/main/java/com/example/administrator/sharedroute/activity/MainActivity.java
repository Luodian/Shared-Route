package com.example.administrator.sharedroute.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.io.IOException;
import java.text.MessageFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();   //得到传过来的bundle
        usrid = bundle.getString("ID");

        orderDao = new OrderDao(this);
        /**
         * 测试用的
         **/
        if (!orderDao.isDataExist())
            orderDao.initTable();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

//        View nav_header_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.nav_header,null);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        View nav_header_view = navView.getHeaderView(0);

        UserID = (TextView) nav_header_view.findViewById(R.id.nav_header_id);
        UserName = (TextView) nav_header_view.findViewById(R.id.nav_header_name);
        UserAccount = (TextView) nav_header_view.findViewById(R.id.nav_header_account);

        UserID.setText(MessageFormat.format("学号：{0}", usrid));
        mFetchTask = new FetchUserInfo(usrid);

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
                        finish();
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
        swipeRefresh1.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
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
        swipeRefresh2.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh2.setRefreshing(true);
//                new refreshKeepTwo().execute();
            }
        });

        swipeRefresh2.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh1.setRefreshing(true);
//                new refreshKeepTwo().execute();
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

    private class refreshKeep extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (swipeRefresh1 != null) swipeRefresh1.setRefreshing(false);
            if (swipeRefresh2 != null) swipeRefresh2.setRefreshing(false);
        }
    }

    private class refreshTask extends AsyncTask<Void, Void, List<listItem>> {
        @Override
        protected List<listItem> doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemPublishList = new ArrayList<>();//这里就等着连数据吧
            itemAcceptList = orderDao.getAllDate();
            return itemPublishList;
        }

        @Override
        protected void onPostExecute(List<listItem> listItems) {
            super.onPostExecute(listItems);
            if (swipeRefresh1 != null) {
                swipeRefresh1.setRefreshing(false);
            }
            if (swipeRefresh2 != null) {
                assert swipeRefresh1 != null;
                swipeRefresh1.setRefreshing(false);
            }
            //if (status==1){
            RecyclerView releaseOrder = (RecyclerView) view1.findViewById(R.id.release_order);
            GridLayoutManager layoutManager1 = new GridLayoutManager(MainActivity.this, 1);
            releaseOrder.setLayoutManager(layoutManager1);
            adapter1 = new ReleaseOrderItemAdapter(itemPublishList);
            releaseOrder.setAdapter(adapter1);
            //	}
            //if (status==2){
            RecyclerView receiveOrder = (RecyclerView) view2.findViewById(R.id.receive_order);
            GridLayoutManager layoutManager2 = new GridLayoutManager(MainActivity.this, 1);
            receiveOrder.setLayoutManager(layoutManager2);
            adapter2 = new AcceptedOrderItemAdapter(itemAcceptList);
            receiveOrder.setAdapter(adapter2);
            //	}

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

//	public void fixListViewHeight(ListView listView) {
//		// 如果没有设置数据适配器，则ListView没有子项，返回。
//		ListAdapter listAdapter = listView.getAdapter();
//		int totalHeight = 0;
//		if (listAdapter == null) {
//			return;
//		}
//		for (int index = 0, len = listAdapter.getCount(); index < len; index++) {
//			View listViewItem = listAdapter.getView(index , null, listView);
//			// 计算子项View 的宽高
//			listViewItem.measure(0, 0);
//			// 计算所有子项的高度和
//			totalHeight += listViewItem.getMeasuredHeight();
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		// listView.getDividerHeight()获取子项间分隔符的高度
//		// params.height设置ListView完全显示需要的高度
//		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//		listView.setLayoutParams(params);
//	}

//	public boolean onCreateOptionsMenu(Menu menu){
//		getMenuInflater().inflate(R.menu.toolbar,menu);
//		return true;
//	}
//
//	public boolean onMenuOpened(int featureId, Menu menu) {
//		if (menu != null) {
//			if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
//				try {
//					Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
//					method.setAccessible(true);
//					method.invoke(menu, true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return super.onMenuOpened(featureId, menu);
//	}

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
                        finish();
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

    public class FetchUserInfo extends AsyncTask<Integer, Void, Boolean> {

        private String id = null;
        private String url = "http://47.95.194.146:8080/sharedroot_server/Login";
        private String result = null;

        FetchUserInfo(String UserID) {
            id = UserID;
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            // TODO: attempt authentication against a network service.

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                if (id!=""){
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                    parameters.add(new BasicNameValuePair("UserID", id));
                    parameters.add(new BasicNameValuePair("action", "login"));
                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                    post.setEntity(ent);
                }
                HttpResponse responsePOST = client.execute(post);

                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                if (result.equals("success"))
                {
                    client.getConnectionManager().shutdown();
                    return true;
                }
                else
                {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFetchTask = null;
            if (success) {
                UserName.setText("电话：" + usrphone);
                UserAccount.setText("余额：" + String.valueOf(usraccount));
            } else {
                Toast.makeText(MainActivity.this, "接受失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
        }
    }


}
