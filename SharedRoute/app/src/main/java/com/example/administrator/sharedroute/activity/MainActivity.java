package com.example.administrator.sharedroute.activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.MyPagerAdapter;
import com.example.administrator.sharedroute.entity.DialogMenuItem;
import com.example.administrator.sharedroute.widget.BannerPager;
import com.example.administrator.sharedroute.widget.BannerPager.BannerClickListener;
import com.example.administrator.sharedroute.widget.NormalListDialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


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
	private String [] receive_order_data = {"item1","item2","item3","item4","item5"
			,"item6","item7","item8","item9","item10"};
	private String [] release_order_data = {"item1","item2","item3","item4","item5"
			,"item6","item7","item8","item9","item10"};
	private ArrayList<DialogMenuItem> testItems = new ArrayList<>();
	private String[] stringItems = {"收藏", "下载", "分享", "删除", "歌手", "专辑"};
	public static String select = "releaseOrder";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		testItems.add(new DialogMenuItem("收藏", R.mipmap.ic_delete));
		testItems.add(new DialogMenuItem("下载", R.mipmap.ic_delete));
		testItems.add(new DialogMenuItem("分享", R.mipmap.ic_delete));
		testItems.add(new DialogMenuItem("删除", R.mipmap.ic_delete));
		testItems.add(new DialogMenuItem("歌手", R.mipmap.ic_delete));
		testItems.add(new DialogMenuItem("专辑", R.mipmap.ic_delete));

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

		ArrayList<Integer> bannerArray = new ArrayList<Integer>();
		bannerArray.add(Integer.valueOf(R.drawable.banner_1));
		bannerArray.add(Integer.valueOf(R.drawable.banner_2));
		bannerArray.add(Integer.valueOf(R.drawable.banner_3));
		bannerArray.add(Integer.valueOf(R.drawable.banner_4));
		bannerArray.add(Integer.valueOf(R.drawable.banner_5));
		mBanner.setImage(bannerArray);
		mBanner.setOnBannerListener(this);
        mBanner.start();

		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,data);
//		ListView qujian = (ListView)findViewById(R.id.qujian);
//		ListView daiqu  = (ListView)findViewById(R.id.daiqu);
//		qujian.setAdapter(adapter);
//		daiqu.setAdapter(adapter);
//		fixListViewHeight(qujian);
//		fixListViewHeight(daiqu);

		mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
		mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
		mInflater = LayoutInflater.from(this);

		view1 = mInflater.inflate(R.layout.activity_release_order, null);
		view2 = mInflater.inflate(R.layout.activity_receive_order, null);
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

		ArrayAdapter<String> ReleaseAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,release_order_data);
		ListView ReleaseListView = (ListView)view1.findViewById(R.id.release_order);
		ReleaseListView.setAdapter(ReleaseAdapter);
		ArrayAdapter<String> ReceiveAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,receive_order_data);
		ListView ReceiveListView = (ListView)view2.findViewById(R.id.receive_order);
		ReceiveListView.setAdapter(ReceiveAdapter);

		ReleaseListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
				vibrator.vibrate(new long[]{0,1000}, -1);
				switch (position){
					case 0:
//						Intent intent = new Intent(MainActivity.this,ReleaseOrderDetail.class);
//						startActivity(intent);
//						Dialog dialog = new Dialog(MainActivity.this,R.style.dialog);
//						View view3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_release_order_detail,null);
//						dialog.setContentView(view3);
//						//自定义宽高（高度一般不用调整，在xml调整好就可以了，这里我只调整了宽度）
//						WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//						params.width = 500;
//						params.height = 500;
//						dialog.getWindow().setAttributes(params);
//						dialog.show();
						final NormalListDialog dialog = new NormalListDialog(MainActivity.this, testItems);
						dialog.title("请选择")//
								.titleTextSize_SP(18)//
								.titleBgColor(Color.parseColor("#409ED7"))//
								.itemPressColor(Color.parseColor("#85D3EF"))//
								.itemTextColor(Color.parseColor("#303030"))//
								.itemTextSize(14)//
								.cornerRadius(0)//
								.widthScale(0.8f)//
								.show(R.style.myDialogAnim);

				}
				Toast.makeText(MainActivity.this,Integer.toString(position),Toast.LENGTH_SHORT).show();
				return true;
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
			switch (item.getItemId()) {
				case R.id.navigation_home:
					return true;
				case R.id.navigation_dashboard:
					return true;
				case R.id.navigation_notifications:
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
}
