package com.example.administrator.sharedroute.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.example.administrator.sharedroute.activity.MainActivity.select;


public class PublishNeedsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    boolean left = true;        //标志选择的是左边的时间还是右边的时间
    int leftStatus = 0;       //标志左边选择的
    String leftDate;
    String leftTime;
    String rightDate;
    String rightTime;
    private int requestCode;
    private BottomNavigationView navigation;
    private DrawerLayout mDrawerLayout;
    private Button pickupLocationButton ;
    private TextView textViewName ;
//    private TextView textViewPhoneNumber ;
    private TextView sendLocation ;

    private TextView UserID;
    private TextView UserName;
    private TextView UserAccount;
    private TextWatcher textWatcher;

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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
                    .LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        pickupLocationButton = (Button)findViewById(R.id.pickupplace);
        textViewName = (TextView)findViewById(R.id.nametext);
//        textViewPhoneNumber = (TextView)findViewById(R.id.phonetext);
        sendLocation = (TextView)findViewById(R.id.delieverplace);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_publishneeds);
        setSupportActionBar(toolbar);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        /*
         *显示返回的收件地点信息
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }

        navigation = (BottomNavigationView) findViewById(R.id.publish_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);



        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);

        View nav_header_view = navView.getHeaderView(0);

        UserID = (TextView) nav_header_view.findViewById(R.id.nav_header_id);
        UserName = (TextView) nav_header_view.findViewById(R.id.nav_header_name);
        UserAccount = (TextView) nav_header_view.findViewById(R.id.nav_header_account);
        SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
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
        nameText = (TextView) findViewById(R.id.nametext);
        final Button pickupPlace;
        pickupPlace = (Button) findViewById(R.id.pickupplace);

        pickupPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, sendLocationActivity.class);
                requestCode=1;
                startActivityForResult(intent,requestCode);
            }
        });

        final TextView delieverPlace = (TextView) findViewById(R.id.delieverplace);

        final TextView qujiantext = (TextView) findViewById(R.id.qujiantext);
//        LinearLayout qujianshijan = (LinearLayout) findViewById(R.id.qujianshijian);

        final TextView songjianText = (TextView) findViewById(R.id.songjiantext);
//        LinearLayout songjianshijian = (LinearLayout) findViewById(R.id.songjianshijian);

        if (bundle != null && bundle.containsKey("nameInfo")) {
            nameText.setText(bundle.getString("nameInfo"));
            delieverPlace.setText(bundle.getString("delieverplaceInfo"));
        }

        LinearLayout cdv1 = (LinearLayout) findViewById(R.id.cdv1);
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

        final NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("书籍", "玩具", "化妆品",
                "电器","水果","零食","文具","日常用品","其他"));
        niceSpinner.attachDataSource(dataset);
//        String[] dataset={"书籍", "玩具", "化妆品", "电器"};
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataset);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        niceSpinner.setAdapter(adapter);

        ((LinearLayout)findViewById(R.id.pick_time_block)).setOnClickListener(new View.OnClickListener() {
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

        ((LinearLayout)findViewById(R.id.deliever_time_block)).setOnClickListener(new View.OnClickListener() {
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

        final EditText remarkText = (EditText) findViewById(R.id.remarktext);
        final EditText numText = (EditText) findViewById(R.id.numtext);
        final EditText money = (EditText) findViewById(R.id.money);

        final Button submitBtn = (Button) findViewById(R.id.submit_btn);
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
                for (int i=0;i<str.length();++i){
                    if (str.charAt(i)<'0' || str.charAt(i)>'9') {
                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                str = money.getText().toString();
                boolean hasDotYet = false;
                for (int i=0;i<str.length();++i) {
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
                bundle.putCharSequence("pickuptime", qujiantext.getText().toString());
                bundle.putCharSequence("delievertime", songjianText.getText().toString());
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
                ((TextView)findViewById(R.id.monitor_money)).setText("￥ "+money.getText().toString());
            }
        };
        money.addTextChangedListener(textWatcher);
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
        String date = "" + year + "年\n" + (++monthOfYear) + "月" + dayOfMonth+"日";

        if (left) {
            leftDate = date;
        } else {
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
        String time = "" + hourString + " : " + minuteString;
        if (left == true) {
            leftTime = time;
            ((TextView) findViewById(R.id.qujiantext)).setText(leftDate + "\n" + leftTime);
            ((TextView) findViewById(R.id.qujiantext)).setTextSize(12);
        } else {
            rightTime = time;
            ((TextView) findViewById(R.id.songjiantext)).setText(rightDate + "\n" + rightTime);
            ((TextView) findViewById(R.id.songjiantext)).setTextSize(12);
        }
    }
}
