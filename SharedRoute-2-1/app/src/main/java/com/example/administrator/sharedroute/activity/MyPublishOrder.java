package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.CardPagerAdapter;
import com.example.administrator.sharedroute.adapter.ListViewAdapter;
import com.example.administrator.sharedroute.entity.CardItem;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.utils.ShadowTransformer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyPublishOrder extends AppCompatActivity {
/**
 *讲道理这里应该接受的是远程的数据
 */
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private CardView viewCard;//页卡视图
    private List<CardView> mViewList = new ArrayList<>();//页卡视图集合
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private List<listItem> myOrders;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        new refreshKeep().execute();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        JumpToActivity(MainActivity.class);
        finish();
    }

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }

    private class refreshKeep extends AsyncTask<Void, Void,ArrayList<listItem>> {

        @Override
        protected ArrayList<listItem> doInBackground(Void... pa) {
            String result = null;
            String path = getResources().getString(R.string.url)+"/Task";
            HttpURLConnection con = null;
            InputStream in = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(path);
                List<NameValuePair> parameters = new ArrayList<>();
//                    String json = new String();
//                    json += "[";
//                    for (int i = 0; i < length; i++) {
//                        json += "{\"id\":" + itemPublishList.get(i).ID + "}";
//                        if (i != (length - 1)) json += ",";
//                        else json += "]";
//                    }
//                    System.out.println(json);
//                    parameters.add(new BasicNameValuePair("name", json));
                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
                String stuNum=sp.getString("now_stu_num",null);
                parameters.add(new BasicNameValuePair("action", "publishpost"));
                parameters.add(new BasicNameValuePair("PublisherID", stuNum));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                JSONArray arr = new JSONArray(result.toString());
                if (myOrders == null) myOrders = new ArrayList<>();
                else  myOrders.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject lan = arr.getJSONObject(i);
                    listItem item = new listItem();
                    item.ID = lan.getInt("ID");
                    item.Money = lan.getDouble("Money");
                    item.PickID = lan.getString("PickID");
                    item.TaskKindID = lan.getString("TaskkindID");
                    item.PublisherName = lan.getString("PublisherName");
                    item.PublisherPhone = lan.getString("PublisherPhone");
                    item.FetchTime = lan.getString("FetchTime");
                    item.FetchLocation = lan.getString("FetchLocation");
                    item.FetcherPhone = lan.getString("FetcherPhone");
                    item.FetcherName = lan.getString("FetcherName");
                    item.FetcherID = lan.getString("FetcherID");
                    item.SendTime = lan.getString("SendTime");
                    item.SendLocation = lan.getString("SendLocation");
                    item.PublisherID = lan.getString("PublisherID");
                    item.PromiseMoney = lan.getDouble("PromiseMoney");
                    item.status = lan.getInt("Status");
                    myOrders.add(item);
                }
                return (ArrayList<listItem>) myOrders;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<listItem> data) {
            super.onPostExecute(data);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("我发布的订单");
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            mViewPager = findViewById(R.id.viewPager);
            mInflater = LayoutInflater.from(MyPublishOrder.this);


            if (myOrders!=null){
                for (listItem e:myOrders) {
                    viewCard = (CardView)mInflater.inflate(R.layout.adapter,null);
                    List<CardItem> cardItems = new ArrayList<>();
                    ListViewAdapter adapter = new ListViewAdapter(MyPublishOrder.this ,R.layout.carditem_layout,cardItems);
                    CardItem item1 = new CardItem("物品类型："+e.TaskKindID, R.mipmap.ic_express);
                    cardItems.add(item1);
                    CardItem item2 = new CardItem("发布时间："+e.FetchTime, R.mipmap.ic_get_time);
                    cardItems.add(item2);
                    CardItem item3 = new CardItem("物品描述："+e.Remark, R.mipmap.ic_type);
                    cardItems.add(item3);
                    CardItem item4 = new CardItem("取货码："+e.PickID, R.mipmap.ic_code);
                    cardItems.add(item4);
                    CardItem item5 = new CardItem("金额："+e.Money+"元", R.mipmap.ic_money);
                    cardItems.add(item5);
                    if (e.status == 1){
                        CardItem item6 = new CardItem("状态："+"未被接受", R.mipmap.ic_status);
                        cardItems.add(item6);
                    }
                    else if (e.status == 2){
                        CardItem item6 = new CardItem("状态："+"已被接受", R.mipmap.ic_status);
                        cardItems.add(item6);
                    }
                    else if (e.status == 3){
                        CardItem item6 = new CardItem("状态："+"已被完成", R.mipmap.ic_status);
                        cardItems.add(item6);
                    }
                    else {
                        CardItem item6 = new CardItem("状态："+"未知", R.mipmap.ic_status);
                        cardItems.add(item6);
                    }
                    ListView listViewCard = viewCard.findViewById(R.id.list_view);
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
            else {
                Toast.makeText(getApplicationContext(),"当前无任务",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
