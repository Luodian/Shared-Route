package com.example.administrator.sharedroute.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.ConfirmTaskAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

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
    private LinearLayout mInformation;
    private View mProgressView;
    private UserLoginTask mAuthTask;
    private TextView name;
    private TextView phone;
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
                finish();
                return true;
        }
        return true;
    }



    private void initView(){
        name = (TextView) findViewById(R.id.textName);
        phone = (TextView) findViewById(R.id.textPhoneNumber);
        listView=(ListView)findViewById(R.id.listViewFirmOrders);
        mInformation = (LinearLayout) findViewById(R.id.informationlayout);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        SharedPreferences sp = getSharedPreferences("now_account",Context.MODE_PRIVATE);
        String Name =sp.getString("now_name",null);
        name.setText(Name);

        String Phone = sp.getString("now_phone",null);
        phone.setText(Phone);

        mProgressView = findViewById(R.id.login_progress);
        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        if (bundle!=null)itemlists = bundle.getParcelableArrayList("listItemList");
        else itemlists = new ArrayList<listItem>();
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
                Toast.makeText(ConfirmTaskActivity.this,"长按可看订单项详细信息",Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                List<listItem> listItemList = adapter.getItems();
                FlipShareView shareBottom = new FlipShareView.Builder(ConfirmTaskActivity.this, mToolbar)
                        .addItem(new ShareItem("发布者：："+listItemList.get(position).PublisherName, Color.WHITE, 0xff43549C))
                        .addItem(new ShareItem("联系方式："+listItemList.get(position).PublisherName, Color.WHITE, 0xff43549C))
                        .addItem(new ShareItem("物品类型："+listItemList.get(position).TaskKindID, Color.WHITE, 0xff43549C))
                        .addItem(new ShareItem("物品描述："+listItemList.get(position).Remark, Color.WHITE, 0xff4999F0))
                        .addItem(new ShareItem("取件时间："+listItemList.get(position).FetchTime, Color.WHITE, 0xffD9392D))
                        .addItem(new ShareItem("取件地点："+listItemList.get(position).FetchLocation, Color.WHITE, 0xff57708A))
                        .addItem(new ShareItem("送件时间："+listItemList.get(position).SendTime, Color.WHITE, 0xffea0bb2))
                        .addItem(new ShareItem("送件地点："+listItemList.get(position).SendLocation, Color.WHITE, 0xffea650b))
                        .addItem(new ShareItem("订单价格："+listItemList.get(position).Money, Color.WHITE,0xff063e04))
                        .setItemDuration(250)
                        .setBackgroundColor(0x60000000)
                        .setAnimType(FlipShareView.TYPE_SLIDE)
                        .create();
                return true;
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserLoginTask(itemlists).execute();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)

   private class UserLoginTask extends AsyncTask<Void, Void, ArrayList<Integer>> {

//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost("http://suc.free.ngrok.cc/sharedroot_server/Login");
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(_queryKey, _queryValue));
//        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//        post.setEntity(ent);
//        private String url = "http://47.95.194.146:8080/sharedroot_server/Task";
        private String url = getResources().getString(R.string.url)+"/Task";
        private String result = null;

        private List<listItem> arraylist = new ArrayList<listItem>();

        UserLoginTask(List<listItem>  arraylist) {
            this.arraylist.addAll( arraylist);
        }

//        public UserLoginTask(String url,HttpListener listener){
//            this.url = url;
////            this.listener = listener;
//        }

        @Override
        protected ArrayList<Integer> doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                //參數
                int length = arraylist.size();
                if (length != 0){
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                    String json = new String();
                    json+="[";
                    for (int i = 0 ; i< length;i++) {
                        json += "{\"id\":"+arraylist.get(i).ID+"}";
                        if ( i != (length-1) )json +=",";
                        else json+="]";
                    }
                    System.out.println(json);
                    parameters.add(new BasicNameValuePair("name", json));
                    parameters.add(new BasicNameValuePair("action","update"));
                    SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
                    String stuNum=sp.getString("now_stu_num",null);
                    parameters.add(new BasicNameValuePair("FetcherID",stuNum));
                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                    post.setEntity(ent);
                }

                HttpResponse responsePOST = client.execute(post);

                HttpEntity resEntity = responsePOST.getEntity();


                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }

                JSONArray arr = new JSONArray(result.toString());
                ArrayList<Integer> array = new ArrayList<Integer>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject lan = arr.getJSONObject(i);
                    array.add(lan.getInt("id"));
                }
                return  array;
            } catch (Exception e) {
                // TODO: handle exception
                Toast.makeText(ConfirmTaskActivity.this,"未选订单或服务器异常，接单失败",Toast.LENGTH_SHORT).show();
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<Integer> integers) {
            if (integers == null) {
                Toast.makeText(getApplicationContext(),"接单失败",Toast.LENGTH_SHORT);
                return;
            }
            ArrayList<listItem> failList = new ArrayList<listItem>();
            ArrayList<listItem> successList = new ArrayList<listItem>();
            for (listItem e:itemlists){
                if (integers.contains(e.ID)) failList.add(e);
                else successList.add(e);
            }
            mAuthTask = null;
            //showProgress(false);
            Intent intent =new Intent(ConfirmTaskActivity.this,ConfirmFinishedActivity.class);
            itemlists = adapter.getItems();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("successList",successList);
            bundle.putParcelableArrayList("failList",failList);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
           // showProgress(false);
        }
    }

    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            adapter.remove(position);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }
}
