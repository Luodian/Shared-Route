package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.ConfirmTaskAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.util.ArrayList;
import java.util.List;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;

public class ConfirmTaskActivity extends AppCompatActivity implements OnDismissCallback {
    private ListView listView;
    private ConfirmTaskAdapter adapter;
    private List<listItem> itemlists;//理论上这个列表应该由之前的页面传过来，这里先自己造几个数据。
    private Toolbar mToolbar;
    private AnimationAdapter mAnimAdapter;
    private Button mButton;
    private static final int INITIAL_DELAY_MILLIS = 100;
    private CardView mCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(null);
        setContentView(R.layout.activity_confirm_task);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("接单详情");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.back:
                startActivity(new Intent(ConfirmTaskActivity.this,MainActivity.class));
                return true;
        }
        return true;
    }



    private void initView(){
        listView=(ListView)findViewById(R.id.listViewFirmOrders);
        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        itemlists = bundle.getParcelableArrayList("listItemList");
        mCardView = (CardView) findViewById(R.id.cardView2);
        mButton =(Button) findViewById(R.id.button);
        adapter = new ConfirmTaskAdapter(ConfirmTaskActivity.this);
        for (int i = 0; i < itemlists.size(); i++) {
            adapter.add(itemlists.get(i));
        }
       /* mAnimAdapter = new SwingBottomInAnimationAdapter(new SwingRightInAnimationAdapter(adapter));
        mAnimAdapter.setAbsListView(listView);
        listView.setAdapter(mAnimAdapter);*/

        final SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(adapter, this));
        swingBottomInAnimationAdapter.setAbsListView(listView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(swingBottomInAnimationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<listItem> listItemList = adapter.getItems();
                FlipShareView shareBottom = new FlipShareView.Builder(ConfirmTaskActivity.this, mCardView)
                        .addItem(new ShareItem("类型："+listItemList.get(position).getExpressType(), Color.WHITE, 0xff43549C))
                        .addItem(new ShareItem("描述："+listItemList.get(position).getExpressSize(), Color.WHITE, 0xff4999F0))
                        .addItem(new ShareItem("取件时间："+listItemList.get(position).getInTimeStamp(), Color.WHITE, 0xffD9392D))
                        .addItem(new ShareItem("取件地点："+listItemList.get(position).getInLocation(), Color.WHITE, 0xff57708A))
                        .addItem(new ShareItem("送件时间："+listItemList.get(position).getOutTimeStamp(), Color.WHITE, 0xffea0bb2))
                        .addItem(new ShareItem("送件地点："+listItemList.get(position).getOutLocation(), Color.WHITE, 0xffea650b))
                        .addItem(new ShareItem("价格："+listItemList.get(position).getPrice(), Color.WHITE,0xff063e04))
                        .setItemDuration(250)
                        .setBackgroundColor(0x60000000)
                        .setAnimType(FlipShareView.TYPE_SLIDE)
                        .create();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemlists = adapter.getItems();
                ArrayList<listItem> listElected = new ArrayList<listItem>();
                for (listItem e:itemlists) {
                    listElected.add(e);
                }

                //将这个listElected传给下一个
                Intent intent =new Intent(ConfirmTaskActivity.this,ConfirmFinishedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listItemList",listElected);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            Log.e("ppp",String.valueOf(adapter.getCount()));
            adapter.remove(position);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }
}
