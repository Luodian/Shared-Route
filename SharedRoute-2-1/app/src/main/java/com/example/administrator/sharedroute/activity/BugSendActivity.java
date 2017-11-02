package com.example.administrator.sharedroute.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BugSendActivity extends AppCompatActivity {

    @BindView(R.id.bug_contact) EditText mcontact;
    @BindView(R.id.bug_info) EditText mBugInfo;
    private PostTask mPostTask;
    private String bugInfo;
    private String bugType;
    private String userID;
    private String contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_send);

        ButterKnife.bind(this);

        Spinner spinner = (Spinner) findViewById(R.id.bug_type);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] type = getResources().getStringArray(R.array.bugType);
//                Toast.makeText(BugSendActivity.this, "你点击的是:"+type[pos],Toast.LENGTH_SHORT).show();
                bugType = type[pos];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
        userID = sp.getString("now_stu_num",null);
        Button send_info = (Button)findViewById(R.id.bug_send_info);
        send_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bugInfo = String.valueOf(mBugInfo.getText());
                contact = String.valueOf(mcontact.getText());
                if (bugInfo != "" && contact != "")
                {
                    mPostTask = new PostTask();
                    mPostTask.execute();
                }
                else
                {
                    Toast.makeText(BugSendActivity.this, "请输入您遇见的问题或您的意见", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class PostTask extends AsyncTask<Void,Void,Boolean> {

        //        private String url = "http://47.95.194.146:8080/sharedroot_server/Task";
        private String url = "http://suc.free.ngrok.cc/sharedroot_server/Bugs";
        private String result = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                //参数
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                parameters.add(new BasicNameValuePair("UserID",userID));
                parameters.add(new BasicNameValuePair("ContactInfo",contact));
                parameters.add(new BasicNameValuePair("BugType",bugType));
                parameters.add(new BasicNameValuePair("BugInfo",bugInfo));
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
            mPostTask = null;

            if (success) {
                Toast.makeText(BugSendActivity.this,"感谢提交，我们会联系每位用户提供小礼品!", Toast.LENGTH_SHORT).show();
                Thread thread = new Thread() {
                    public void run(){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(BugSendActivity.this,MainActivity.class));
                        finish();
                    }
                };
                thread.start();
            }
            else
            {
                Toast.makeText(BugSendActivity.this,result.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mPostTask = null;

        }

    }
}
