package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.Client;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.ArrayAdapter;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Client> arrayList;
    public static String PorA = "submit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);








        setContentView( R.layout.activity_rank2 );
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
        initView();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarss);
        setSupportActionBar(toolbar);
        if (PorA.equals("submit"))getSupportActionBar().setTitle("发单排行");
        else if (PorA.equals("fetch"))getSupportActionBar().setTitle("接单排行");
    }
    private void initView() {
        new RandAsysc().execute();
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
                arrayList = new ArrayList<Client>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject lan = arr.getJSONObject(i);

                    String id =  lan.getString("UserID");
                    String name = lan.getString("Name");
                    int publishtasknum = lan.getInt("PublishTaskNum");
                    int fetchtasknum = lan.getInt("FetchTaskNum");
                    Client client1 = new Client(id,name,fetchtasknum,publishtasknum);
                    arrayList.add(client1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListView = findViewById(R.id.rand_list);
            RankAdapter rankAdapter = new RankAdapter(RankActivity.this);
            for (Client c:arrayList) {
                rankAdapter.add(c);
            }
            if (arrayList.size() == 0) Toast.makeText(RankActivity.this, "发生未知错误",Toast.LENGTH_SHORT).show();
            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(rankAdapter);
            swingBottomInAnimationAdapter.setAbsListView(mListView);
            mListView.setAdapter(swingBottomInAnimationAdapter);
        }
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        RankAdapter listAdapter = (RankAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
class RankAdapter extends ArrayAdapter<Client> {
    private Context mContext;
    public RankAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.compus_rank,null,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.text_ID.setText( getItem(i).studentID);
        viewHolder.text_name.setText(getItem(i).name);
        viewHolder.pickOrdernum.setText(String.valueOf(getItem(i).pickOrderNum)+"次");
        viewHolder.completeOrdernum.setText(String.valueOf(getItem(i).completeOrderNum)+"次");
        int judge = i%3;
        switch (judge){
            case 0:{
                viewHolder.rankBG.setBackgroundResource(R.drawable.shadow1);
                break;
            }
            case 1:{
                viewHolder.rankBG.setBackgroundResource(R.drawable.shadow2);
                break;
            }
            case 2:{
                viewHolder.rankBG.setBackgroundResource(R.drawable.shadow3);
                break;
            }
            default:break;
        }
        return view;
    }

    static class ViewHolder{
        ImageView imageView;
        TextView text_name;
        TextView text_ID;
        TextView pickOrdernum;
        TextView completeOrdernum;
        LinearLayout rankBG;
        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.rank_icon_image);
            text_name = (TextView) view.findViewById(R.id.rank_name);
            text_ID = (TextView) view.findViewById(R.id.rank_ID);
            pickOrdernum = (TextView) view.findViewById(R.id.rank_picknum);
            completeOrdernum = (TextView) view.findViewById(R.id.rank_completenum);
            rankBG = (LinearLayout)view.findViewById(R.id.rank_bg);
        }
    }
}