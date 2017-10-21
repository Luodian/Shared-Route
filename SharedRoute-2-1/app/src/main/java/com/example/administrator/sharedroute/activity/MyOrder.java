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
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.List;

public class MyOrder extends AppCompatActivity {

    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private CardView view1, view2, view3,viewCard;//页卡视图
    private List<CardView> mViewList = new ArrayList<>();//页卡视图集合
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private String select;
    private OrderDao orderDao;
    private List<listItem> myOrders;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        orderDao = new OrderDao(this);
        if (! orderDao.isDataExist()){
            orderDao.initTable();
        }
        myOrders = orderDao.getAcceptOrder();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("我的订单");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mInflater = LayoutInflater.from(this);



        for (listItem e:myOrders) {
            viewCard = (CardView)mInflater.inflate(R.layout.adapter,null);
            List<CardItem> cardItems = new ArrayList<>();
            ListViewAdapter adapter = new ListViewAdapter(MyOrder.this ,R.layout.carditem_layout,cardItems);
            CardItem item1 = new CardItem("快递类型:"+e.getExpressType(), R.mipmap.ic_express);
            cardItems.add(item1);
            CardItem item2 = new CardItem("发布时间:"+e.getPublishTime(), R.mipmap.ic_get_time);
            cardItems.add(item2);
            CardItem item3 = new CardItem("类型:"+e.getExpressSize(), R.mipmap.ic_type);
            cardItems.add(item3);
            CardItem item4 = new CardItem("取货码:"+e.getPickupCode(), R.mipmap.ic_code);
            cardItems.add(item4);
            CardItem item5 = new CardItem("金额:"+e.getPrice()+"元", R.mipmap.ic_money);
            cardItems.add(item5);
            CardItem item6 = new CardItem("状态:还没设置", R.mipmap.ic_status);
            cardItems.add(item6);
            ListView listViewCard = (ListView)viewCard.findViewById(R.id.list_view);
            listViewCard.setAdapter(adapter);
            mViewList.add(viewCard);
            viewCard.setOnClickListener(new ViewPager.OnClickListener(){
                @Override
                public void onClick(View v){
                    mViewPager.setCurrentItem(count++);
                }
            });
        }


        //给ViewPager设置适配器
        mCardAdapter = new CardPagerAdapter(mViewList);
        mViewPager.setAdapter(mCardAdapter);
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(1);
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

}
