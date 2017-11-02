package com.example.administrator.sharedroute.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean isSuccess = false;

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时5秒钟
    private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
    private PostTask mAuthTask = null;

    private View mProgressView;
    private View mAttemptRegisterFormView;

    private EditText  mName;
    private EditText  mPhone;
    private EditText  mStuNum;
    private EditText  mInviteCode;
    private EditText  mPassWord;
    private EditText  mVerifyPassword;
    private Button mRegisterBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_register);
        setSupportActionBar(toolbar);

        mName = (EditText)findViewById(R.id.regi_name);
        mPhone = (EditText)findViewById(R.id.regi_phone);
        mStuNum = (EditText)findViewById(R.id.regi_stu_num);
        mInviteCode = (EditText)findViewById(R.id.regi_invite_code);
        mPassWord = (EditText)findViewById(R.id.regi_password);
        mVerifyPassword = (EditText)findViewById(R.id.verify_password);

        mRegisterBtn = (Button)findViewById(R.id.regi_ensure_btn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mPassWord.getText().toString();
                String verify = mVerifyPassword.getText().toString();
                if (!(password.equals(verify))){
                    Toast.makeText(RegisterActivity.this,"buyizhia",Toast.LENGTH_SHORT).show();
                    mVerifyPassword.setError("密码不一致");
                    mVerifyPassword.requestFocus();
                }else {
                    attemptRegister();
                    if (isSuccess) {
//                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Thread thread = new Thread() {
                            public void run(){
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent data = new Intent();
                                data.putExtra("name", mName.getText().toString());
                                data.putExtra("phone", mPhone.getText().toString());
                                data.putExtra("stuNum", mStuNum.getText().toString());
                                data.putExtra("inviteCode", mInviteCode.getText().toString());
                                data.putExtra("password", mPassWord.getText().toString());
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        };
                        thread.start();
                    }
                }
            }
        });

        mAttemptRegisterFormView = findViewById(R.id.regiser_form);
        mProgressView = findViewById(R.id.register_progress);

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


    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }
        showProgress(true);

        mAuthTask = new PostTask(mName.getText().toString(),mPhone.getText().toString(),
                mStuNum.getText().toString(),mInviteCode.getText().toString(),mPassWord.getText().toString());
        mAuthTask.execute((Void) null);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAttemptRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAttemptRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAttemptRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAttemptRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegisterActivity.ProfileQuery.PROJECTION,

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
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return true;
    }

    private class PostTask extends AsyncTask<Void,Void,Boolean> {
        private String name;
        private String stuNum;
        private String phone;
        private String inviteCode;
        private String password;

//        private String url = "http://47.95.194.146:8080/sharedroot_server/Login";
//        private String url = "http://suc.free.ngrok.cc/sharedroot_server/Login";
private String url="http://hitschool.free.ngrok.cc/sharedroot_server/Login";

        private String result = null;

        //初始化
        PostTask(String name,String stuNum,String phone,String inviteCode,String password){
            this.name=name;
            this.stuNum = stuNum;
            this.phone=phone;
            this.inviteCode=inviteCode;
            this.password=password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {

                BasicHttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
                HttpClient client = new DefaultHttpClient(httpParams);
                HttpPost post = new HttpPost(url);

                //参数
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                parameters.add(new BasicNameValuePair("Name",this.name));
                parameters.add(new BasicNameValuePair("UserID",this.stuNum));
                parameters.add(new BasicNameValuePair("Account",this.phone));
                parameters.add(new BasicNameValuePair("ID",this.inviteCode));
                parameters.add(new BasicNameValuePair("Password",this.password));
                parameters.add(new BasicNameValuePair("action", "registe"));           //数据库应该建表

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

                Toast.makeText(RegisterActivity.this,"注册成功", Toast.LENGTH_SHORT).show();
                isSuccess = true;
//                finish();
            }
            else
            {
                Toast.makeText(RegisterActivity.this,"注册失败,详细信息为\n"+ result.toString(), Toast.LENGTH_SHORT).show();
                isSuccess=false;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

}
