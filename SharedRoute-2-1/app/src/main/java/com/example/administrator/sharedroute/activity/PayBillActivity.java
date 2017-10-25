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

    private class PostTask extends AsyncTask<Void,Void,Boolean> {
        private String mMoney;
        private String mName;
        private String mPhone;
        private String mNum;
        private String mPackSort;
        private String mPickPlace;
        private String mDelieverPlace;
        private String mPickTime;
        private String mDelieverTime;
        private String mPayPath;                //支付方式指明是支付宝支付还是微信支付
        private String mRemark;                 //备注

        private String url = "http://suc.free.ngrok.cc/sharedroot_server/Task";

        private String result = null;

        //初始化
        PostTask(String money,String name,String phone,String num,String packSort,
                    String pickPlace,String delieverPlace,String pickTime,String delieverTime,
                    String payPath,String remark) {
            mMoney=money;
            mName=name;
            mPhone=phone;
            mNum=num;
            mPackSort=packSort;
            mPickPlace=pickPlace;
            mDelieverPlace=delieverPlace;
            mPickTime=pickTime;
            mDelieverTime=delieverTime;
            mPayPath=payPath;
            mRemark=remark;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                //参数
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                parameters.add(new BasicNameValuePair("money",mMoney));
                parameters.add(new BasicNameValuePair("name",mName));
                parameters.add(new BasicNameValuePair("phone",mPhone));
                parameters.add(new BasicNameValuePair("num",mNum));
                parameters.add(new BasicNameValuePair("packSort",mPackSort));
                parameters.add(new BasicNameValuePair("pickPlace",mPickPlace));
                parameters.add(new BasicNameValuePair("delieverPlace",mDelieverPlace));
                parameters.add(new BasicNameValuePair("pickTime",mPickTime));
                parameters.add(new BasicNameValuePair("delieverTime",mDelieverTime));
                parameters.add(new BasicNameValuePair("payPath",mPayPath));
                parameters.add(new BasicNameValuePair("remark",mRemark));
                parameters.add(new BasicNameValuePair("action", "submit"));

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse responsePOST = client.execute(post);

                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                if (result.toString().equals("success"))
                {
                    client.getConnectionManager().shutdown();
                    return true;
                }
                else
                {
                    return false;
                }
            } catch (IOException e) {
                // TODO: handle exception
                e.getMessage();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(PayBillActivity.this,"Successful!", Toast.LENGTH_SHORT).show();
//                finish();
            }
            else
            {
                Toast.makeText(PayBillActivity.this,result.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(PayBillActivity.this,"Failure", Toast.LENGTH_SHORT).show();
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

}
