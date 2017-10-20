package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.TaskViewAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.util.ArrayList;
import java.util.List;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;

public class TaskViewActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,OnDismissCallback{
    private TaskViewAdapter trollyAdapter;
    private ListView listView;
    private List<listItem> listItemList;//这个表应该接收之前代选中加入购物车的项
    private Button getOrders;
    private CheckBox getAll;
    private Button deleteOrders;
    private boolean lastCheckBoxStatus = false;
    private  Toolbar mToolbar;
    private AnimationAdapter mAnimAdapter ;
    private static final int INITIAL_DELAY_MILLIS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        initView();
    }
    private void initView() {
        listView = (ListView) findViewById(R.id.shoppingtrolly_listview);
        mToolbar = (Toolbar) findViewById(R.id.toolbartaskview) ;
//        listItem item1 = new listItem("书籍","小件","今天 12：30","一区 顺风速运","今天 12：30","一区 正心楼 524",2.0,false);
//        listItem item8 = new listItem("书籍","小件","今天 12：30","一区 顺风速运","今天 12：30","一区 正心楼 524",2.0,false);
//        listItem item2 = new listItem("设备","小件","今天 18：30","一区 韵达快递","今天 12：30","一区 2公寓 5024",8.0,false);
//        listItem item6 = new listItem("设备","小件","今天 18：30","一区 韵达快递","今天 12：30","一区 2公寓 5024",8.0,false);
//        listItem item7 = new listItem("设备","小件","今天 18：30","一区 韵达快递","今天 12：30","一区 2公寓 5024",8.0,false);
//        listItem item3 = new listItem("食物","小件","今天 15：30","一区 中通快递","今天 12：30","一区 18公寓 9001",5.0,false);
//        listItem item4 = new listItem("食物","小件","今天 15：30","一区 中通快递","今天 12：30","一区 18公寓 9001",5.0,false);
//        listItem item5 = new listItem("食物","小件","今天 15：30","一区 中通快递","今天 12：30","一区 18公寓 9001",5.0,false);
//        listItem item9 = new listItem("食物","小件","今天 15：30","一区 中通快递","今天 12：30","一区 18公寓 9001",5.0,false);
//        listItem item10 = new listItem("食物","小件","今天 15：30","一区 中通快递","今天 12：30","一区 18公寓 9001",5.0,false);

        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        listItemList = bundle.getParcelableArrayList("listItemList");

        trollyAdapter  = new TaskViewAdapter(TaskViewActivity.this);

        for (listItem e : listItemList)
        {
            trollyAdapter.add(e);
        }
//        trollyAdapter.add(item1);
//        trollyAdapter.add(item2);
//        trollyAdapter.add(item3);
//        trollyAdapter.add(item4);
//        trollyAdapter.add(item5);
//        trollyAdapter.add(item6);
//        trollyAdapter.add(item7);
//        trollyAdapter.add(item8);
//        trollyAdapter.add(item9);
//        trollyAdapter.add(item10);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(trollyAdapter, this));
        swingBottomInAnimationAdapter.setAbsListView(listView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(swingBottomInAnimationAdapter);
        listView.setOnItemClickListener(this);

        deleteOrders=(Button)findViewById(R.id.deleteItems);
        deleteOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemList = trollyAdapter.getItems();
                ArrayList<Integer> deleteNumbers = new ArrayList<Integer>();
                int count=0;
                for (listItem e:listItemList) {
                    if (e.isCheckBoxElected()) deleteNumbers.add(count);
                    count++;
                }
                int length = deleteNumbers.size();
                for (int i=0;i<length;i++){
                    trollyAdapter.remove(trollyAdapter.getItem(deleteNumbers.get(length-i-1)));
                }
            }
        });


        getOrders = (Button)findViewById(R.id.jiedan);
        getOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemList = trollyAdapter.getItems();
                ArrayList<listItem> listElected = new ArrayList<listItem>();
                for (listItem e:listItemList) {
                    if (e.isCheckBoxElected()) listElected.add(e);
                }

                //将这个listElected传给下一个
                Intent intent =new Intent(TaskViewActivity.this,ConfirmTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listItemList",listElected);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        getAll = (CheckBox)findViewById(R.id.checkbox_item_all);
        getAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemList = trollyAdapter.getItems();
                if (lastCheckBoxStatus == false){
                    for (listItem e:listItemList) {
                        e.setCheckBoxElected(true);
                        lastCheckBoxStatus=true;
                    }
                    trollyAdapter.notifyDataSetChanged();
                }
                else{
                    for (listItem e:listItemList) {
                        e.setCheckBoxElected(false);
                        lastCheckBoxStatus=false;
                    }
                    trollyAdapter.notifyDataSetChanged();
                }
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("任务栏");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(TaskViewActivity.this,MainActivity.class));
                finish();
                return true;
            case R.id.back:
                startActivity(new Intent(TaskViewActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        listItemList = trollyAdapter.getItems();
        FlipShareView shareBottom = new FlipShareView.Builder(this, mToolbar)
                .addItem(new ShareItem("类型："+listItemList.get(position).getExpressType(), Color.WHITE, 0xffea650b))
                .addItem(new ShareItem("描述："+listItemList.get(position).getExpressSize(), Color.WHITE, 0xff4999F0))
                .addItem(new ShareItem("取件时间："+listItemList.get(position).getInTimeStamp(), Color.WHITE, 0xffD9392D))
                .addItem(new ShareItem("取件地点："+listItemList.get(position).getInLocation(), Color.WHITE, 0xff57708A))
                .addItem(new ShareItem("送件时间："+listItemList.get(position).getOutTimeStamp(), Color.WHITE, 0xffea0bb2))
                .addItem(new ShareItem("送件地点："+listItemList.get(position).getOutLocation(), Color.WHITE, 0xffea650b))
                .addItem(new ShareItem("价格："+listItemList.get(position).getPrice(), Color.WHITE,0xff063e04))
                .setItemDuration(200)
                .setBackgroundColor(0x60000000)
                .setAnimType(FlipShareView.TYPE_SLIDE)
                .create();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }


    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            Log.e("ppp",String.valueOf(trollyAdapter.getCount()));
            trollyAdapter.remove(position);
        }
    }
}
