package com.example.administrator.sharedroute.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.CardPagerAdapter;
import com.example.administrator.sharedroute.adapter.ListViewAdapter;
import com.example.administrator.sharedroute.entity.CardItem;
import com.example.administrator.sharedroute.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.List;

public class MyOrder extends AppCompatActivity {

    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private CardView view1, view2, view3;//页卡视图
    private List<CardView> mViewList = new ArrayList<>();//页卡视图集合
    private List<CardItem> cardItems = new ArrayList<>();
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private String select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("我的订单");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mInflater = LayoutInflater.from(this);

        ListViewAdapter adapter = new ListViewAdapter(MyOrder.this,R.layout.carditem_layout,cardItems);
        initData();

        view1 = (CardView) mInflater.inflate(R.layout.adapter, null);
        ListView listView1 = (ListView)view1.findViewById(R.id.list_view);
        listView1.setAdapter(adapter);

        view2 =(CardView) mInflater.inflate(R.layout.adapter, null);
        ListView listView2 = (ListView)view2.findViewById(R.id.list_view);
        listView2.setAdapter(adapter);

        view3 = (CardView)mInflater.inflate(R.layout.adapter, null);
        ListView listView3 = (ListView)view3.findViewById(R.id.list_view);
        listView3.setAdapter(adapter);

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);

        //给ViewPager设置适配器
        mCardAdapter = new CardPagerAdapter(mViewList);
        mViewPager.setAdapter(mCardAdapter);
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        view1.setOnClickListener(new ViewPager.OnClickListener(){
            @Override
            public void onClick(View v){
                mViewPager.setCurrentItem(0);
            }
        });
        view2.setOnClickListener(new ViewPager.OnClickListener(){
            @Override
            public void onClick(View v){
                mViewPager.setCurrentItem(1);
            }
        });
        view3.setOnClickListener(new ViewPager.OnClickListener(){
            @Override
            public void onClick(View v){
                mViewPager.setCurrentItem(2);
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                select = "";
                finish();
                return true;
        }
        return true;
    }

    public void initData(){
        CardItem item1 = new CardItem("快递名称:", R.mipmap.ic_express);
        cardItems.add(item1);
        CardItem item2 = new CardItem("发布时间:", R.mipmap.ic_get_time);
        cardItems.add(item2);
        CardItem item3 = new CardItem("类型:", R.mipmap.ic_type);
        cardItems.add(item3);
        CardItem item4 = new CardItem("取货码:", R.mipmap.ic_code);
        cardItems.add(item4);
        CardItem item5 = new CardItem("金额:", R.mipmap.ic_money);
        cardItems.add(item5);
        CardItem item6 = new CardItem("状态:", R.mipmap.ic_status);
        cardItems.add(item6);
    }

}
