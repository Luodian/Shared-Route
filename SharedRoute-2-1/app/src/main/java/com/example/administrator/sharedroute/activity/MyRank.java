package com.example.administrator.sharedroute.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.ReleaseRankItemAdapter;
import com.example.administrator.sharedroute.entity.Client;
import com.example.administrator.sharedroute.entity.ReleaseRankItem;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

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
import org.zackratos.ultimatebar.UltimateBar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyRank extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private String select;
    private List<Client> itemList = new ArrayList<>();
    private List<Client> itemListOthers = new ArrayList<>();
    private LayoutAnimationController lac;
    public static String PorA = "submit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rank);

        UltimateBar ultimateBar = new UltimateBar(this);
        ultimateBar.setImmersionBar();
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        if (PorA.equals("submit"))
        collapsingToolbar.setTitle("发单排行");
        else collapsingToolbar.setTitle("接单排行");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        new RandAsysc().execute();
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
    private class RandAsysc extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String result = null;
//            String path = "http://47.95.194.146:8080/sharedroot_server/Task";
            String path = getResources().getString(R.string.url)+"/Task";
            HttpURLConnection con = null;
            InputStream in = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(path);


                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("action", "rank"));
                parameters.add(new BasicNameValuePair("parameter",PorA));
//                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
//                String stuNum=sp.getString("now_stu_num",null);
//                parameters.add(new BasicNameValuePair("PublisherID", stuNum));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                JSONArray arr = new JSONArray(result.toString());
                itemList = new ArrayList<Client>();
                itemListOthers = new ArrayList<Client>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject lan = arr.getJSONObject(i);

                    String id =  lan.getString("UserID");
                    String name = lan.getString("Name");
                    int publishtasknum = lan.getInt("PublishTaskNum");
                    int fetchtasknum = lan.getInt("FetchTaskNum");
                    Client client1 = new Client(id,name,fetchtasknum,publishtasknum);
                    client1.rank = i + 1;
                    if (i<=2) itemList.add(client1);
                    else itemListOthers.add(client1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (itemList.size() == 0) Toast.makeText(MyRank.this, "发生未知错误",Toast.LENGTH_SHORT).show();
            RecyclerView rankList = findViewById(R.id.rank_list);
            ReleaseRankItemAdapter adapter = new ReleaseRankItemAdapter(itemList);
            GridLayoutManager layoutManager = new GridLayoutManager(MyRank.this, 1);
            rankList.setLayoutManager(layoutManager);
            rankList.setAdapter(adapter);

            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF,
                    0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setDuration(550);

            lac = new LayoutAnimationController(animation, 0.12f);
            lac.setInterpolator(new DecelerateInterpolator());
            rankList.setLayoutAnimation(lac);

            RecyclerView ranklist1 = findViewById(R.id.rank_list1);
            ReleaseRankItemAdapter adapter1 = new ReleaseRankItemAdapter(itemListOthers);
            GridLayoutManager layoutManager1 = new GridLayoutManager(MyRank.this,1);
            ranklist1.setLayoutManager(layoutManager1);
            ranklist1.setAdapter(adapter1);
            ranklist1.setLayoutAnimation(lac);
        }
    }
}
