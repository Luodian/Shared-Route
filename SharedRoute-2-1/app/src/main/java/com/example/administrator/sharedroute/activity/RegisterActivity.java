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
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.utils.CheckFetcherUtil;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时5秒钟
    private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
    private boolean isSuccess = false;
    private int REQUEST_CODE_GO_TO_REGIST = 20;
    private int resultCodeFromRegister = 21;
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
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
                    .LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mName = findViewById(R.id.regi_name);
        mPhone = findViewById(R.id.regi_phone);
        mStuNum = findViewById(R.id.regi_stu_num);
        mInviteCode = findViewById(R.id.regi_invite_code);
        mPassWord = findViewById(R.id.regi_password);
        mVerifyPassword = findViewById(R.id.verify_password);

        mRegisterBtn = findViewById(R.id.regi_ensure_btn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mPassWord.getText().toString();
                String verify = mVerifyPassword.getText().toString();
                String  curStuNum = mStuNum.getText().toString();
                String name = mName.getText().toString();
                String phone = mPhone.getText().toString();
                String stu_num = mStuNum.getText().toString();
                String invite_num = mInviteCode.getText().toString();
                String verify_pass = mVerifyPassword.getText().toString();
                String regex=".*[a-zA-Z]+.*";
                Matcher m= Pattern.compile(regex).matcher(curStuNum);
                if (name.equals("") || phone.equals("") || stu_num.equals("") || invite_num.equals("") || password.equals("") || verify_pass.equals("")) {
                    Toast.makeText(getApplicationContext(),"请将信息填写完整",Toast.LENGTH_SHORT).show();
                } else if (!m.matches()) {
                    if (CheckFetcherUtil.isTheIDValid(curStuNum)) {
                        attemptRegister();
                    } else if (curStuNum.length() != 10) {
                        Toast.makeText(getApplicationContext(), "请验证您的学号是否输入正确~", Toast.LENGTH_SHORT).show();
                    } else if (!(password.equals(verify))) {
                        Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                        mVerifyPassword.setError("密码不一致");
                        mVerifyPassword.requestFocus();
                    }
                    //普通用户注册
                    else {
                        attemptRegister();
                    }
                } else {
                    if (curStuNum.length() != 10) {
                        Toast.makeText(getApplicationContext(), "请验证您的学号是否输入正确~", Toast.LENGTH_SHORT).show();
                    } else if (!(password.equals(verify))) {
                        Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                        mVerifyPassword.setError("密码不一致");
                        mVerifyPassword.requestFocus();
                    }
                    //普通用户注册
                    else {
                        attemptRegister();
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
        return checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
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

        mAuthTask = new PostTask(mName.getText().toString(), mStuNum.getText().toString(),mPhone.getText().toString(),mInviteCode.getText().toString(),mPassWord.getText().toString());
        mAuthTask.execute((Void) null);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class PostTask extends AsyncTask<Void,Void,Boolean> {
        private String name;
        private String stuNum;
        private String phone;
        private String inviteCode;
        private String password;
//        private String url="http://47.95.194.146:8080/sharedroot_server/Login";
        private String url = getResources().getString(R.string.url)+"/Login";
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
                parameters.add(new BasicNameValuePair("Phone",this.phone));
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
                Toast.makeText(getApplicationContext(),"注册成功", Toast.LENGTH_SHORT).show();
                isSuccess = true;
//                finish();
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                Intent data = new Intent();
                data.putExtra("name", mName.getText().toString());
                data.putExtra("phone", mPhone.getText().toString());
                data.putExtra("stuNum", mStuNum.getText().toString());
                data.putExtra("inviteCode", mInviteCode.getText().toString());
                data.putExtra("password", mPassWord.getText().toString());
                setResult(resultCodeFromRegister, data);
                finish();
            }
            else
            {
                if (result.equals("exist"))
                {
                    Toast.makeText(getApplicationContext(),"注册失败,该账户已经存在", Toast.LENGTH_SHORT).show();
                    isSuccess=false;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"注册失败,详细信息为\n"+ result.toString(), Toast.LENGTH_SHORT).show();
                    isSuccess=false;
                }
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
