package com.example.administrator.sharedroute.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.example.administrator.sharedroute.activity.MainActivity.select;

class NaiveDate{
    private int year;
    private int month;
    private int day;
    private String str;
    public NaiveDate(int year,int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
        str =  " " + year + "年\n" + (month) + "月" + day+"日";
    }

    public String getStr() {
        return str;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
class NaiveTime {
    private int hour;
    private int minute;
    private String str;
    public NaiveTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        str =   " " + hour + " : " + minute;;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

}
public class PublishNeedsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    boolean left = true;        //标志选择的是左边的时间还是右边的时间
    String leftDate;
    String leftTime;
    String rightDate;
    String rightTime;
    private NaiveTime naiveTimeLeft = null;
    private NaiveTime naiveTimeRight = null;
    private NaiveDate naiveDateLeft = null;
    private NaiveDate naiveDateRight = null;

    private int requestCode;
    private BottomNavigationView navigation;
    private DrawerLayout mDrawerLayout;
    private Button pickupLocationButton ;
    private TextView textViewName ;
//    private TextView textViewPhoneNumber ;
    private TextView sendLocation ;
    private ActionBarDrawerToggle mDrawerToggle;

    private TextView UserID;
    private TextView UserName;
    private TextView UserAccount;
    private TextWatcher textWatcher;
    private FetchUserInfo mFetchTask;
    private String usrid = "";
    private String usrphone = "";
    private double usraccount = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    JumpToActivity(MainActivity.class);
                    finish();
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    JumpToActivity(SearchNeedsActivity.class);
                    finish();
                    return true;
            }
            return false;
        }

    };

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_needs);
        if (!MainActivity.activityList.contains(PublishNeedsActivity.this)) MainActivity.activityList.add(PublishNeedsActivity.this);
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
        pickupLocationButton = findViewById(R.id.pickupplace);
        textViewName = findViewById(R.id.nametext);
//        textViewPhoneNumber = (TextView)findViewById(R.id.phonetext);
        sendLocation = findViewById(R.id.delieverplace);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_publishneeds);
        setSupportActionBar(toolbar);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        /*
         *显示返回的收件地点信息
         */
        SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
        usrid = sp.getString("now_stu_num", null);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }

        navigation = findViewById(R.id.publish_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (usrid != null) {
                    UserID.setText(MessageFormat.format("学号：{0}", usrid));
                    mFetchTask = new FetchUserInfo(usrid);
                    mFetchTask.execute();
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);

        View nav_header_view = navView.getHeaderView(0);

        UserID = nav_header_view.findViewById(R.id.nav_header_id);
        UserName = nav_header_view.findViewById(R.id.nav_header_name);
        UserAccount = nav_header_view.findViewById(R.id.nav_header_account);
        if (sp.getString("now_stu_num", null) != null) {
            UserID.setText(MessageFormat.format("学号：{0}", sp.getString("now_stu_num", null)));
        }
        UserName.setText(MessageFormat.format("电话：{0}", sp.getString("now_phone",null)));
        UserAccount.setText(MessageFormat.format("余额：{0}", sp.getString("now_account_money",null)));

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.nav_shop:
                        select = "releaseOrder";
                        Intent intent1 = new Intent(PublishNeedsActivity.this,TaskViewActivity.class);
                        intent1.putExtra("select_order",select);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_release:
                        select = "releaseOrder";
                        Intent intent2 = new Intent(PublishNeedsActivity.this,MyPublishOrder.class);
                        intent2.putExtra("select_order",select);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_receive:
                        select = "receiveOrder";
                        Intent intent3 = new Intent(PublishNeedsActivity.this,MyFinishedActivity.class);
                        intent3.putExtra("select_order",select);
                        startActivity(intent3);
                        return true;
                    case R.id.release_rank:
                        select = "releaseRank";
                        Intent intent4 = new Intent(PublishNeedsActivity.this,WaitingFutureActivity.class);
                        intent4.putExtra("select_order",select);
                        startActivity(intent4);
                        return true;
                    case R.id.receive_rank:
//                        select = "receiveRank";
                        Intent intent5 = new Intent(PublishNeedsActivity.this,WaitingFutureActivity.class);
//                        intent5.putExtra("select_order",select);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_wallet:
                        Intent intent6 = new Intent(PublishNeedsActivity.this,BugSendActivity.class);
                        startActivity(intent6);
                        return true;
                    case R.id.nav_setting:
                        Intent intent7 = new Intent(PublishNeedsActivity.this,WaitingFutureActivity.class);
                        startActivity(intent7);
                        return true;
                    case R.id.nav_login:
                        Intent intent8 = new Intent(PublishNeedsActivity.this,LoginActivity.class);
                        intent8.putExtra("from","homePage");
                        startActivity(intent8);
                        finish();
                        return true;
                    default:
                }
                return true;
            }
        });

        final TextView nameText;
        nameText = findViewById(R.id.nametext);
        final Button pickupPlace;
        pickupPlace = findViewById(R.id.pickupplace);

        pickupPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, sendLocationActivity.class);
                requestCode = 1;
                startActivityForResult(intent, requestCode);
            }
        });

        final TextView delieverPlace = findViewById(R.id.delieverplace);

        final TextView qujiantext = findViewById(R.id.qujiantext);
