package com.example.administrator.sharedroute.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.DialogMenuItem;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.widget.BlurBehind;
import com.example.administrator.sharedroute.widget.NormalListDialog;

import java.util.ArrayList;

public class BlurredActivity extends Activity {

	public static ArrayList<DialogMenuItem> testItems = new ArrayList<>();
	public static String fromActivity;
	private listItem listItem;
//	private BaseAnimatorSet base_in;
//	private BaseAnimatorSet base_out;
//	private Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blurred);
		Bundle bundle = getIntent().getExtras();
		listItem info = bundle.getParcelable("item");
		BlurBehind.getInstance()
				.withAlpha(90)
				.withFilterColor(Color.parseColor("#7fffffff"))
				.setBackground(this);
		fromActivity = "Main";
		testItems.add(new DialogMenuItem("快递名称:"+info.getInLocation(), R.mipmap.ic_express));
		testItems.add(new DialogMenuItem("取件时间:"+info.getInTimeStamp(), R.mipmap.ic_get_time));
		testItems.add(new DialogMenuItem("类型:"+info.getExpressType(), R.mipmap.ic_type));
		testItems.add(new DialogMenuItem("取货码:"+info.getPickupCode(), R.mipmap.ic_code));
		testItems.add(new DialogMenuItem("金额:"+info.getPrice()+"元", R.mipmap.ic_money));
		testItems.add(new DialogMenuItem("状态:暂无", R.mipmap.ic_status));
//		try {
//			((BlurredActivity) context).setBaseIn((BaseAnimatorSet) ZoomInEnter.class.newInstance());
//			((BlurredActivity) context).setBaseOut((BaseAnimatorSet) ZoomOutExit.class.newInstance());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		final NormalListDialog dialog = new NormalListDialog(BlurredActivity.this, testItems);
		dialog.title("物品详情")//
				.titleTextSize_SP(22)//
				.titleBgColor(Color.parseColor("#0097A8"))//
				.itemPressColor(Color.parseColor("#85D3EF"))//
				.itemTextColor(Color.parseColor("#303030"))//
				.itemTextSize(20)//
				.cornerRadius(0)//
				.widthScale(0.8f)//
				.show(R.style.myDialogAnim);
		testItems.clear();
	}

//
//	public void setBaseIn(BaseAnimatorSet base_in) {
//		this.base_in = base_in;
//	}
//
//	public void setBaseOut(BaseAnimatorSet base_out) {
//		this.base_out = base_out;
//	}
}