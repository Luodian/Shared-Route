package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.TaskViewAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.utils.CheckFetcherUtil;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.util.ArrayList;
import java.util.List;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;

public class TaskViewActivity extends AppCompatActivity implements ListView.OnItemLongClickListener, AdapterView.OnItemClickListener,OnDismissCallback{
    private static final int INITIAL_DELAY_MILLIS = 100;
    private TaskViewAdapter trollyAdapter;
    private ListView listView;
    private List<listItem> listItemList;//这个表应该接收之前代选中加入购物车的项
    private Button getOrders;
    private CheckBox getAll;
    private Button deleteOrders;
    private boolean lastCheckBoxStatus = false;
    private  Toolbar mToolbar;
    private AnimationAdapter mAnimAdapter ;
    private OrderDao orderDao;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout mLinearLayout;
    private String lastActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        if (!MainActivity.activityList.contains(TaskViewActivity.this)) MainActivity.activityList.add(TaskViewActivity.this);
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
                    .LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Intent intent = getIntent();
        lastActivity =  intent.getExtras().getString("lastActivity");
        orderDao = new OrderDao(this);
        initView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(TaskViewActivity.this,"长按可看订单项详细信息",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        listItemList = trollyAdapter.getItems();
        FlipShareView shareBottom = new FlipShareView.Builder(this, mToolbar)
                .addItem(new ShareItem("发布者：："+listItemList.get(position).PublisherName, Color.WHITE, 0xff43549C))
                .addItem(new ShareItem("联系方式："+listItemList.get(position).PublisherName, Color.WHITE, 0xff43549C))
                .addItem(new ShareItem("物品类型："+listItemList.get(position).TaskKindID, Color.WHITE, 0xff43549C))
                .addItem(new ShareItem("物品描述："+listItemList.get(position).Remark, Color.WHITE, 0xff4999F0))
                .addItem(new ShareItem("取件时间："+listItemList.get(position).FetchTime, Color.WHITE, 0xffD9392D))
                .addItem(new ShareItem("取件地点："+listItemList.get(position).FetchLocation, Color.WHITE, 0xff57708A))
                .addItem(new ShareItem("送件时间："+listItemList.get(position).SendTime, Color.WHITE, 0xffea0bb2))
                .addItem(new ShareItem("送件地点："+listItemList.get(position).SendLocation, Color.WHITE, 0xffea650b))
                .addItem(new ShareItem("订单价格："+listItemList.get(position).Money, Color.WHITE,0xff063e04))
                .setItemDuration(200)
                .setBackgroundColor(0x60000000)
                .setAnimType(FlipShareView.TYPE_SLIDE)
                .create();
        return true;
    }

    private void initView() {
        listView = findViewById(R.id.shoppingtrolly_listview);
        mToolbar = findViewById(R.id.toolbartaskview);
        mLinearLayout = findViewById(R.id.bottom_toolbar);
        swipeRefresh = findViewById(R.id.swipe_refresh_taskview);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                new refreshKeep().execute();
            }
        });
//        listItemList = orderDao.getAcceptOrder();
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                mLinearLayout.setVisibility(View.GONE);
                new refreshTask().execute();
            }
        });


        deleteOrders = findViewById(R.id.deleteItems);
        deleteOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemList = trollyAdapter.getItems();
                ArrayList<Integer> deleteNumbers = new ArrayList<>();
                int count=0;
                for (listItem e:listItemList) {
                    if (e.isCheckBoxElected()) deleteNumbers.add(count);
                    count++;
                }
                int length = deleteNumbers.size();
                for (int i=0;i<length;i++){
                    OrderDao orderDao = new OrderDao(TaskViewActivity.this);
                    orderDao.deleteOrder(trollyAdapter.getItem(deleteNumbers.get(length-i-1)));
                    trollyAdapter.remove(trollyAdapter.getItem(deleteNumbers.get(length-i-1)));
                }
            }
        });


        getOrders = findViewById(R.id.jiedan);
        getOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckFetcherUtil checkFetcherUtil = new CheckFetcherUtil(TaskViewActivity.this);
                if (checkFetcherUtil.isTheFetcherIlligal()) {
                    listItemList = trollyAdapter.getItems();
                    ArrayList<listItem> listElected = new ArrayList<>();
                    for (listItem e : listItemList) {
                        if (e.isCheckBoxElected()) listElected.add(e);
                    }

                    //将这个listElected传给下一个
                    Intent intent = new Intent(TaskViewActivity.this, ConfirmTaskActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("listItemList", listElected);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(TaskViewActivity.this)
                            .setIcon(R.drawable.share_icon_with_background)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                            .setTitle("温馨提示")
                            .setMessage("运营期间，为了安全起见，暂不开放代取功能，目前由专人派送~")
                            .setPositiveButton("确认", null).show();
                }
            }
        });


        getAll = findViewById(R.id.checkbox_item_all);
        getAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemList = trollyAdapter.getItems();
                if (!lastCheckBoxStatus) {
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
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
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


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }


    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        List<listItem> arrayList = trollyAdapter.getItems();
        for (int position : reverseSortedPositions) {
            orderDao.deleteOrder(arrayList.get(position));
            trollyAdapter.remove(position);
        }
    }

    private class refreshTask extends AsyncTask<Void, Void, List<listItem>> {
        @Override
        protected List<listItem> doInBackground(Void... params) {
            try {
                Thread.sleep(400);
            } catch (Exception e) {
                e.printStackTrace();
            }
            listItemList = orderDao.getAllDate();
            return listItemList;
        }

        @Override
        protected void onPostExecute(List<listItem> listItemList) {
            super.onPostExecute(listItemList);
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            if (listItemList == null)
                Toast.makeText(getApplicationContext(), "当前无预订任务", Toast.LENGTH_SHORT).show();
            trollyAdapter = new TaskViewAdapter(TaskViewActivity.this);
            //没有新的数据，提示消息
            if (listItemList != null) {
                for (listItem e : listItemList) {
                    trollyAdapter.add(e);
                }
            }
            trollyAdapter.notifyDataSetChanged();
            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(trollyAdapter, TaskViewActivity.this));

            mAnimAdapter = swingBottomInAnimationAdapter;

            swingBottomInAnimationAdapter.setAbsListView(listView);
            assert swingBottomInAnimationAdapter.getViewAnimator() != null;
            swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
            listView.setAdapter(swingBottomInAnimationAdapter);
            listView.setOnItemLongClickListener(TaskViewActivity.this);
            listView.setOnItemClickListener(TaskViewActivity.this);
            if (mLinearLayout != null) mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private class refreshKeep extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
        }
    }
}


