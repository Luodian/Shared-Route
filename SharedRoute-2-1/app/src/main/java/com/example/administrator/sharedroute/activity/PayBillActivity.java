package com.example.administrator.sharedroute.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.READ_CONTACTS;


public class PayBillActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>  {

    //测试使用，真实情况的时候，登录时缓存到本地，然后使用本地的学号
    private String stuNum;

    private String usrid = "";
    private String usrphone = "";
    private double usraccount = 0;
    private FetchUserInfo mFetchTask;
    private static final int REQUEST_READ_CONTACTS = 0;
    private PostTask mAuthTask = null;

    private TextView vertifyView;
    private View mProgressView;
    private View mPostInfoFormView;

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
        //没有测试，如果这样不可以的话，可以尝试ontick中发一个消息，然后handler进行timer.cancel()
        //测试成功
    }

    private CountDownTimer timer = new CountDownTimer(180000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            vertifyView.setText("(倒计时"+millisUntilFinished/1000 +"s)" );
        }

        @Override
        public void onFinish() {
            vertifyView.setEnabled(true);
            vertifyView.setText("时间已到");
            Toast.makeText(PayBillActivity.this,"时间已到，订单取消，即将返回",Toast.LENGTH_SHORT).show();
            Timer curTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            };
            curTimer.schedule(timerTask,2000);  //时间到了之后,2s后返回发布订单的页面
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);
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
        final Bundle bundle = intent.getExtras();

        vertifyView=(TextView)findViewById(R.id.timer);
        final TextView money = (TextView)findViewById(R.id.textView3);
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
        EditText total = (EditText)findViewById(R.id.textView3);

        total.setKeyListener(null);

        String fetchTime = bundle.getString("pickuptime");
        int index1 = fetchTime.indexOf('日');
        fetchTime = "\t\t\t\t"+fetchTime.substring(0,index1+1)+" "+fetchTime.substring(index1+2);
        String sendTime = bundle.getString("delievertime");
        int index2 = sendTime.indexOf('日');
        sendTime = "\t\t\t\t"+sendTime.substring(0,index2+1)+" "+sendTime.substring(index2+2);

        if(bundle!=null){
            money.setText("￥ "+bundle.getString("money"));
            name.setText(bundle.getString("name"));
            phone.setText(bundle.getString("phone"));
            packSort.setText(bundle.getString("packsort"));
            pickNum.setText(bundle.getString("num"));
            pickPlace.setText(bundle.getString("pickupplace"));
            delieverPlace.setText(bundle.getString("delieverplace"));
            pickTime.setText(fetchTime);
            delieverTime.setText(sendTime);
            ensureBillBtn.setText("确认支付 ￥ "+bundle.getString("money"));
        }


        mPostInfoFormView = findViewById(R.id.pay_form);
        mProgressView = findViewById(R.id.pay_progress);

        ensureBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
                String stuNum = sp.getString("now_stu_num", null);
                FetchUserInfo fetchUserInfo = new FetchUserInfo(stuNum);
                fetchUserInfo.execute();

            }
        });
    }
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void postInfo() {
        if (mAuthTask != null) {
            return;
        }
        showProgress(true);
        String payPath = "";
        RadioButton zhifubao = (RadioButton)findViewById(R.id.zhifubao);
        RadioButton wechat = (RadioButton)findViewById(R.id.wechat);
        if (zhifubao.isChecked()){
            payPath = "zhifubao";
        } else if (wechat.isChecked()) {
            payPath="wechat";
        }
        SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
        stuNum=sp.getString("now_stu_num",null);
        Bundle bundle = getIntent().getExtras();
        mAuthTask = new PostTask(bundle.getString("money"),bundle.getString("name"),
                bundle.getString("phone"),bundle.getString("num"),bundle.getString("packsort"),
                bundle.getString("pickupplace"),bundle.getString("delieverplace"),
                bundle.getString("pickuptime"),bundle.getString("delievertime"),
                payPath,bundle.getString("remark"),stuNum,bundle.getString("securitymoney"));
        mAuthTask.execute((Void) null);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPostInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPostInfoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPostInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPostInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
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
        private String mStuNum;                 //学号
        private String mSecurityMoney;          //保证金

//        private String url = "http://47.95.194.146:8080/sharedroot_server/Task";
//        private String url = "http://suc.free.ngrok.cc/sharedroot_server/Task";
//        private String url="http://47.95.194.146:8080/sharedroot_server/Task";
        private String url = getResources().getString(R.string.url)+"/Task";
        private String result = null;

        //初始化
        PostTask(String money,String name,String phone,String num,String packSort,
                 String pickPlace,String delieverPlace,String pickTime,String delieverTime,
                 String payPath,String remark,String stuNum,String securityMoney) {
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

            mStuNum=stuNum;
            mSecurityMoney=securityMoney;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                //参数
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                parameters.add(new BasicNameValuePair("Money",mMoney));
                parameters.add(new BasicNameValuePair("Name",mName));
                parameters.add(new BasicNameValuePair("Phone",mPhone));
                parameters.add(new BasicNameValuePair("PickID",mNum));
                parameters.add(new BasicNameValuePair("TaskKindID",mPackSort));
                parameters.add(new BasicNameValuePair("FetchLocation",mPickPlace));
                parameters.add(new BasicNameValuePair("SendLocation",mDelieverPlace));
                parameters.add(new BasicNameValuePair("FetchTime",mPickTime));
                parameters.add(new BasicNameValuePair("SendTime",mDelieverTime));
                int whichPay;
                if (mPayPath.equals("zhifubao")){
                    whichPay=0;
                }
                else if(mPayPath.equals("wechat")){
                    whichPay=1;
                }else {
                    whichPay=2;         //虚拟货币
                }
                parameters.add(new BasicNameValuePair("WhichPay",String.valueOf(whichPay)));
                parameters.add(new BasicNameValuePair("Remark",mRemark));
                parameters.add(new BasicNameValuePair("PublisherID",mStuNum));
                parameters.add(new BasicNameValuePair("PromiseMoney",mSecurityMoney));
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
                new AlertDialog.Builder(PayBillActivity.this)
                        .setIcon(R.drawable.share_icon_with_background)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                        .setTitle("支付成功")
                        .setMessage("即将返回主页")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(PayBillActivity.this, MainActivity.class));
                                finish();
                            }
                        }).show();
