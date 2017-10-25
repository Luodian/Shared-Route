package com.example.administrator.sharedroute.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BannerPager extends RelativeLayout implements View.OnClickListener {
	private static final String TAG = "BannerPager";
	private Context mContext;
	private ViewPager mPager;
	private List<ImageView> mViewList = new ArrayList<ImageView>();
	private RadioGroup mGroup;
	private int mCount;
	private LayoutInflater mInflater;
	private int dip_15;
	private static int mInterval = 2000;

	public BannerPager(Context context) {
		this(context, null);
	}

	public BannerPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public void start() {
		mHandler.postDelayed(mScroll, mInterval);
	}

	public void stop() {
		mHandler.removeCallbacks(mScroll);
	}

	public void setInterval(int interval) {
		mInterval = interval;
	}

	public void setImage(ArrayList<Integer> imageList) {
		for (int i = 0; i < imageList.size(); i++) {
			Integer imageID = ((Integer) imageList.get(i)).intValue();
			ImageView iv = new ImageView(mContext);
			iv.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setImageResource(imageID);
			iv.setOnClickListener(this);
			mViewList.add(iv);
		}
		mPager.setAdapter(new ImageAdapater());
		mPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setButton(position);
			}
		});

		mCount = imageList.size();
		for (int i = 0; i < mCount; i++) {
			RadioButton radio = new RadioButton(mContext);
			radio.setLayoutParams(new RadioGroup.LayoutParams(dip_15, dip_15));
			radio.setGravity(Gravity.CENTER);
			radio.setButtonDrawable(R.drawable.indicator_selector);
			mGroup.addView(radio);
		}
		mPager.setCurrentItem(0);
		setButton(0);
	}

	private void setButton(int position) {
		((RadioButton) mGroup.getChildAt(position)).setChecked(true);
	}

	private void init() {
		mInflater = ((Activity) mContext).getLayoutInflater();
		View view = mInflater.inflate(R.layout.banner_pager, null);
		mPager = (ViewPager) view.findViewById(R.id.vp_banner);
		mGroup = (RadioGroup) view.findViewById(R.id.rg_indicator);
		addView(view);
		dip_15 = Utils.dip2px(mContext, 15);
	}

	private Handler mHandler = new Handler();
	private Runnable mScroll = new Runnable() {
		@Override
		public void run() {
			scrollToNext();
			mHandler.postDelayed(this, mInterval);
		}
	};

	public void scrollToNext() {
		int index = mPager.getCurrentItem() + 1;
		if (mViewList.size() <= index) {
			index = 0;
		}
		mPager.setCurrentItem(index);
	}
	
	private class ImageAdapater extends PagerAdapter {
		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViewList.get(position));
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mViewList.get(position));
			return mViewList.get(position);
		}
	}

	@Override
	public void onClick(View v) {
		int position = mPager.getCurrentItem();
		mListener.onBannerClick(position);
	}

	public void setOnBannerListener(BannerClickListener listener) {
		mListener = listener;
	}

	private BannerClickListener mListener;
	public interface BannerClickListener {
		public void onBannerClick(int position);
	}

}