package com.example.administrator.bannertest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ouyangshen on 2016/10/21.
 */
public class BannerPagerActivity extends AppCompatActivity implements OnBannerListener {
	Banner mBanner;
	private DrawerLayout mDrawerLayout;
	private String[] data = {"item1","item2","item3","item4","item5"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banner_pager);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_user);
		}
		navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
			@Override
			public boolean onNavigationItemSelected(MenuItem item){
				mDrawerLayout.closeDrawers();
				return true;
			}
		});

		mBanner = (Banner)findViewById(R.id.mbanner);

		LayoutParams params = (LayoutParams) mBanner.getLayoutParams();
		params.height = (int) (DisplayUtil.getSreenWidth(this) * 250f/ 640f);
		mBanner.setLayoutParams(params);
		mBanner.setImages(new ArrayList<>(Arrays.asList(R.drawable.banner_1,R.drawable.banner_2,R.drawable.banner_3,R.drawable.banner_4,R.drawable.banner_5)))
				.setImageLoader(new GlideImageLoader())
				.setBannerAnimation(Transformer.CubeOut)			//动画效果
				.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)		//指示器位置，是否有标题
				.setOnBannerListener((OnBannerListener) this)
				.start();

		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(BannerPagerActivity.this,android.R.layout.simple_list_item_1,data);
		ListView qujian = (ListView)findViewById(R.id.qujian);
		ListView daiqu  = (ListView)findViewById(R.id.daiqu);
		qujian.setAdapter(adapter);
		daiqu.setAdapter(adapter);
		fixListViewHeight(qujian);
		fixListViewHeight(daiqu);

	}

	@Override
	protected void onStart() {
		super.onStart();
		//开始轮播
		mBanner.startAutoPlay();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//结束轮播
		mBanner.stopAutoPlay();
	}

    public void OnBannerClick(int position) {
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

	public void fixListViewHeight(ListView listView) {
		// 如果没有设置数据适配器，则ListView没有子项，返回。
		ListAdapter listAdapter = listView.getAdapter();
		int totalHeight = 0;
		if (listAdapter == null) {
			return;
		}
		for (int index = 0, len = listAdapter.getCount(); index < len; index++) {
			View listViewItem = listAdapter.getView(index , null, listView);
			// 计算子项View 的宽高
			listViewItem.measure(0, 0);
			// 计算所有子项的高度和
			totalHeight += listViewItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		// listView.getDividerHeight()获取子项间分隔符的高度
		// params.height设置ListView完全显示需要的高度
		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.toolbar,menu);
		return true;
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
