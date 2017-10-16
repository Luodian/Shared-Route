package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.HistoryInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HistoryInfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<HistoryInfo> listData= new ArrayList<>();
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_info);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        SharedPreferences sp = getSharedPreferences("finals", MODE_PRIVATE);

        //从主页面进入，就只需要读数据
        List<HistoryInfo> datas=new ArrayList<>();

        String result = sp.getString("history_info", "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names!= null) {
                    Map<String,String> itemMap = new HashMap<>();
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name,value);
                    }
                    String name = itemMap.get("name");
                    String phone = itemMap.get("phone");
                    String place = itemMap.get("delieverPlace");
                    HistoryInfo historyInfo = new HistoryInfo(name,phone,place);
                    datas.add(historyInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listData=datas;

        int index=-1;// 表示新增加的
        if (bundle!=null ) {
            String name=bundle.getString("name");
            String phone=bundle.getString("phone");
            String place=bundle.getString("place");
            index=Integer.parseInt(bundle.getString("index"));
            HistoryInfo historyInfo = new HistoryInfo(name,phone,place);
            if (index==-1){
                listData.add(historyInfo);
            } else {
                listData.set(index,historyInfo);
            }
            JSONArray mJsonArray = new JSONArray();
            for (int i = 0; i < listData.size(); i++) {
                String userName = listData.get(i).getName();
                String userPhone = listData.get(i).getPhone();
                String userPlace = listData.get(i).getDeliverPlace();
                Map<String, String> itemMap = new HashMap<>();

                itemMap.put("name",userName);
                itemMap.put("phone",userPhone);
                itemMap.put("delieverPlace",userPlace);
                Iterator<Map.Entry<String, String>> iterator = itemMap.entrySet().iterator();

                JSONObject object = new JSONObject();

                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    try {
                        object.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {

                    }
                }
                mJsonArray.put(object);
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("history_info", mJsonArray.toString());
            editor.commit();
        }
        ImageView addInfoImg = (ImageView)findViewById(R.id.add_info_img);
        addInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HistoryInfoActivity.this,InfoSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("nameInfo", "");
                bundle.putCharSequence("phoneInfo", "");
                bundle.putCharSequence("delieverplaceInfo", "");
                bundle.putCharSequence("index","-1");
                intent1.putExtras(bundle);
                startActivity(intent1);
                finish();
            }
        });
        initIView();
        initIEvent();
        initIData();
    }


    private void initIView() {
        recyclerView = (RecyclerView) findViewById(R.id.history_info_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(R.layout.history_info, null);
        recyclerView.setAdapter(myAdapter);
        inflater = getLayoutInflater();

    }

    private void initIEvent() {
    }

    private void initIData() {
//        for (int i = 0; i < 20; i++) {
//            HistoryInfo historyInfo=new HistoryInfo("name1","123","正心");
//            listData.add(historyInfo);
//            HistoryInfo historyInfo1 = new HistoryInfo("name2","456","格物");
//            listData.add(historyInfo1);
//        }
        myAdapter.addData(listData);
        myAdapter.notifyDataSetChanged();
    }


    public class MyAdapter extends BaseQuickAdapter<HistoryInfo, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<HistoryInfo> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final HistoryInfo item) {

            LinearLayout historyView=(LinearLayout)helper.getView(R.id.content);
            TextView nameInfo = (TextView)helper.getView(R.id.nameinfo);
            TextView phoneInfo = (TextView)helper.getView(R.id.phoneinfo);
            TextView delieverplaceInfo = (TextView)helper.getView(R.id.delieverplaceinfo);
//            editButton = (Button)helper.getView(edit_btn);
//            delButton =(Button)helper.getView(del_btn);
            nameInfo.setText(item.getName());
            phoneInfo.setText(item.getPhone());
            delieverplaceInfo.setText(item.getDeliverPlace());

            helper.getView(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(HistoryInfoActivity.this,InfoSettingActivity.class);
                    Bundle bundle = new Bundle();
                    //list 中的索引值
                    bundle.putCharSequence("index",String.valueOf(listData.indexOf(item)));
                    bundle.putCharSequence("nameInfo", item.getName());
                    bundle.putCharSequence("phoneInfo", item.getPhone());
                    bundle.putCharSequence("delieverplaceInfo", item.getDeliverPlace());
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    finish();
                }
            });
            helper.getView(R.id.del_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                   int index = listData.indexOf(item);
                    listData.remove(item);
                    JSONArray mJsonArray = new JSONArray();
                    for (int i = 0; i < listData.size(); i++) {
                        String userName = listData.get(i).getName();
                        String userPhone = listData.get(i).getPhone();
                        String userPlace = listData.get(i).getDeliverPlace();
                        Map<String, String> itemMap = new HashMap<>();

                        itemMap.put("name",userName);
                        itemMap.put("phone",userPhone);
                        itemMap.put("delieverPlace",userPlace);
                        Iterator<Map.Entry<String, String>> iterator = itemMap.entrySet().iterator();

                        JSONObject object = new JSONObject();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();
                            try {
                                object.put(entry.getKey(), entry.getValue());
                            } catch (JSONException e) {

                            }
                        }
                        mJsonArray.put(object);
                    }
                    SharedPreferences sp = getSharedPreferences("finals", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("history_info", mJsonArray.toString());
                    editor.commit();

                    Toast.makeText(HistoryInfoActivity.this,"delete",Toast.LENGTH_LONG).show();
//                    for (int i=0;i<listData.size()+1;i++){
//                        myAdapter.remove(i);
//                    }
                    myAdapter.mData.clear();
                    myAdapter.addData(listData);
                    myAdapter.notifyDataSetChanged();
                }
            });

            helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(HistoryInfoActivity.this,PublishNeedsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("nameInfo", item.getName());
                    bundle.putCharSequence("phoneInfo", item.getPhone());
                    bundle.putCharSequence("delieverplaceInfo", item.getDeliverPlace());
                    intent1.putExtras(bundle);
                    v.getContext().startActivity(intent1);
                    ((AppCompatActivity)mContext).finish();

                }
            });

        }

    }
}