//        LinearLayout qujianshijan = (LinearLayout) findViewById(R.id.qujianshijian);

        final TextView songjianText = findViewById(R.id.songjiantext);
//        LinearLayout songjianshijian = (LinearLayout) findViewById(R.id.songjianshijian);

        if (bundle != null && bundle.containsKey("nameInfo")) {
            nameText.setText(bundle.getString("nameInfo"));
            delieverPlace.setText(bundle.getString("delieverplaceInfo"));
        }

        LinearLayout cdv1 = findViewById(R.id.cdv1);
        cdv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, HistoryInfoActivity.class);
                requestCode = 0;
                startActivityForResult(intent,requestCode);
            }
        });
        delieverPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, HistoryInfoActivity.class);
                requestCode = 0;
                startActivityForResult(intent,requestCode);
            }
        });

        if (delieverPlace.getText().toString().equals("")) {
            delieverPlace.setText("送件地点");
        }

        final NiceSpinner niceSpinner = findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("书籍", "玩具", "化妆品",
                "电器","水果","零食","文具","日常用品","其他"));
        niceSpinner.attachDataSource(dataset);
//        String[] dataset={"书籍", "玩具", "化妆品", "电器"};
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataset);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        niceSpinner.setAdapter(adapter);

        findViewById(R.id.pick_time_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = true;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PublishNeedsActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(false);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        findViewById(R.id.deliever_time_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = false;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PublishNeedsActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(false);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        final EditText remarkText = findViewById(R.id.remarktext);
        final EditText numText = findViewById(R.id.numtext);
        final EditText money = findViewById(R.id.money);
//        money.setFocusable(true);
        money.setFocusableInTouchMode(true);
//        money.requestFocus();
//        money.requestFocusFromTouch();
        InputMethodManager inputManager =
                (InputMethodManager)money.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(money, 0);
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(PublishNeedsActivity.this, "试运营期间，" +
                        "我们会安排专人为您代取派送快递，建议您设置金额为1-2元，如果您的任务紧急，" +
                        "可以适当提高金额，更高金额的任务会得到优先处理。",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 120);
                toast.show();
            }
        });

        final Button submitBtn = findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfDivide = nameText.getText().toString().indexOf('/');
                String testName = "";
                String testPhone = "";
                if (indexOfDivide > 1 && nameText.getText().toString().length() > indexOfDivide + 2) {
                    testName = nameText.getText().toString().substring(0, indexOfDivide - 1);
                    testPhone = nameText.getText().toString().substring(indexOfDivide + 2);
                }
                if (testName.equals("")||testPhone.equals("")||numText.getText().toString().equals("")
                        ||delieverPlace.getText().toString().equals("请选择")||pickupPlace.getText().toString().equals("请选择")||
                        ((TextView) findViewById(R.id.qujiantext)).getText().toString().equals("时间")||
                        ((TextView) findViewById(R.id.songjiantext)).getText().toString().equals("时间")||
                        money.getText().toString().equals("")){
                    Toast.makeText(PublishNeedsActivity.this,"请将信息填写完整",Toast.LENGTH_LONG).show();
                    return;
                }
                String str = testPhone;
                for (int i=0; i<str.length(); ++i){
                    if (str.charAt(i)<'0' || str.charAt(i)>'9') {
                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                str = money.getText().toString();
                boolean hasDotYet = false;
                for (int i=0; i<str.length(); ++i) {
                    if(str.charAt(i)=='.'&&(!hasDotYet)){
                        hasDotYet=true;
                    }else  if ((str.charAt(i)=='.'&&hasDotYet)) {
                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的金额",Toast.LENGTH_LONG).show();
                        return;
                    } else if (str.charAt(i)!='.'&&(str.charAt(i)<'0'||str.charAt(i)>'9')) {
                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的金额",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                ////////////////////////////
                if (!checkTime()) {
                    Toast.makeText(PublishNeedsActivity.this,"取件时间应早于送件时间",Toast.LENGTH_LONG).show();
                    return;
                }

                String fetchTime = qujiantext.getText().toString();
                int index1 = fetchTime.indexOf('日');
                fetchTime = fetchTime.substring(1,index1+2)+fetchTime.substring(index1+3);
                String delieverTime = songjianText.getText().toString();
                int index2 = delieverTime.indexOf('日');
                delieverTime = delieverTime.substring(1,index2+2)+delieverTime.substring(index2+3);

                Intent intent1 = new Intent(PublishNeedsActivity.this, PayBillActivity.class);
                Bundle bundle = new Bundle();
                int divideindex = nameText.getText().toString().indexOf('/');
                bundle.putCharSequence("remark",remarkText.getText().toString());
                bundle.putCharSequence("name", testName);
                bundle.putCharSequence("phone", testPhone);
                bundle.putCharSequence("packsort", niceSpinner.getText().toString());
                bundle.putCharSequence("remark", remarkText.getText().toString());
                bundle.putCharSequence("num", numText.getText().toString());
                bundle.putCharSequence("pickupplace", pickupPlace.getText().toString());
                bundle.putCharSequence("delieverplace", delieverPlace.getText().toString());
                bundle.putCharSequence("pickuptime", fetchTime);
                bundle.putCharSequence("delievertime", delieverTime);
                bundle.putCharSequence("money", money.getText().toString());
                bundle.putCharSequence("securitymoney",((EditText)findViewById(R.id.securitymoney)).getText().toString());
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((TextView)findViewById(R.id.monitor_money)).setText("￥ " + money.getText().toString());
            }
        };
        money.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        JumpToActivity(MainActivity.class);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:{
                if (data == null) break;
                String textName = data.getStringExtra("textName");
                String textPhone = data.getStringExtra("textPhone");
                String textDeliverPlace = data.getStringExtra("textDeliverPlace");
                textViewName.setText(textName + " / " + textPhone);
//                textViewPhoneNumber.setText(textPhone);
                sendLocation.setText(textDeliverPlace);
                break;
            }
            case 1:{
                if (data != null) {
                    ((Button)findViewById(R.id.pickupplace)).setText(data.getStringExtra("pickupLocation"));
                }
                break;
            }
            default:break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return true;
    }



    @Override
    public void onPause(){
        super.onPause();
        mDrawerLayout.closeDrawers();
    }

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        if (tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = " " + year + "年\n" + (++monthOfYear) + "月" + dayOfMonth+"日";

        if (left) {
            naiveDateLeft = new NaiveDate(year, ++monthOfYear, dayOfMonth);
            leftDate = date;
        } else {
            naiveDateRight = new NaiveDate(year, ++monthOfYear, dayOfMonth);
            rightDate = date;
        }

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                PublishNeedsActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.enableSeconds(false);
        tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = " " + hourString + " : " + minuteString;

        if (left == true) {
            leftTime = time;
            naiveTimeLeft = new NaiveTime(Integer.valueOf(hourString),Integer.valueOf(minuteString));
            ((TextView) findViewById(R.id.qujiantext)).setText(leftDate + "\n" + leftTime);
            ((TextView) findViewById(R.id.qujiantext)).setTextSize(12);
        } else {
            rightTime = time;
            naiveTimeRight = new NaiveTime(Integer.valueOf(hourString),Integer.valueOf(minuteString));
            ((TextView) findViewById(R.id.songjiantext)).setText(rightDate + "\n" + rightTime);
            ((TextView) findViewById(R.id.songjiantext)).setTextSize(12);
        }
    }

    private class FetchUserInfo extends AsyncTask<String, Void, Boolean> {

        private String id = null;
        private String result = null;

        FetchUserInfo(String UserID) {
            id = UserID;
        }

        @Override
        protected Boolean doInBackground(String... ID) {
            String result = null;
            String path = getResources().getString(R.string.url) + "/Task?action=FetchUserID&ID=" + id;
            HttpURLConnection con = null;
            InputStream in = null;

            try {
                URL url = new URL(path);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(5 * 1000);
                /*
                * http响应码：getResponseCode
                  200：成功 404：未找到 500：发生错误
              */
                if (con.getResponseCode() == 200) {
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
                    if (result != "fail") {
                        System.out.println(builder.toString());
                        JSONObject lan = new JSONObject(result);
                        usrphone = lan.getString("Phone");
                        usraccount = lan.getDouble("Account");
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (con != null) {
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
                UserName.setText(MessageFormat.format("电话：{0}", usrphone));
                UserAccount.setText(MessageFormat.format("余额：{0}", String.valueOf(usraccount)));

                SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);

                sp.edit().putString("now_account_money", String.valueOf(usraccount)).commit();
            } else {
                Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
        }
    }
    private boolean checkTime() {

        if ((naiveDateLeft.getYear() > naiveDateRight.getYear())||(naiveDateLeft.getMonth() >
        naiveDateRight.getMonth())||(naiveDateLeft.getDay() > naiveDateRight.getDay())||(naiveTimeLeft.getHour()
        > naiveTimeRight.getHour())||(naiveTimeLeft.getMinute() >= naiveTimeRight.getMinute()))
            return false;
        return true;
    }
}
