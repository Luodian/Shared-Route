package com.example.administrator.sharedroute.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    //socket 的 host 和 port
    private static MyThread thread;

    public static Socket socket;
    public static BufferedReader in;
    public static PrintStream out;

    //    private static boolean stop;
//    public static void setStop(Boolean stop){
//        LoginActivity.stop=stop;
//    }

    private static final String HOST = "free.ngrok.cc";
    private static final int PORT = 12974;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x11:
                    noti(msg.getData().getString("msg"));
                    break;
                default:
                    break;
            }
        }
    };

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时5秒钟
    private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mRegibtn;
    private static final int REQUEST_CODE_GO_TO_REGIST = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        TextView userText = (TextView)findViewById(R.id.user_text);
//        TextView passText = (TextView)findViewById(R.id.pass_text);
//        EditText userEditText = (EditText)findViewById(R.id.user_edit_text);
//        EditText passEditText = (EditText)findViewById(R.id.pass_edit_text);
//        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/youyuan.TTF");
//        userText.setTypeface(typeFace);
//        passText.setTypeface(typeFace);
//        userEditText.setTypeface(typeFace);
//        passEditText.setTypeface(typeFace);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.user_edit_text);
//        mEmailView.setFocusable(false);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.pass_edit_text);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                //本来是login
                if (id == R.id.email_login_form || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        final SharedPreferences sp = getSharedPreferences("logininfo", MODE_PRIVATE);

        String result = sp.getString("login_info", "");
        String logInName="";
        String loginPassword="";
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
                    logInName = itemMap.get("stuNum");
                    loginPassword= itemMap.get("password");
                    break;
                }
            }
            mEmailView.setText(logInName);
            mPasswordView.setText(loginPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEmailView.getText().equals(""))
                {
                    JSONArray mJsonArray = new JSONArray();
                    String stuNum = mEmailView.getText().toString();
                    String passWord = mPasswordView.getText().toString();
                    Map<String, String> itemMap = new HashMap<>();

                    itemMap.put("stuNum",stuNum);
                    itemMap.put("password",passWord);
                    Iterator<Map.Entry<String, String>> iterator = itemMap.entrySet().iterator();

                    JSONObject object = new JSONObject();

                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        try {
                            object.put(entry.getKey(), entry.getValue());
                        } catch (JSONException ignored) {

                        }
                    }
                    mJsonArray.put(object);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("login_info", mJsonArray.toString());
                    editor.apply();
                }
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mRegibtn = (TextView)findViewById(R.id.regi_text);
        mRegibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,REQUEST_CODE_GO_TO_REGIST);
            }
        });

        //如果条件满足，就自动登录
        if (sp.contains("login_info")&&(!(getIntent().hasExtra("from"))||!(getIntent().getStringExtra("from").equals("homePage")))){
            if ((!mEmailView.getText().equals(""))&&(!mPasswordView.getText().equals(""))){
                attemptLogin();
            }
        }else if((getIntent().hasExtra("from"))&&(getIntent().getStringExtra("from").equals("homePage")))
        {
                Thread thread = new Thread() {
                    public void run(){
                        Socket anotherSocket = null;
                        try {
                            anotherSocket = new Socket(HOST,PORT);
                            PrintStream out1 = new PrintStream(anotherSocket.getOutputStream());
                            out1.println("action=send;name="+mEmailView.getText().toString()+";msg=byebye");
                            out1.flush();
                            out1.close();
                            anotherSocket.close();

                            in.close();
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==REQUEST_CODE_GO_TO_REGIST && resultCode==RESULT_OK){
            mEmailView.setText(data.getStringExtra("name"));
            mPasswordView.setText(data.getStringExtra("password"));
        }
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
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
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


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
//            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
//        private String url = "http://47.95.194.146:8080/sharedroot_server/Login";
//        private String url = "http://suc.free.ngrok.cc/sharedroot_server/Login";
        private String url="http://47.95.194.146:8080/sharedroot_server/Login";
        private String result = null;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
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


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (!Objects.equals(mEmail, "") && !Objects.equals(mPassword, "")){
                        List<NameValuePair> parameters = new ArrayList<>();
                        parameters.add(new BasicNameValuePair("UserID", mEmail));
                        parameters.add(new BasicNameValuePair("Password", mPassword));
                        parameters.add(new BasicNameValuePair("action", "login"));
                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                        post.setEntity(ent);
                    }

                }
                HttpResponse responsePOST = client.execute(post);

                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                if (result.equals("fail")){
                    return false;
                }
                else
                {
                    client.getConnectionManager().shutdown();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                Toast.makeText(getApplicationContext(),"登录成功", Toast.LENGTH_SHORT).show();

                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);

                sp.edit().putString("now_stu_num",mEmailView.getText().toString()).commit();
                if (result.contains("name:") && result.contains("phone:"))
                {
                    String now_name = result.substring(result.indexOf("name:") + 5, result.indexOf(",phone"));
                    String now_phone = result.substring(result.indexOf("phone:") + 6);

                    Log.e("name:", now_name);
                    Log.e("phone:", now_phone);
                    sp.edit().putString("now_name", now_name).commit();
                    sp.edit().putString("now_phone", now_phone).commit();

                    //启动接收命令的线程
                    thread = new MyThread();
                    thread.start();
//                new MyThread().start();

                    //开始新界面
                }
                Bundle mBundle = new Bundle();
                mBundle.putString("ID",mEmailView.getText().toString());//压入数据
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "登录失败，用户名和密码错误", Toast.LENGTH_SHORT).show();
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    //获取通知使用的线程
    class MyThread extends  Thread{
        public void run(){
            try {
                socket = new Socket(HOST,PORT);
                out = new PrintStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //向服务器发送学号
                out.println("action=login;name="+mEmailView.getText().toString());
                out.flush();

                //从服务器获取通知,由handler发送给主线程,之后保持这个线程贯穿程序始终

                String line = null;
                while ((!(socket.isClosed()))&&(line = in.readLine()) != null) {

                    Log.e("line",line);
                    Message msg = new Message();
                    msg.what = 0x11;
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", line);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
//                //停止监听线程
//                if (stop){
//                    out.println("action=login;name="+mEmailView.getText().toString()+";"+"msg:Bye bye!");
//                    out.flush();
//                    in.close();
//                    out.close();
//                    socket.close();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void noti(String str){

        Notification notification = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.banner_1))
                .setSmallIcon(R.drawable.banner_2)
                .setTicker("您的订单已被接收")
                .setContentTitle("恭喜您")
                .setContentText("您编号为"+str+"已经被接收")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1,notification);
    }
}

