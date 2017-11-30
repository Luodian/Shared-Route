package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.Client;
import com.nhaarman.listviewanimations.ArrayAdapter;

import java.util.ArrayList;

public class RankActivity extends AppCompatActivity {
    private ListView mListView;
    private SwipeRefreshLayout mSwipe;
    private ArrayList<Client> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
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
    }

    private void initView() {
        mListView = (ListView)findViewById(R.id.rand_list);
        mSwipe = (SwipeRefreshLayout)findViewById(R.id.rand_swipe);
        RankAdapter rankAdapter = new RankAdapter(RankActivity.this);
        rankAdapter.add(new Client("1150310116","李博",15,12));
        rankAdapter.add(new Client("1150310116","武德浩",14,11));
        rankAdapter.add(new Client("1150310116","王烨臻",13,11));
        mListView.setAdapter(rankAdapter);
    }
    private class RandAsysc extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
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