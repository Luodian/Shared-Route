package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.MyPagerAdapter;
import com.example.administrator.sharedroute.adapter.ReleaseOrderItemAdapter;
import com.example.administrator.sharedroute.entity.ReleaseOrderItem;
import com.example.administrator.sharedroute.widget.BannerPager;
import com.example.administrator.sharedroute.widget.BannerPager.BannerClickListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.sharedroute.R.layout.activity_receive_order;
import static com.example.administrator.sharedroute.R.layout.activity_release_order;

/**
 * Created by ouyangshen on 2016/10/21.
 */
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
	private List<ReleaseOrderItem> itemList = new ArrayList<>();
	private ReleaseOrderItemAdapter adapter;
	private int mMenuId;
	private BottomNavigationView navigation;
	private SwipeRefreshLayout swipeRefresh;
//    ReleaseOrderItem[] items = {new ReleaseOrderItem(R.drawable.yd_express,R.mipmap.ic_type_book,R.mipmap.ic_none_receive_status,"10月1日","书籍"),
//                                new ReleaseOrderItem(R.drawable.yt_express,R.mipmap.ic_type_book,R.mipmap.ic_none_receive_status,"10月2日","衣服"),
//                                new ReleaseOrderItem(R.drawable.zto_express,R.mipmap.ic_type_book,R.mipmap.ic_receive_status,"10月3日","电子"),
//                                new ReleaseOrderItem(R.drawable.best_express,R.mipmap.ic_type_book,R.mipmap.ic_receive_status,"10月4日","生活"),
//                                new ReleaseOrderItem(R.drawable.ems_express,R.mipmap.ic_type_book,R.mipmap.ic_none_receive_status,"10月5日","书籍")};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
						Intent intent2 = new Intent(MainActivity.this,MyOrder.class);
						intent2.putExtra("select_order",select);
						startActivity(intent2);
						return true;
					case R.id.nav_receive:
						select = "receiveOrder";
						Intent intent3 = new Intent(MainActivity.this,MyOrder.class);
						intent3.putExtra("select_order",select);
						startActivity(intent3);
						return true;
					case R.id.release_rank:
						select = "releaseRank";
						Intent intent4 = new Intent(MainActivity.this,MyRank.class);
						intent4.putExtra("select_order",select);
						startActivity(intent4);
						return true;
					case R.id.receive_rank:
						select = "receiveRank";
						Intent intent5 = new Intent(MainActivity.this,MyRank.class);
						intent5.putExtra("select_order",select);
						startActivity(intent5);
						return true;
					case R.id.nav_wallet:
						Intent intent6 = new Intent(MainActivity.this,BandCard.class);
						startActivity(intent6);
						return true;
					case R.id.nav_setting:
						return true;
					default:
				}
				return true;
			}
		});

		mBanner = (BannerPager) findViewById(R.id.banner_pager);
		LayoutParams params = (LayoutParams) mBanner.getLayoutParams();
		params.height = (int) (com.example.administrator.sharedroute.utils.DisplayUtil.getSreenWidth(this) * 250f/ 640f);
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

		initItems();
		RecyclerView releaseOrder = (RecyclerView)view1.findViewById(R.id.release_order);
		GridLayoutManager layoutManager = new GridLayoutManager(this,1);
		releaseOrder.setLayoutManager(layoutManager);
		adapter = new ReleaseOrderItemAdapter(itemList);
		releaseOrder.setAdapter(adapter);

        navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

		swipeRefresh = (SwipeRefreshLayout)view1.findViewById(R.id.swipe_refresh);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
		swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeRefresh.setRefreshing(false);
			}
		});

	}

	@Override
	public void onBannerClick(int position) {
		switch (position+1){
			case 1:
				Intent intent1 = new Intent(Intent.ACTION_VIEW);
				intent1.setData(Uri.parse("http://www.baidu.com"));
				startActivity(intent1);
				break;
			case 2:
				Intent intent2 = new Intent(Intent.ACTION_VIEW);
				intent2.setData(Uri.parse("http://www.github.com"));
				startActivity(intent2);
				break;
			case 3:
				Intent intent3 = new Intent(Intent.ACTION_VIEW);
				intent3.setData(Uri.parse("http://www.qq.com"));
				startActivity(intent3);
				break;
			case 4:
				Intent intent4 = new Intent(Intent.ACTION_VIEW);
				intent4.setData(Uri.parse("http://www.4399.com"));
				startActivity(intent4);
				break;
			case 5:
				Intent intent5 = new Intent(Intent.ACTION_VIEW);
				intent5.setData(Uri.parse("http://www.hao123.com"));
				startActivity(intent5);
				break;
			default:
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

	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar,menu);
		return true;
	}

	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menu != null) {
			if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
				try {
					Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					method.setAccessible(true);
					method.invoke(menu, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				break;
		}
		return true;
	}

	@Override
	public void onPause(){
		super.onPause();
		mDrawerLayout.closeDrawers();
	}

//	public void BlurActivityDialog(final String title, final String select){
//        Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
//        vibrator.vibrate(new long[]{0,1000}, -1);
//		BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
//			@Override
//			public void onBlurComplete() {
//				Intent intent = new Intent(MainActivity.this, BlurredActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.putExtra("title_name",title);
//                intent.putExtra("select",select);
//				startActivity(intent);
//			}
//		});
//	}

	private void initItems(){

        for(int i = 0;i<2;i++){
            ReleaseOrderItem item1 = new ReleaseOrderItem();
            item1.setExpressimageId(R.drawable.yd_express);
            item1.setDate("10月1日");
            item1.setTpyeimageId(R.mipmap.ic_type_book);
            item1.setType("书籍");
            item1.setStatusimageId(R.mipmap.ic_none_receive_status);
            itemList.add(item1);

            ReleaseOrderItem item2 = new ReleaseOrderItem();
            item2.setExpressimageId(R.drawable.yt_express);
            item2.setDate("10月2日");
            item2.setTpyeimageId(R.mipmap.ic_type_clothes);
            item2.setType("衣服");
            item2.setStatusimageId(R.mipmap.ic_none_receive_status);
            itemList.add(item2);

            ReleaseOrderItem item3 = new ReleaseOrderItem();
            item3.setExpressimageId(R.drawable.zto_express);
            item3.setDate("10月3日");
            item3.setTpyeimageId(R.mipmap.ic_type_electri);
            item3.setType("电子");
            item3.setStatusimageId(R.mipmap.ic_receive_status);
            itemList.add(item3);

            ReleaseOrderItem item4 = new ReleaseOrderItem();
            item4.setExpressimageId(R.drawable.best_express);
            item4.setDate("10月4日");
            item4.setTpyeimageId(R.mipmap.ic_type_life);
            item4.setType("生活");
            item4.setStatusimageId(R.mipmap.ic_receive_status);
            itemList.add(item4);

            ReleaseOrderItem item5 = new ReleaseOrderItem();
            item5.setExpressimageId(R.drawable.ems_express);
            item5.setDate("10月5日");
            item5.setTpyeimageId(R.mipmap.ic_type_else);
            item5.setType("其他");
            item5.setStatusimageId(R.mipmap.ic_none_receive_status);
            itemList.add(item5);
        }
	}

	public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }

