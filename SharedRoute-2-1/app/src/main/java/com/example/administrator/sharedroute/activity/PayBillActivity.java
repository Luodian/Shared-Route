package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;


public class PayBillActivity extends AppCompatActivity {

    private TextView vertifyView;
    private CountDownTimer timer = new CountDownTimer(180000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            vertifyView.setText("(倒计时"+millisUntilFinished/1000 +"s)" );
        }

        @Override
        public void onFinish() {
            vertifyView.setEnabled(true);
            vertifyView.setText("时间已到");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        vertifyView=(TextView)findViewById(R.id.timer);
        TextView money = (TextView)findViewById(R.id.textView3);
        TextView name=(TextView)findViewById(R.id.textView4);
        TextView phone = (TextView)findViewById(R.id.textView7);
        TextView packSort= (TextView)findViewById(R.id.textView8);
        TextView pickNum = (TextView)findViewById(R.id.textView9);
        Button pickPlace=(Button)findViewById(R.id.button);
        Button delieverPlace = (Button)findViewById(R.id.button2);
        TextView pickTime =(TextView)findViewById(R.id.pick_time);
        TextView delieverTime=(TextView)findViewById(R.id.deliever_time);
        Button ensureBillBtn = (Button)findViewById(R.id.ensure_bill_btn);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("确认支付");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        timer.start();
        EditText total = (EditText) findViewById(R.id.totalEdit);

        total.setKeyListener(null);

        if(bundle!=null){
            money.setText("￥ "+bundle.getString("money"));
            name.setText(bundle.getString("name"));
            phone.setText(bundle.getString("phone"));
            packSort.setText(bundle.getString("packsort"));
            pickNum.setText(bundle.getString("num"));
            pickPlace.setText(bundle.getString("pickupplace"));
            delieverPlace.setText(bundle.getString("delieverplace"));
            pickTime.setText(bundle.getString("pickuptime"));
            delieverTime.setText(bundle.getString("delievertime"));
            ensureBillBtn.setText("确认支付 ￥ "+bundle.getString("money"));
        }



    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.back:
                startActivity(new Intent(PayBillActivity.this,MainActivity.class));
                return true;
        }
        return true;
    }
}
