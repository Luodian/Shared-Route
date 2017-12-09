package com.example.administrator.sharedroute.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.net.SocketTimeoutException;
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

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_TIMEOUT = 5 * 1000;//设置请求超时5秒钟
    private static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟
    private static final int REQUEST_CODE_GO_TO_REGIST = 20;
    public static Socket socket;

    //    private static boolean stop;
//    public static void setStop(Boolean stop){
//        LoginActivity.stop=stop;
//    }
    public static BufferedReader in;
    public static PrintStream out;
    public static List<Activity> activityList = new ArrayList<Activity>();
    //socket 的 host 和 port
    private static MyThread thread;
//    private static final String HOST = "47.95.194.146";
//    private static final int PORT = 9986;
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
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mRegibtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        mEmailView = findViewById(R.id.user_edit_text);
//        mEmailView.setFocusable(false);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.pass_edit_text);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_form || id == EditorInfo.IME_NULL) {
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

        final Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
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
        mRegibtn = findViewById(R.id.regi_text);
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
//                            anotherSocket = new Socket(HOST,PORT);
                            anotherSocket = new Socket(getResources().getString(R.string.HOST), Integer.parseInt(getResources().getString(R.string.PORT)));
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
    public void onBackPressed() {
        finish();
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

    public void noti(String str) {
        String ticker = "";
        String content = "";
        if (str.contains("接收")) {
            ticker = "您发布的订单已被接收";
            int index = str.indexOf(',');
            content = str.substring(0, index);
        } else {
            ticker = "订单已成功送达";
            content = "您接收的订单已经被成功送达";
        }
        Notification notification = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.share_icon_withrectangle_background))
                .setSmallIcon(R.drawable.albule)
                .setTicker(ticker)
                .setContentTitle("恭喜您")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        //这里notify的id改为当前时间戳，实现多个notification排列显示，如果为一个常数，就是覆盖显示
        notificationManager.notify((int) System.currentTimeMillis(), notification);
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
//        private String url="http://47.95.194.146:8080/sharedroot_server/Login";
        private String url=getResources().getString(R.string.url)+"/Login";
        private String result = null;
        private Boolean network_flag = false;

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
                network_flag = true;

                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                if (result.equals("fail"))
                {
                    return false;
                }
                else
                {
                    client.getConnectionManager().shutdown();
                    return true;
                }
            }
            catch (SocketTimeoutException e)
            {
                network_flag = false;
                return false;
            }
            catch (IOException e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {


                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);

                sp.edit().putString("now_stu_num",mEmailView.getText().toString()).commit();
                if (result.contains("name:") && result.contains("phone:"))
                {
                    Toast.makeText(getApplicationContext(),"登录成功", Toast.LENGTH_SHORT).show();
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
                    Bundle mBundle = new Bundle();
                    mBundle.putString("ID",mEmailView.getText().toString());//压入数据
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                if (network_flag)
                {
                    if (!result .equals("outline")) {
                        Toast.makeText(getApplicationContext(), "登录失败，用户名和密码错误", Toast.LENGTH_SHORT).show();
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    } else {
                        Toast.makeText(getApplicationContext(), "用户已在别的设备在线", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.check_network_status, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    class MyThread extends  Thread{
        public void run() {
            try {
//                socket = new Socket(HOST,PORT);
                socket = new Socket(getResources().getString(R.string.HOST), Integer.parseInt(getResources().getString(R.string.PORT)));
                out = new PrintStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //向服务器发送学号
                out.println("action=login;name="+mEmailView.getText().toString());
                out.flush();

                //从服务器获取通知,由handler发送给主线程,之后保持这个线程贯穿程序始终

                String line = null;
                while ((!(socket.isClosed()))&&(line = in.readLine()) != null) {
                    if (line.equals("offline")) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getBaseContext());

                                builder.setIcon(R.drawable.share_icon_with_background);//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                                builder.setTitle("强制下线");
                                builder.setMessage("您的账号在别处登录");
                                builder.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Thread thread = new Thread() {
                                            public void run(){
                                                Socket anotherSocket = null;
                                                try {
                                                    LoginActivity.in.close();
                                                    LoginActivity.out.close();
                                                    LoginActivity.socket.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        thread.start();
                                        for (Activity a:activityList){
                                            if (a != null) a.finish();
                                        }
                                    }
                                        }
                                );
                                builder.show();
                    } else {
                        Log.e("line", line);
                        Message msg = new Message();
                        msg.what = 0x11;
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", line);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