//                Toast.makeText(getApplicationContext(),"支付成功，即将返回主页!", Toast.LENGTH_SHORT).show();
//                Thread thread = new Thread() {
//                    public void run(){
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        startActivity(new Intent(PayBillActivity.this,MainActivity.class));
//                        finish();
//                    }
//                };
//                thread.start();
            }
            else
            {
                Toast.makeText(getApplicationContext(),result.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    public class FetchUserInfo extends AsyncTask<String, Void, Boolean> {

        private String id = null;
        private String result = null;

        FetchUserInfo(String UserID) {
            id = UserID;
        }

        @Override
        protected Boolean doInBackground(String... ID) {
            String result = null;
            String path = getResources().getString(R.string.url)+"/Task?action=FetchUserID&ID=" + id;
            HttpURLConnection con=null;
            InputStream in=null;

            try
            {
                URL url=new URL(path);
                con= (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5*1000);
                con.setReadTimeout(5*1000);
                /*
                * http响应码：getResponseCode
                  200：成功 404：未找到 500：发生错误
              */
                if (con.getResponseCode()==200)
                {
                    System.out.println("连接成功");
                    in = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    //InputStreamReader isr = new InputStreamReader(getAssets().open("get_data.json"),"UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    //StringBuilder 缓存区 StringBuffer
                    StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        builder.append(line);
                    }
                    br.close();
                    isr.close();
                    result = builder.toString();
                    if (result != "fail")
                    {
                        System.out.println(builder.toString());
                        JSONObject lan = new JSONObject(result);
                        usrphone = lan.getString("Phone");
                        usraccount = lan.getDouble("Account");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (in!=null){
                    try
                    {
                        in.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (con!=null){
                    con.disconnect();
                    //断开连接
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFetchTask = null;
            if (success) {
                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);

                sp.edit().putString("now_account_money",String.valueOf(usraccount)).commit();
                String moneyStr = ((EditText)findViewById(R.id.textView3)).getText().toString();
                moneyStr = moneyStr.substring(2);
                if (usraccount < Double.valueOf(moneyStr)) {
                    new AlertDialog.Builder(PayBillActivity.this)
                            .setIcon(R.drawable.share_icon_with_background)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                            .setTitle("余额不足")
                            .setMessage("试运营期间，暂不支持直接充值，如有需要请联系我们~")
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(PayBillActivity.this, ContactUsActivity.class));
                                    //跳转到扫二维码充值的页面

                                }
                            }).show();
                } else
                    postInfo();
            } else {
                Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
        }
    }
}
