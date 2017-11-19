package com.example.administrator.sharedroute.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.base.BaseDialog;
import com.example.administrator.sharedroute.entity.DialogMenuItem;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.widget.BlurBehind;
import com.example.administrator.sharedroute.widget.NormalListDialog;

import java.util.ArrayList;

public class ConfirmBlurredActivity extends Activity {

    public static ArrayList<DialogMenuItem> testItems = new ArrayList<>();
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
        BaseDialog.fromActivity = "main";
        BlurBehind.getInstance()
                .withAlpha(90)
                .withFilterColor(Color.parseColor("#7fffffff"))
                .setBackground(this);
        testItems.add(new DialogMenuItem("确认送达"));
        testItems.add(new DialogMenuItem("取消"));
//		try {
//			((BlurredActivity) context).setBaseIn((BaseAnimatorSet) ZoomInEnter.class.newInstance());
//			((BlurredActivity) context).setBaseOut((BaseAnimatorSet) ZoomOutExit.class.newInstance());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        System.out.println(info.ID +"?");
        final NormalListDialog dialog = new NormalListDialog(ConfirmBlurredActivity.this, testItems,info.ID,info.FetcherID );
        dialog.title("确认收货")//
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