//	private void sendRequestWithOkHttp(){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try{
//					OkHttpClient client = new OkHttpClient();
//					Request request = new Request.Builder()
//									.url("http://192.168.43.49:8080/packageMessage/5/6/7/8/9")
//									.build();
//					Response response = client.newCall(request).execute();
//					String responseData = "["+response.body().string()+"]";
//					parseJSONwithJSONObject(responseData);
//					httpPostJSON();
//				}catch (Exception e){
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
//
//	private void parseJSONwithJSONObject(String jsonData) {
//		try {
//			JSONArray jsonArray = new JSONArray(jsonData);
//			for(int i = 0;i<jsonArray.length();i++){
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				Long id = jsonObject.getLong("id");
//				String expressName = jsonObject.getString("expressName");
//				String releaseDate = jsonObject.getString("releaseDate");
//				String releaseType = jsonObject.getString("releaseType");
//				String packageStatus = jsonObject.getString("packageStatus");
//				Log.d("MainActivity",Long.toString(id));
//				Log.d("MainActivity",expressName);
//				Log.d("MainActivity",releaseDate);
//				Log.d("MainActivity",releaseType);
//				Log.d("MainActivity",packageStatus);
//
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}

//	public void httpPost(){
//		//换成自己的ip就行
//		String url = "http://192.168.43.49:8080/packageMessage";
//		OkHttpClient client = new OkHttpClient();//创建okhttp实例
//		FormBody body=new FormBody.Builder()
//				.add("id","123")
//				.add("expressName","23")
//				.add("releaseDate","46")
//				.add("releaseType","355")
//				.add("packageStatus","566")
//				.build();
//		Request request=new Request.Builder().post(body).url(url).build();
//		Call call = client.newCall(request);
//		call.enqueue(new Callback() {
//			//请求失败时调用
//			@Override
//			public void onFailure(Call call, IOException e) {
//				Log.i("MainActivity", "onFailure: " + e);
//			}
//			//请求成功时调用
//			@Override
//			public void onResponse(Call call, Response response) throws IOException {
//				if (response.isSuccessful()) {
//					Log.i("MainActivity", "onResponse: " + response.body().string());
//				}
//			}
//		});
//	}

	public void httpPostJSON(){
		String json="{\n" +
				"  \"id\" : 3,\n" +
				"  \"expressName\" : \"45\",\n" +
				"  \"releaseDate\" : \"366\",\n" +
				"  \"releaseType\" : \"351\",\n" +
				"  \"packageStatus\" : \"1234\"\n" +
				"}";
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		//换成自己的ip就行
		String url = "http://192.168.43.49:8080/packageMessage";
		OkHttpClient client = new OkHttpClient();//创建okhttp实例
		RequestBody body = RequestBody.create(JSON,json);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			//请求失败时调用
			@Override
			public void onFailure(Call call, IOException e) {
				Log.i("MainActivity", "onFailure: " + e);
			}
			//请求成功时调用
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					Log.i("MainActivity", "onResponse: " + response.body().string());
				}
			}
		});

	}
}
