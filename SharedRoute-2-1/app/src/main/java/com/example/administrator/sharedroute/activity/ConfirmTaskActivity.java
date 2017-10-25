package com.example.administrator.sharedroute.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.ConfirmTaskAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
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

import java.io.IOException;
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
    private OrderDao orderDao;
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
                return true;
        }
        return true;
    }



    private void initView(){
        listView=(ListView)findViewById(R.id.listViewFirmOrders);
        mInformation = (LinearLayout) findViewById(R.id.informationlayout);
        orderDao = new OrderDao(this);
        mProgressView = findViewById(R.id.login_progress);
        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        itemlists = bundle.getParcelableArrayList("listItemList");
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
                List<listItem> listItemList = adapter.getItems();
                FlipShareView shareBottom = new FlipShareView.Builder(ConfirmTaskActivity.this, mCardView)
                        .addItem(new ShareItem("类型："+listItemList.get(position).getExpressType(), Color.WHITE, 0xff43549C))
                        .addItem(new ShareItem("描述："+listItemList.get(position).getExpressSize(), Color.WHITE, 0xff4999F0))
                        .addItem(new ShareItem("取件时间："+listItemList.get(position).getInTimeStamp(), Color.WHITE, 0xffD9392D))
                        .addItem(new ShareItem("取件地点："+listItemList.get(position).getInLocation(), Color.WHITE, 0xff57708A))
                        .addItem(new ShareItem("送件时间："+listItemList.get(position).getOutTimeStamp(), Color.WHITE, 0xffea0bb2))
                        .addItem(new ShareItem("送件地点："+listItemList.get(position).getOutLocation(), Color.WHITE, 0xffea650b))
                        .addItem(new ShareItem("价格："+listItemList.get(position).getPrice(), Color.WHITE,0xff063e04))
                        .setItemDuration(250)
                        .setBackgroundColor(0x60000000)
                        .setAnimType(FlipShareView.TYPE_SLIDE)
                        .create();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemlists = adapter.getItems();
                ArrayList<listItem> listElected = new ArrayList<listItem>();
                for (listItem e:itemlists) {
                    listElected.add(e);
                }
                attemptLogin();
                //将这个listElected传给下一个
                Intent intent =new Intent(ConfirmTaskActivity.this,ConfirmFinishedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listItemList",listElected);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
//
//        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);
//
//        // Store values at the time of the login attempt.
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();

        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
           // focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(itemlists);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mInformation.setVisibility(show ? View.GONE : View.VISIBLE);
            mInformation.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInformation.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mInformation.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost("http://suc.free.ngrok.cc/sharedroot_server/Login");
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(_queryKey, _queryValue));
//        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//        post.setEntity(ent);

        private String url = "http://suc.free.ngrok.cc/sharedroot_server/Task";

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
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                HttpClient client = new DefaultHttpClient();

                HttpPost post = new HttpPost(url);

                //參數
                if (arraylist.size() != 0){
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                    parameters.add(new BasicNameValuePair("listRequest", "还未设置"));
                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                    post.setEntity(ent);
                }

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
                Toast.makeText(ConfirmTaskActivity.this,"Successful!", Toast.LENGTH_SHORT).show();
//                finish();
            }
            else
            {
                Toast.makeText(ConfirmTaskActivity.this,result.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        List<listItem> arrayList = adapter.getItems();
        for (int position : reverseSortedPositions) {
            adapter.remove(position);
            orderDao.deleteOrder(arrayList.get(position));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back_to_main,menu);
        return true;
    }
}
